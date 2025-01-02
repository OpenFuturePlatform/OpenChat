package io.openfuture.openmessenger.service.impl

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.*
import com.amazonaws.services.cognitoidp.model.UsernameExistsException
import io.openfuture.openmessenger.configuration.AwsConfig
import io.openfuture.openmessenger.domain.enums.CognitoAttributesEnum
import io.openfuture.openmessenger.exception.*
import io.openfuture.openmessenger.exception.InvalidParameterException
import io.openfuture.openmessenger.exception.InvalidPasswordException
import io.openfuture.openmessenger.exception.UserNotFoundException
import io.openfuture.openmessenger.service.CognitoUserService
import io.openfuture.openmessenger.service.dto.UserSignUpRequest
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.apache.logging.log4j.util.Strings
import org.passay.CharacterData
import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Consumer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@RequiredArgsConstructor
@Slf4j
@Service
class CognitoUserServiceImpl(
    val awsCognitoIdentityProvider: AWSCognitoIdentityProvider,
    val awsConfig: AwsConfig
) : CognitoUserService {

    companion object{
        val log: Logger = LoggerFactory.getLogger(CognitoUserServiceImpl::class.java)
    }
    
    override fun signUp(request: UserSignUpRequest): UserType? {
        validatePassword(request.password!!)
        return try {
            val signUpRequest = AdminCreateUserRequest()
                .withUserPoolId(awsConfig.cognito.userPoolId)
                .withTemporaryPassword(generateValidPassword())
                .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .withUsername(request.email)
                .withMessageAction(MessageActionType.SUPPRESS)
                .withUserAttributes(
                    AttributeType().withName("given_name").withValue(request.firstName),
                    AttributeType().withName("family_name").withValue(request.lastName),
                    AttributeType().withName("email").withValue(request.email),
                    AttributeType().withName("email_verified").withValue("true")
                )
            val createUserResult = awsCognitoIdentityProvider.adminCreateUser(signUpRequest)
            log.info("Created User id: {}, email: {}", createUserResult.user.username, request.email)
            request.roles?.forEach(Consumer { r: String? -> addUserToGroup(request.email, r) })
            setUserPassword(request.email, request.password)
            createUserResult.user
        } catch (e: UsernameExistsException) {
            //throw UsernameExistsException("User name that already exists")
            log.info("User Exists: {}", e.errorMessage)
            val userType = UserType()
            userType.username = request.email
            return userType
        } catch (e: com.amazonaws.services.cognitoidp.model.InvalidPasswordException) {
            throw InvalidPasswordException("Invalid password.", e)
        }
    }

    override fun addUserToGroup(username: String?, groupName: String?) {
        try {
            val addUserToGroupRequest = AdminAddUserToGroupRequest()
                .withGroupName(groupName)
                .withUserPoolId(awsConfig.cognito.userPoolId)
                .withUsername(username)
            awsCognitoIdentityProvider.adminAddUserToGroup(addUserToGroupRequest)
        } catch (e: com.amazonaws.services.cognitoidp.model.InvalidPasswordException) {
            throw FailedAuthenticationException(String.format("Invalid parameter: %s", e.errorMessage), e)
        }
    }

    override fun setUserPassword(username: String?, password: String?): AdminSetUserPasswordResult? {
        return try {
            val adminSetUserPasswordRequest = AdminSetUserPasswordRequest()
                .withUsername(username)
                .withPassword(password)
                .withUserPoolId(awsConfig.cognito.userPoolId)
                .withPermanent(true)
            awsCognitoIdentityProvider.adminSetUserPassword(adminSetUserPasswordRequest)
        } catch (e: com.amazonaws.services.cognitoidp.model.InvalidPasswordException) {
            throw FailedAuthenticationException(String.format("Invalid parameter: %s", e.errorMessage), e)
        }
    }

    override fun initiateAuth(email: String, password: String): Optional<AdminInitiateAuthResult> {
        val authParams: MutableMap<String, String> = HashMap()
        authParams[CognitoAttributesEnum.USERNAME.name] = email
        authParams[CognitoAttributesEnum.PASSWORD.name] = password
        authParams[CognitoAttributesEnum.SECRET_HASH.name] = calculateSecretHash(awsConfig.cognito.appClientId!!, awsConfig.cognito.appClientSecret!!, email)
        val authRequest = AdminInitiateAuthRequest()
            .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
            .withClientId(awsConfig.cognito.appClientId)
            .withUserPoolId(awsConfig.cognito.userPoolId)
            .withAuthParameters(authParams)
        return try {
            Optional.of(awsCognitoIdentityProvider.adminInitiateAuth(authRequest))
        } catch (e: NotAuthorizedException) {
            throw FailedAuthenticationException(String.format("Authenticate failed: %s", e.errorMessage), e)
        } catch (e: com.amazonaws.services.cognitoidp.model.UserNotFoundException) {
            val username = authRequest.authParameters[CognitoAttributesEnum.USERNAME.name]
            throw UserNotFoundException(String.format("Username %s  not found.", username), e)
        }
    }

    override fun respondToAuthChallenge(
        username: String?, newPassword: String?, session: String?
    ): Optional<AdminRespondToAuthChallengeResult> {
        val request = AdminRespondToAuthChallengeRequest()
        request.withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
            .withUserPoolId(awsConfig.cognito.userPoolId)
            .withClientId(awsConfig.cognito.appClientId)
            .withSession(session)
            .addChallengeResponsesEntry("userAttributes.name", "aek")
            .addChallengeResponsesEntry(CognitoAttributesEnum.USERNAME.name, username)
            .addChallengeResponsesEntry(CognitoAttributesEnum.NEW_PASSWORD.name, newPassword)
            .addChallengeResponsesEntry(
                CognitoAttributesEnum.SECRET_HASH.name, calculateSecretHash(
                    awsConfig.cognito.appClientId!!,
                    awsConfig.cognito.appClientSecret!!, username
                )
            )
        return try {
            Optional.of(awsCognitoIdentityProvider!!.adminRespondToAuthChallenge(request))
        } catch (e: NotAuthorizedException) {
            throw NotAuthorizedException("User not found." + e.errorMessage)
        } catch (e: com.amazonaws.services.cognitoidp.model.UserNotFoundException) {
            throw UserNotFoundException("User not found.", e)
        } catch (e: com.amazonaws.services.cognitoidp.model.InvalidPasswordException) {
            throw InvalidPasswordException("Invalid password.", e)
        }
    }

    override fun respondToAuthSmsChallenge(
        username: String?, smsCode: String?, session: String?
    ): Optional<AdminRespondToAuthChallengeResult> {
        val request = AdminRespondToAuthChallengeRequest()
        request.withChallengeName(ChallengeNameType.SMS_MFA)
            .withUserPoolId(awsConfig.cognito.userPoolId)
            .withClientId(awsConfig.cognito.appClientId)
            .withSession(session)
            .addChallengeResponsesEntry("userAttributes.name", "aek")
            .addChallengeResponsesEntry(CognitoAttributesEnum.USERNAME.name, username)
            .addChallengeResponsesEntry(CognitoAttributesEnum.SMS_MFA_CODE.name, smsCode)
            .addChallengeResponsesEntry(
                CognitoAttributesEnum.SECRET_HASH.name,
                calculateSecretHash(awsConfig.cognito.appClientId!!, awsConfig.cognito.appClientSecret!!, username)
            )
        return try {
            Optional.of(awsCognitoIdentityProvider.adminRespondToAuthChallenge(request))
        } catch (e: NotAuthorizedException) {
            throw NotAuthorizedException("User not found." + e.errorMessage)
        } catch (e: com.amazonaws.services.cognitoidp.model.UserNotFoundException) {
            throw UserNotFoundException("User not found.", e)
        }
    }

    override fun getUserAuthEvents(username: String?, maxResult: Int, nextToken: String?): AdminListUserAuthEventsResult? {
        return try {
            val userAuthEventsRequest = AdminListUserAuthEventsRequest()
            userAuthEventsRequest.username = username
            userAuthEventsRequest.userPoolId = awsConfig.cognito.userPoolId
            userAuthEventsRequest.maxResults = maxResult
            if (Strings.isNotBlank(nextToken)) {
                userAuthEventsRequest.nextToken = nextToken
            }
            awsCognitoIdentityProvider.adminListUserAuthEvents(userAuthEventsRequest)
        } catch (e: InternalErrorException) {
            throw InternalErrorException(e.errorMessage)
        } catch (e: com.amazonaws.services.cognitoidp.model.InvalidParameterException) {
            throw InvalidParameterException(String.format("Amazon Cognito service encounters an invalid parameter %s", e.errorMessage), e)
        } catch (e: UserPoolAddOnNotEnabledException) {
            throw InvalidParameterException(String.format("Amazon Cognito service encounters an invalid parameter %s", e.errorMessage), e)
        }
    }

    override fun signOut(accessToken: String?): GlobalSignOutResult? {
        return try {
            awsCognitoIdentityProvider.globalSignOut(GlobalSignOutRequest().withAccessToken(accessToken))
        } catch (e: NotAuthorizedException) {
            throw FailedAuthenticationException(String.format("Logout failed: %s", e.errorMessage), e)
        }
    }

    override fun getUserDetails(cognitoId: String?): AdminGetUserResult? {
        return awsCognitoIdentityProvider
            .adminGetUser(
                AdminGetUserRequest().withUsername(cognitoId)
                    .withUserPoolId(awsConfig.cognito.userPoolId)
            )
    }

    override fun forgotPassword(username: String?): ForgotPasswordResult? {
        return try {
            val request = ForgotPasswordRequest()
            request.withClientId(awsConfig.cognito.appClientId)
                .withUsername(username)
                .withSecretHash(calculateSecretHash(awsConfig.cognito.appClientId!!, awsConfig.cognito.appClientSecret!!, username))
            awsCognitoIdentityProvider.forgotPassword(request)
        } catch (e: NotAuthorizedException) {
            throw FailedAuthenticationException(String.format("Forgot password failed: %s", e.errorMessage), e)
        }
    }

    override fun refreshAccessToken(userId: String?, refreshToken: String?): AuthenticationResultType? {
        val authParams = HashMap<String, String?>()
        authParams[CognitoAttributesEnum.REFRESH_TOKEN.name] = refreshToken
        authParams[CognitoAttributesEnum.USERNAME.name] = userId
        authParams[CognitoAttributesEnum.SECRET_HASH.name] = calculateSecretHash(awsConfig.cognito.appClientId!!, awsConfig.cognito.appClientSecret!!, userId)
        val authRequest = AdminInitiateAuthRequest()
            .withAuthFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
            .withUserPoolId(awsConfig.cognito.userPoolId)
            .withClientId(awsConfig.cognito.appClientId)
            .withAuthParameters(authParams)
        val authResult = awsCognitoIdentityProvider.adminInitiateAuth(authRequest)
        return authResult.authenticationResult
    }

    private fun calculateSecretHash(userPoolClientId: String, userPoolClientSecret: String, userName: String?): String {
        val HMAC_SHA256_ALGORITHM = "HmacSHA256"
        val signingKey = SecretKeySpec(
            userPoolClientSecret.toByteArray(StandardCharsets.UTF_8),
            HMAC_SHA256_ALGORITHM
        )
        return try {
            val mac = Mac.getInstance(HMAC_SHA256_ALGORITHM)
            mac.init(signingKey)
            mac.update(userName!!.toByteArray(StandardCharsets.UTF_8))
            val rawHmac = mac.doFinal(userPoolClientId.toByteArray(StandardCharsets.UTF_8))
            Base64.getEncoder().encodeToString(rawHmac)
        } catch (e: Exception) {
            throw ServiceException("Error while calculating ", e)
        }
    }

    private fun validatePassword(password: String) {
        if (!StringUtils.hasText(password) && password.length < 8) {
            throw InvalidPasswordException("Password is too weak")
        }
        //        String regExpn = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
//
//        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(password);
//        if (!matcher.matches()) {
//            throw new InvalidPasswordException("Password is too weak");
//        }
    }

    private fun generateValidPassword(): String {
        val gen = PasswordGenerator()
        val lowerCaseChars: CharacterData = EnglishCharacterData.LowerCase
        val lowerCaseRule = CharacterRule(lowerCaseChars)
        lowerCaseRule.numberOfCharacters = 2
        val upperCaseChars: CharacterData = EnglishCharacterData.UpperCase
        val upperCaseRule = CharacterRule(upperCaseChars)
        upperCaseRule.numberOfCharacters = 2
        val digitChars: CharacterData = EnglishCharacterData.Digit
        val digitRule = CharacterRule(digitChars)
        digitRule.numberOfCharacters = 2
        val specialChars: CharacterData = object : CharacterData {
            override fun getErrorCode(): String {
                return "ERRONEOUS_SPECIAL_CHARS"
            }

            override fun getCharacters(): String {
                return "!@#$%^&*()_+"
            }
        }
        val splCharRule = CharacterRule(specialChars)
        splCharRule.numberOfCharacters = 2
        return gen.generatePassword(
            10, splCharRule, lowerCaseRule,
            upperCaseRule, digitRule
        )
    }
}