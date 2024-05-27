package io.openfuture.openmessanger.service

import com.amazonaws.services.cognitoidp.model.AdminListUserAuthEventsResult
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult
import io.openfuture.openmessanger.service.dto.*
import io.openfuture.openmessanger.service.response.UserResponse
import io.openfuture.openmessanger.web.response.AuthenticatedResponse
import javax.validation.constraints.NotNull

interface UserAuthService {
    fun authenticate(userLogin: LoginRequest): AuthenticatedResponse
    fun refreshToken(request: RefreshTokenRequest): AuthenticatedResponse
    fun authenticateSms(loginSmsVerifyRequest: LoginSmsVerifyRequest): AuthenticatedResponse
    fun logout(accessToken: @NotNull String?)
    fun userForgotPassword(username: String?): ForgotPasswordResult?
    fun createUser(signUpDTO: UserSignUpRequest)
    fun userAuthEvents(username: String?, maxResult: Int, nextToken: String?): AdminListUserAuthEventsResult?
    fun current(): UserResponse
}