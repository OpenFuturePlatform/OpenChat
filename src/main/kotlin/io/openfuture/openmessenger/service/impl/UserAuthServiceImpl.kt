package io.openfuture.openmessenger.service.impl

import com.amazonaws.services.cognitoidp.model.AdminListUserAuthEventsResult
import com.amazonaws.services.cognitoidp.model.AttributeType
import com.amazonaws.services.cognitoidp.model.ChallengeNameType
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult
import io.openfuture.openmessenger.exception.UserNotFoundException
import io.openfuture.openmessenger.repository.UserJpaRepository
import io.openfuture.openmessenger.repository.entity.User
import io.openfuture.openmessenger.service.CognitoUserService
import io.openfuture.openmessenger.service.UserAuthService
import io.openfuture.openmessenger.service.dto.*
import io.openfuture.openmessenger.service.response.UserResponse
import io.openfuture.openmessenger.web.response.AuthenticatedResponse
import lombok.extern.slf4j.Slf4j
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.util.ObjectUtils
import javax.validation.constraints.NotNull

@Slf4j
@Service
class UserAuthServiceImpl(
    val userJpaRepository: UserJpaRepository,
    val cognitoUserService: CognitoUserService
) : UserAuthService {

    override fun authenticate(userLogin: LoginRequest): AuthenticatedResponse {
        val result = cognitoUserService.initiateAuth(userLogin.email!!, userLogin.password!!)
            .orElseThrow { UserNotFoundException(String.format("Username %s  not found.", userLogin.email)) }
        if (ObjectUtils.nullSafeEquals(ChallengeNameType.NEW_PASSWORD_REQUIRED.name, result!!.challengeName)) {
            AuthenticatedChallengeRequest(result.session, userLogin.email, ChallengeNameType.NEW_PASSWORD_REQUIRED.name)
        }
        if (ObjectUtils.nullSafeEquals(ChallengeNameType.SMS_MFA.name, result.challengeName)) {
            AuthenticatedChallengeRequest(result.session, userLogin.email, ChallengeNameType.SMS_MFA.name)
        }
        return AuthenticatedResponse(
            userLogin.email,
            result.authenticationResult.accessToken,
            result.authenticationResult.idToken,
            result.authenticationResult.refreshToken
        )
    }

    override fun refreshToken(request: RefreshTokenRequest): AuthenticatedResponse {
        val current = current()
        val result = cognitoUserService.refreshAccessToken(current.id, request.refreshToken)
        return AuthenticatedResponse(
            current.email,
            result?.accessToken,
            result?.idToken,
            result?.refreshToken
        )
    }

    override fun authenticateSms(loginSmsVerifyRequest: LoginSmsVerifyRequest): AuthenticatedResponse {
        val result = cognitoUserService.respondToAuthSmsChallenge(
            loginSmsVerifyRequest.username,
            loginSmsVerifyRequest.sms,
            loginSmsVerifyRequest.sessionId
        ).get()
        return AuthenticatedResponse(
            current().email,
            result.authenticationResult.accessToken,
            result.authenticationResult.idToken,
            result.authenticationResult.refreshToken
        )
    }

    override fun logout(accessToken: @NotNull String?) {
        cognitoUserService.signOut(accessToken)
    }

    override fun userForgotPassword(username: String?): ForgotPasswordResult? {
        return cognitoUserService.forgotPassword(username)
    }

    override fun createUser(signUpDTO: UserSignUpRequest) {
        cognitoUserService.signUp(signUpDTO)
        val user = User(signUpDTO.email, signUpDTO.firstName, signUpDTO.lastName)
        user.email = signUpDTO.email
        user.firstName = signUpDTO.firstName
        user.lastName = signUpDTO.lastName
        println("request: $signUpDTO and user: $user")
        userJpaRepository.save(user)
    }

    override fun userAuthEvents(username: String?, maxResult: Int, nextToken: String?): AdminListUserAuthEventsResult? {
        return cognitoUserService.getUserAuthEvents(username, maxResult, nextToken)
    }

    override fun current(): UserResponse {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.principal as String
        val userDetails = cognitoUserService.getUserDetails(userId)
        val userAttributes = userDetails!!.userAttributes
        return UserResponse(
            userDetails.username,
            get("given_name", userAttributes).value,
            get("family_name", userAttributes).value,
            get("email", userAttributes).value,
            "",
            "",
            ""
        )
    }

    operator fun get(name: String, attributeTypes: List<AttributeType>): AttributeType {
        return attributeTypes.stream().filter { attributeType: AttributeType -> attributeType.name == name }.findFirst().get()
    }

}