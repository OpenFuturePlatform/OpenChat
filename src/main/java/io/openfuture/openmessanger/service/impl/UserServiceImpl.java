package io.openfuture.openmessanger.service.impl;

import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;
import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.SMS_MFA;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminListUserAuthEventsResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.UserType;

import io.openfuture.openmessanger.exception.UserNotFoundException;
import io.openfuture.openmessanger.service.CognitoUserService;
import io.openfuture.openmessanger.service.UserService;
import io.openfuture.openmessanger.service.dto.AuthenticatedChallengeRequest;
import io.openfuture.openmessanger.service.dto.LoginRequest;
import io.openfuture.openmessanger.service.dto.LoginSmsVerifyRequest;
import io.openfuture.openmessanger.service.dto.UserPasswordUpdateRequest;
import io.openfuture.openmessanger.service.dto.UserSignUpRequest;
import io.openfuture.openmessanger.service.response.UserResponse;
import io.openfuture.openmessanger.web.response.AuthenticatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final CognitoUserService cognitoUserService;

    private ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();

    @Override
    public AuthenticatedResponse authenticate(LoginRequest userLogin) {

        AdminInitiateAuthResult result = cognitoUserService.initiateAuth(userLogin.getEmail(), userLogin.getPassword())
                                                           .orElseThrow(() -> new UserNotFoundException(String.format("Username %s  not found.", userLogin.getEmail())));

        if (ObjectUtils.nullSafeEquals(NEW_PASSWORD_REQUIRED.name(), result.getChallengeName())) {
            AuthenticatedChallengeRequest.builder()
                                                .challengeType(NEW_PASSWORD_REQUIRED.name())
                                                .sessionId(result.getSession())
                                                .username(userLogin.getEmail())
                                                .build();
        }

        if (ObjectUtils.nullSafeEquals(SMS_MFA.name(), result.getChallengeName())) {
            AuthenticatedChallengeRequest.builder().challengeType(SMS_MFA.name())
                                                .sessionId(result.getSession())
                                                .username(userLogin.getEmail()).build();
        }

        users.put(result.getAuthenticationResult().getAccessToken(), userLogin.getEmail());
        return AuthenticatedResponse.builder()
                .accessToken(result.getAuthenticationResult().getAccessToken())
                .idToken(result.getAuthenticationResult().getIdToken())
                .refreshToken(result.getAuthenticationResult().getRefreshToken())
                .username(userLogin.getEmail())
                .build();
    }

    @Override
    public AuthenticatedResponse authenticateSms(final LoginSmsVerifyRequest loginSmsVerifyRequest) {
        final AdminRespondToAuthChallengeResult result = cognitoUserService.respondToAuthSmsChallenge(loginSmsVerifyRequest.getUsername(),
                                                                                                      loginSmsVerifyRequest.getSms(),
                                                                                                      loginSmsVerifyRequest.getSessionId()).get();

        return AuthenticatedResponse.builder()
                                    .accessToken(result.getAuthenticationResult().getAccessToken())
                                    .idToken(result.getAuthenticationResult().getIdToken())
                                    .refreshToken(result.getAuthenticationResult().getRefreshToken())
                                    .username(loginSmsVerifyRequest.getUsername())
                                    .build();
    }

    @Override
    public AuthenticatedResponse updateUserPassword(UserPasswordUpdateRequest userPassword) {

        AdminRespondToAuthChallengeResult result =
                cognitoUserService.respondToAuthChallenge(userPassword.getUsername(), userPassword.getPassword(), userPassword.getSessionId()).get();

        return AuthenticatedResponse.builder()
                                    .accessToken(result.getAuthenticationResult().getAccessToken())
                                    .idToken(result.getAuthenticationResult().getIdToken())
                                    .refreshToken(result.getAuthenticationResult().getRefreshToken())
                                    .username(userPassword.getUsername())
                                    .build();
    }

    @Override
    public void logout(@NotNull String accessToken) {
        cognitoUserService.signOut(accessToken);
    }

    @Override
    public ForgotPasswordResult userForgotPassword(String username) {
        return cognitoUserService.forgotPassword(username);
    }

    @Override
    public UserType createUser(final UserSignUpRequest signUpDTO) {
        return cognitoUserService.signUp(signUpDTO);
    }

    @Override
    public AdminListUserAuthEventsResult userAuthEvents(String username, int maxResult, String nextToken) {
        return cognitoUserService.getUserAuthEvents(username, maxResult, nextToken);
    }

    @Override
    public UserResponse getCurrent(final String token) {
        final String email = users.get(token);
        final AdminGetUserResult userDetails = cognitoUserService.getUserDetails(email);

        final List<AttributeType> userAttributes = userDetails.getUserAttributes();

        return new UserResponse(userDetails.getUsername(),
                         get("given_name", userAttributes).getValue(),
                         get("family_name", userAttributes).getValue(),
                         get("email", userAttributes).getValue(),
                                "",
                                "",
                                "");
    }

    AttributeType get(String name, List<AttributeType> attributeTypes) {
        return attributeTypes.stream().filter(attributeType -> attributeType.getName().equals(name)).findFirst().get();
    }

}
