package io.openfuture.openmessanger.service;

import javax.validation.constraints.NotNull;

import com.amazonaws.services.cognitoidp.model.AdminListUserAuthEventsResult;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.UserType;

import io.openfuture.openmessanger.service.dto.LoginRequest;
import io.openfuture.openmessanger.service.dto.LoginSmsVerifyRequest;
import io.openfuture.openmessanger.service.dto.UserPasswordUpdateRequest;
import io.openfuture.openmessanger.service.dto.UserSignUpRequest;
import io.openfuture.openmessanger.service.response.LoginResponse;
import io.openfuture.openmessanger.web.response.AuthenticatedResponse;
import io.openfuture.openmessanger.web.response.BaseResponse;


public interface UserService {

    AuthenticatedResponse authenticate(LoginRequest userLogin);

    AuthenticatedResponse authenticateSms(LoginSmsVerifyRequest loginSmsVerifyRequest);

    AuthenticatedResponse updateUserPassword(UserPasswordUpdateRequest userPasswordUpdateRequest);

    void logout(@NotNull String accessToken);

    ForgotPasswordResult userForgotPassword(String username);

    UserType createUser(UserSignUpRequest signUpDTO);

    AdminListUserAuthEventsResult userAuthEvents(String username, int maxResult, String nextToken);

}
