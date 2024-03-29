package io.openfuture.openmessanger.web.controller;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.cognitoidp.model.AdminListUserAuthEventsResult;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;

import io.openfuture.openmessanger.service.UserService;
import io.openfuture.openmessanger.service.dto.LoginRequest;
import io.openfuture.openmessanger.service.dto.LoginSmsVerifyRequest;
import io.openfuture.openmessanger.service.dto.UserPasswordUpdateRequest;
import io.openfuture.openmessanger.service.dto.UserSignUpRequest;
import io.openfuture.openmessanger.service.response.LoginResponse;
import io.openfuture.openmessanger.service.response.SignUpResponse;
import io.openfuture.openmessanger.service.response.UserResponse;
import io.openfuture.openmessanger.web.response.AuthenticatedResponse;
import io.openfuture.openmessanger.web.response.BaseResponse;

@RestController
@Validated
@RequestMapping("/api/v1/public")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/signup")
    public SignUpResponse signUp(@RequestBody @Validated UserSignUpRequest signUpDTO) {
        userService.createUser(signUpDTO);
        return new SignUpResponse("User account created successfully", new SignUpResponse.Data(signUpDTO.getEmail(),
                                                                                               signUpDTO.getFirstName(),
                                                                                               signUpDTO.getLastName()));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Validated LoginRequest loginRequest) {
        final AuthenticatedResponse authenticate = userService.authenticate(loginRequest);
        return new LoginResponse(authenticate.getAccessToken(), "User logged in Successfully", authenticate.getRefreshToken());
    }

    @GetMapping("/current")
    public ResponseEntity<String> current(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(username, HttpStatus.OK);
    }

    @GetMapping("/user")
    public UserResponse getUserDetails(@RequestHeader("Authorization") String bearerToken) {
        String accessToken = bearerToken.replace("Bearer ", "");
        return userService.getCurrent(accessToken);
    }

    @PostMapping("/login-sms-verify")
    public ResponseEntity<AuthenticatedResponse> loginSmsVerify(@RequestBody @Validated LoginSmsVerifyRequest loginSmsVerifyRequest) {
        return new ResponseEntity<>(userService.authenticateSms(loginSmsVerifyRequest), HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<BaseResponse> changePassword(@RequestBody @Validated UserPasswordUpdateRequest userPasswordUpdateRequest) {
        AuthenticatedResponse authenticatedResponse = userService.updateUserPassword(userPasswordUpdateRequest);

        return new ResponseEntity<>(new BaseResponse(authenticatedResponse, "Update successfully", false), HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<BaseResponse> logout(@RequestHeader("Authorization") String bearerToken) {
        if (bearerToken != null && bearerToken.contains("Bearer ")) {
            String accessToken = bearerToken.replace("Bearer ", "");

            userService.logout(accessToken);

            return new ResponseEntity<>(new BaseResponse(null, "Logout successfully", false), HttpStatus.OK);
        }
        return new ResponseEntity<>(new BaseResponse(null, "Header not correct"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/forget-password")
    public ResponseEntity<BaseResponse> forgotPassword(@NotNull @NotEmpty @Email @RequestParam("email") String email) {
        ForgotPasswordResult result = userService.userForgotPassword(email);
        return new ResponseEntity<>(new BaseResponse(
                result.getCodeDeliveryDetails().getDestination(),
                "You should soon receive an email which will allow you to reset your password. Check your spam and trash if you can't find the email.", false), HttpStatus.OK);
    }

    @GetMapping(value = "/user-events")
    public ResponseEntity<BaseResponse> getUserAuthEvents(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "maxResult", defaultValue = "0") int maxResult,
            @RequestParam(value = "nextToken", required = false) String nextToken) {

        AdminListUserAuthEventsResult result = userService.userAuthEvents(username, maxResult, nextToken);
        return new ResponseEntity<>(new BaseResponse(
                result, "user data", false), HttpStatus.OK);
    }

}
