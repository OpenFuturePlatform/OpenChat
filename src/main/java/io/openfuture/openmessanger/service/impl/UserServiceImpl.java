package io.openfuture.openmessanger.service.impl;

import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;
import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.SMS_MFA;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminListUserAuthEventsResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
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
import io.openfuture.openmessanger.web.response.AuthenticatedResponse;
import io.openfuture.openmessanger.web.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final CognitoUserService cognitoUserService;


    @Override
    public BaseResponse authenticate(LoginRequest userLogin) {

        AdminInitiateAuthResult result = cognitoUserService.initiateAuth(userLogin.getUsername(), userLogin.getPassword())
                                                           .orElseThrow(() -> new UserNotFoundException(String.format("Username %s  not found.", userLogin.getUsername())));

        if (ObjectUtils.nullSafeEquals(NEW_PASSWORD_REQUIRED.name(), result.getChallengeName())) {
            return new BaseResponse(AuthenticatedChallengeRequest.builder()
                                                                 .challengeType(NEW_PASSWORD_REQUIRED.name())
                                                                 .sessionId(result.getSession())
                                                                 .username(userLogin.getUsername())
                                                                 .build(), "First time login - Password change required", false);
        }

        if (ObjectUtils.nullSafeEquals(SMS_MFA.name(), result.getChallengeName())) {
            return new BaseResponse(AuthenticatedChallengeRequest.builder()
                                                                 .challengeType(SMS_MFA.name())
                                                                 .sessionId(result.getSession())
                                                                 .username(userLogin.getUsername())
                                                                 .build(), "We sent a code to your phone number", false);
        }

        return new BaseResponse(AuthenticatedResponse.builder()
                                                     .accessToken(result.getAuthenticationResult().getAccessToken())
                                                     .idToken(result.getAuthenticationResult().getIdToken())
                                                     .refreshToken(result.getAuthenticationResult().getRefreshToken())
                                                     .username(userLogin.getUsername())
                                                     .build(), "Login successful", false);
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
    public UserType createUser(UserSignUpRequest signUpDTO) {
        return cognitoUserService.signUp(signUpDTO);
    }

    @Override
    public AdminListUserAuthEventsResult userAuthEvents(String username, int maxResult, String nextToken) {
        return cognitoUserService.getUserAuthEvents(username, maxResult, nextToken);
    }
}
