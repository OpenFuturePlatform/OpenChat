package io.openfuture.openmessanger.service;

import javax.validation.constraints.NotNull;

import com.amazonaws.services.cognitoidp.model.AdminListUserAuthEventsResult;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;

import io.openfuture.openmessanger.service.dto.LoginRequest;
import io.openfuture.openmessanger.service.dto.LoginSmsVerifyRequest;
import io.openfuture.openmessanger.service.dto.RefreshTokenRequest;
import io.openfuture.openmessanger.service.dto.UserPasswordUpdateRequest;
import io.openfuture.openmessanger.service.dto.UserSignUpRequest;
import io.openfuture.openmessanger.service.response.UserResponse;
import io.openfuture.openmessanger.web.response.AuthenticatedResponse;


public interface UserAuthService {

    AuthenticatedResponse authenticate(LoginRequest userLogin);

    AuthenticatedResponse refreshToken(RefreshTokenRequest request);

    AuthenticatedResponse authenticateSms(LoginSmsVerifyRequest loginSmsVerifyRequest);

    AuthenticatedResponse updateUserPassword(UserPasswordUpdateRequest userPasswordUpdateRequest);

    void logout(@NotNull String accessToken);

    ForgotPasswordResult userForgotPassword(String username);

    void createUser(UserSignUpRequest signUpDTO);

    AdminListUserAuthEventsResult userAuthEvents(String username, int maxResult, String nextToken);

    UserResponse getCurrent();
}
