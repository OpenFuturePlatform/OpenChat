package io.openfuture.openmessenger.service

import com.amazonaws.services.cognitoidp.model.AdminListUserAuthEventsResult
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult
import io.openfuture.openmessenger.service.dto.*
import io.openfuture.openmessenger.service.response.UserResponse
import io.openfuture.openmessenger.web.response.AuthenticatedResponse
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