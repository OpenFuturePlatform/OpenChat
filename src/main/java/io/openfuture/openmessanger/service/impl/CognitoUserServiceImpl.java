package io.openfuture.openmessanger.service.impl;

import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;
import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.SMS_MFA;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.util.Strings;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import io.openfuture.openmessanger.configuration.AwsConfig;
import io.openfuture.openmessanger.domain.enums.CognitoAttributesEnum;
import io.openfuture.openmessanger.exception.FailedAuthenticationException;
import io.openfuture.openmessanger.exception.ServiceException;
import io.openfuture.openmessanger.service.CognitoUserService;
import io.openfuture.openmessanger.service.dto.UserSignUpRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class CognitoUserServiceImpl implements CognitoUserService {

    private final AWSCognitoIdentityProvider awsCognitoIdentityProvider;

    private final AwsConfig awsConfig;


    @Override
    public UserType signUp(UserSignUpRequest signUpDTO) {

        try {
            final AdminCreateUserRequest signUpRequest = new AdminCreateUserRequest()
                    .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                    .withTemporaryPassword(generateValidPassword())
                    .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                    .withUsername(signUpDTO.getEmail())
                    .withMessageAction(MessageActionType.SUPPRESS)
                    .withUserAttributes(
                            new AttributeType().withName("name").withValue(signUpDTO.getFirstName()),
                            new AttributeType().withName("email").withValue(signUpDTO.getEmail()),
                            new AttributeType().withName("email_verified").withValue("true"),
                            new AttributeType().withName("phone_number").withValue(signUpDTO.getPhoneNumber()),
                            new AttributeType().withName("phone_number_verified").withValue("true"));

            AdminCreateUserResult createUserResult = awsCognitoIdentityProvider.adminCreateUser(signUpRequest);
            log.info("Created User id: {}", createUserResult.getUser().getUsername());

            signUpDTO.getRoles().forEach(r -> addUserToGroup(signUpDTO.getEmail(), r));

            setUserPassword(signUpDTO.getEmail(), signUpDTO.getPassword());

            return createUserResult.getUser();

        } catch (com.amazonaws.services.cognitoidp.model.UsernameExistsException e) {
            throw new UsernameExistsException("User name that already exists");
        } catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
            throw new io.openfuture.openmessanger.exception.InvalidPasswordException("Invalid password.", e);
        }

    }

    @Override
    public void addUserToGroup(String username, String groupName) {

        try {
            // add user to group
            AdminAddUserToGroupRequest addUserToGroupRequest = new AdminAddUserToGroupRequest()
                    .withGroupName(groupName)
                    .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                    .withUsername(username);

            awsCognitoIdentityProvider.adminAddUserToGroup(addUserToGroupRequest);
        } catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
            throw new FailedAuthenticationException(String.format("Invalid parameter: %s", e.getErrorMessage()), e);
        }
    }

    @Override
    public AdminSetUserPasswordResult setUserPassword(String username, String password) {

        try {
            AdminSetUserPasswordRequest adminSetUserPasswordRequest = new AdminSetUserPasswordRequest()
                    .withUsername(username)
                    .withPassword(password)
                    .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                    .withPermanent(true);

            return awsCognitoIdentityProvider.adminSetUserPassword(adminSetUserPasswordRequest);
        } catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
            throw new FailedAuthenticationException(String.format("Invalid parameter: %s", e.getErrorMessage()), e);
        }
    }


    @Override
    public Optional<AdminInitiateAuthResult> initiateAuth(String username, String password) {

        final Map<String, String> authParams = new HashMap<>();
        authParams.put(CognitoAttributesEnum.USERNAME.name(), username);
        authParams.put(CognitoAttributesEnum.PASSWORD.name(), password);
        authParams.put(CognitoAttributesEnum.SECRET_HASH.name(), calculateSecretHash(awsConfig.getCognito().getAppClientId(), awsConfig.getCognito().getAppClientSecret(), username));


        final AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withClientId(awsConfig.getCognito().getAppClientId())
                .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                .withAuthParameters(authParams);

        return adminInitiateAuthResult(authRequest);
    }

    @Override
    public Optional<AdminRespondToAuthChallengeResult> respondToAuthChallenge(
            String username, String newPassword, String session) {
        AdminRespondToAuthChallengeRequest request = new AdminRespondToAuthChallengeRequest();
        request.withChallengeName(NEW_PASSWORD_REQUIRED)
                .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                .withClientId(awsConfig.getCognito().getAppClientId())
                .withSession(session)
                .addChallengeResponsesEntry("userAttributes.name", "aek")
                .addChallengeResponsesEntry(CognitoAttributesEnum.USERNAME.name(), username)
                .addChallengeResponsesEntry(CognitoAttributesEnum.NEW_PASSWORD.name(), newPassword)
                .addChallengeResponsesEntry(CognitoAttributesEnum.SECRET_HASH.name(), calculateSecretHash(awsConfig.getCognito().getAppClientId(),
                                                                                                          awsConfig.getCognito().getAppClientSecret(), username));

        try {
            return Optional.of(awsCognitoIdentityProvider.adminRespondToAuthChallenge(request));
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException("User not found." + e.getErrorMessage());
        } catch (UserNotFoundException e) {
            throw new io.openfuture.openmessanger.exception.UserNotFoundException("User not found.", e);
        } catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
            throw new io.openfuture.openmessanger.exception.InvalidPasswordException("Invalid password.", e);
        }
    }

    @Override
    public Optional<AdminRespondToAuthChallengeResult> respondToAuthSmsChallenge(
            String username, String smsCode, String session) {
        AdminRespondToAuthChallengeRequest request = new AdminRespondToAuthChallengeRequest();
        request.withChallengeName(SMS_MFA)
                .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                .withClientId(awsConfig.getCognito().getAppClientId())
                .withSession(session)
                .addChallengeResponsesEntry("userAttributes.name", "aek")
                .addChallengeResponsesEntry(CognitoAttributesEnum.USERNAME.name(), username)
                .addChallengeResponsesEntry(CognitoAttributesEnum.SMS_MFA_CODE.name(), smsCode)
                .addChallengeResponsesEntry(CognitoAttributesEnum.SECRET_HASH.name(), calculateSecretHash(awsConfig.getCognito().getAppClientId(), awsConfig.getCognito().getAppClientSecret(), username));

        try {
            return Optional.of(awsCognitoIdentityProvider.adminRespondToAuthChallenge(request));
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException("User not found." + e.getErrorMessage());
        } catch (UserNotFoundException e) {
            throw new io.openfuture.openmessanger.exception.UserNotFoundException("User not found.", e);
        }
    }

    @Override
    public AdminListUserAuthEventsResult getUserAuthEvents(String username, int maxResult, String nextToken) {
        try {

            AdminListUserAuthEventsRequest userAuthEventsRequest = new AdminListUserAuthEventsRequest();
            userAuthEventsRequest.setUsername(username);
            userAuthEventsRequest.setUserPoolId(awsConfig.getCognito().getUserPoolId());
            userAuthEventsRequest.setMaxResults(maxResult);
            if (Strings.isNotBlank(nextToken)) {
                userAuthEventsRequest.setNextToken(nextToken);
            }

            return awsCognitoIdentityProvider.adminListUserAuthEvents(userAuthEventsRequest);
        } catch (InternalErrorException e) {
            throw new InternalErrorException(e.getErrorMessage());
        } catch (InvalidParameterException | UserPoolAddOnNotEnabledException e) {
            throw new io.openfuture.openmessanger.exception.InvalidParameterException(String.format("Amazon Cognito service encounters an invalid parameter %s", e.getErrorMessage()), e);
        }
    }


    @Override
    public GlobalSignOutResult signOut(String accessToken) {
        try {
            return awsCognitoIdentityProvider.globalSignOut(new GlobalSignOutRequest().withAccessToken(accessToken));
        } catch (NotAuthorizedException e) {
            throw new FailedAuthenticationException(String.format("Logout failed: %s", e.getErrorMessage()), e);
        }
    }

    @Override
    public ForgotPasswordResult forgotPassword(String username) {
        try {
            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.withClientId(awsConfig.getCognito().getAppClientId())
                    .withUsername(username)
                    .withSecretHash(calculateSecretHash(awsConfig.getCognito().getAppClientId(), awsConfig.getCognito().getAppClientSecret(), username));

            return awsCognitoIdentityProvider.forgotPassword(request);

        } catch (NotAuthorizedException e) {
            throw new FailedAuthenticationException(String.format("Forgot password failed: %s", e.getErrorMessage()), e);
        }
    }

    private Optional<AdminInitiateAuthResult> adminInitiateAuthResult(AdminInitiateAuthRequest request) {
        try {
            return Optional.of(awsCognitoIdentityProvider.adminInitiateAuth(request));
        } catch (NotAuthorizedException e) {
            throw new FailedAuthenticationException(String.format("Authenticate failed: %s", e.getErrorMessage()), e);
        } catch (UserNotFoundException e) {
            String username = request.getAuthParameters().get(CognitoAttributesEnum.USERNAME.name());
            throw new io.openfuture.openmessanger.exception.UserNotFoundException(String.format("Username %s  not found.", username), e);
        }
    }

    private String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new ServiceException("Error while calculating ", e);
        }
    }

    private String generateValidPassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "ERRONEOUS_SPECIAL_CHARS";
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        return gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
    }

}
