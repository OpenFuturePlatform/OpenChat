package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.service.UserAuthService
import io.openfuture.openmessenger.service.dto.LoginRequest
import io.openfuture.openmessenger.service.dto.LoginSmsVerifyRequest
import io.openfuture.openmessenger.service.dto.RefreshTokenRequest
import io.openfuture.openmessenger.service.dto.UserSignUpRequest
import io.openfuture.openmessenger.service.response.Data
import io.openfuture.openmessenger.service.response.LoginResponse
import io.openfuture.openmessenger.service.response.SignUpResponse
import io.openfuture.openmessenger.service.response.UserResponse
import io.openfuture.openmessenger.web.response.AuthenticatedResponse
import io.openfuture.openmessenger.web.response.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@RestController
@Validated
@RequestMapping("/api/v1")
class AuthController(val userAuthService: UserAuthService) {

    @PostMapping(value = ["/public/signup"])
    fun signUp(@RequestBody @Validated signUpDTO: UserSignUpRequest): SignUpResponse {
        userAuthService.createUser(signUpDTO)
        return SignUpResponse(
            "User account created successfully", Data(
                signUpDTO.email,
                signUpDTO.firstName,
                signUpDTO.lastName
            )
        )
    }

    @PostMapping("/public/login")
    fun login(@RequestBody @Validated loginRequest: LoginRequest): LoginResponse {
        val authenticate = userAuthService.authenticate(loginRequest)
        return LoginResponse(
            authenticate.accessToken,
            "User logged in Successfully",
            authenticate.refreshToken
        )
    }

    @PostMapping("/refreshToken")
    fun refreshToken(@RequestBody @Validated request: RefreshTokenRequest): LoginResponse {
        val authenticate = userAuthService.refreshToken(request)
        return LoginResponse(
            authenticate.accessToken,
            "Token refreshed",
            authenticate.refreshToken
        )
    }

    @GetMapping("/user")
    fun userDetails(): UserResponse? {
        return userAuthService.current()
    }

    @PostMapping("/login-sms-verify")
    fun loginSmsVerify(@RequestBody @Validated loginSmsVerifyRequest: LoginSmsVerifyRequest): ResponseEntity<AuthenticatedResponse> {
        return ResponseEntity(userAuthService.authenticateSms(loginSmsVerifyRequest), HttpStatus.OK)
    }

    @DeleteMapping("/logout")
    fun logout(@RequestHeader("Authorization") bearerToken: String?): ResponseEntity<BaseResponse> {
        if (bearerToken != null && bearerToken.contains("Bearer ")) {
            val accessToken = bearerToken.replace("Bearer ", "")
            userAuthService.logout(accessToken)
            return ResponseEntity(BaseResponse(null, "Logout successfully", false), HttpStatus.OK)
        }
        return ResponseEntity(BaseResponse(null, "Header not correct"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping(value = ["/forget-password"])
    fun forgotPassword(@RequestParam("email") email: @NotNull @NotEmpty @Email String?): ResponseEntity<BaseResponse> {
        val result = userAuthService.userForgotPassword(email)
        return ResponseEntity(
            BaseResponse(
                result!!.codeDeliveryDetails.destination,
                "You should soon receive an email which will allow you to reset your password. Check your spam and trash if you can't find the email.", false
            ), HttpStatus.OK
        )
    }

    @GetMapping(value = ["/user-events"])
    fun getUserAuthEvents(
        @RequestParam(value = "username") username: String?,
        @RequestParam(value = "maxResult", defaultValue = "0") maxResult: Int,
        @RequestParam(value = "nextToken", required = false) nextToken: String?
    ): ResponseEntity<BaseResponse> {
        val result = userAuthService.userAuthEvents(username, maxResult, nextToken)
        return ResponseEntity(
            BaseResponse(
                result, "user data", false
            ), HttpStatus.OK
        )
    }
}