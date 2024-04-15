package io.openfuture.openmessanger.service.impl;

import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;
import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.SMS_MFA;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminListUserAuthEventsResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.UnauthorizedException;

import io.openfuture.openmessanger.exception.UserNotFoundException;
import io.openfuture.openmessanger.repository.UserJpaRepository;
import io.openfuture.openmessanger.repository.entity.User;
import io.openfuture.openmessanger.service.CognitoUserService;
import io.openfuture.openmessanger.service.UserAuthService;
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
public class UserAuthServiceImpl implements UserAuthService {

    private final UserJpaRepository userJpaRepository;
    private final CognitoUserService cognitoUserService;

    private ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>() {{
        put("eyJraWQiOiJIOTQrUmNYdWdobGZUc0JLcDRcL0taQkZ4MmhRY09PUHM3eCt1SThZam1Tcz0iLCJhbGciOiJSUzI1NiJ9" +
                    ".eyJzdWIiOiJmZGQ3MDI5YS05MjY5LTQ5ZjEtYjJlMy01NzcwYzU0YjVlZWYiLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtd2VzdC0yLmFtYXpvbmF3cy5jb21cL3VzLXdlc3QtMl9LNHV6eksxeloiLCJjbGllbnRfaWQiOiIxbWZtZWVyY2E3ZzNsMTJubnMzZDI0YjdpcyIsIm9yaWdpbl9qdGkiOiJlZTcyNDNmYy0wYWE0LTRkZmMtOGMzYi1mYjA3N2Y4MGY5NTMiLCJldmVudF9pZCI6ImViMWU5NWU4LWZiZmQtNGZiOS1hMDQzLTEwYWU5OWVlZjUzMyIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE3MTE0ODk2MDUsImV4cCI6MTcxMTQ5MzIwNSwiaWF0IjoxNzExNDg5NjA1LCJqdGkiOiI4NGU4NmI1Ny0xZDM4LTQ0MmQtOTA0MS1hM2Q5ODQxN2E1YzUiLCJ1c2VybmFtZSI6IjJUMVFIUXlEcUtYQzNrTEhBSUpKSm9cL2FNTkg3RmFNZkw1WGtoZ1VtQ3dVPSJ9.UxQfyZMkdm53EY0W-YxlwEOiWyH_y2WZUzMTo305VWxLErt7C9sMqPlSvMA_NK0M90wQ1Z8vpGLBN0R48YQZaFmrNPCVOl5RrMnnFKpeofNCrjjd9HfLr6ZaPW_X-0MeL3ABnFny7t-Da3aDASezTuAAcM3Qc6HlDwlaDZ9ofxXZGYMr4fqYc3J_CPgQdzdtfQd1xYMsw50APPTY38uq8698I2ZFuYe8hJftdHPTaJ3R3wP3oKk8HnM2ulzIgld7tbr6vH_FjyxBr7OSXOUV9s2rarhuxeqm4Axz8GQ7iahFCsrGoaxKytmQvy6Wk2GxPGKMJvN6-U6Ar9WIHNTrfQ",

            "cool@gmail.com");
    }};

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
    public void createUser(final UserSignUpRequest signUpDTO) {
//        cognitoUserService.signUp(signUpDTO);

        final User user = new User(signUpDTO.getEmail());
        userJpaRepository.save(user);
    }

    @Override
    public AdminListUserAuthEventsResult userAuthEvents(String username, int maxResult, String nextToken) {
        return cognitoUserService.getUserAuthEvents(username, maxResult, nextToken);
    }

    @Override
    public UserResponse getCurrent(final String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        final String email = users.get(token);
//        final AdminGetUserResult userDetails = cognitoUserService.getUserDetails(email);

//        final List<AttributeType> userAttributes = userDetails.getUserAttributes();

//        return new UserResponse(userDetails.getUsername(),
//                         get("given_name", userAttributes).getValue(),
//                         get("family_name", userAttributes).getValue(),
//                         get("email", userAttributes).getValue(),
//                                "",
//                                "",
//                                "");

        return new UserResponse(email, "", "", email, "orgId", "", "");
    }

    AttributeType get(String name, List<AttributeType> attributeTypes) {
        return attributeTypes.stream().filter(attributeType -> attributeType.getName().equals(name)).findFirst().get();
    }

}
