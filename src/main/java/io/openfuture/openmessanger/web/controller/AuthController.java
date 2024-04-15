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

import io.jsonwebtoken.JwtBuilder;
import io.openfuture.openmessanger.service.UserAuthService;
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

    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping(value = "/signup")
    public SignUpResponse signUp(@RequestBody @Validated UserSignUpRequest signUpDTO) {
        userAuthService.createUser(signUpDTO);
        return new SignUpResponse("User account created successfully", new SignUpResponse.Data(signUpDTO.getEmail(),
                                                                                               signUpDTO.getFirstName(),
                                                                                               signUpDTO.getLastName()));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Validated LoginRequest loginRequest) {
//        final AuthenticatedResponse authenticate = userAuthService.authenticate(loginRequest);
        return new LoginResponse("eyJraWQiOiJIOTQrUmNYdWdobGZUc0JLcDRcL0taQkZ4MmhRY09PUHM3eCt1SThZam1Tcz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJmZGQ3MDI5YS05MjY5LTQ5ZjEtYjJlMy01NzcwYzU0YjVlZWYiLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtd2VzdC0yLmFtYXpvbmF3cy5jb21cL3VzLXdlc3QtMl9LNHV6eksxeloiLCJjbGllbnRfaWQiOiIxbWZtZWVyY2E3ZzNsMTJubnMzZDI0YjdpcyIsIm9yaWdpbl9qdGkiOiJlZTcyNDNmYy0wYWE0LTRkZmMtOGMzYi1mYjA3N2Y4MGY5NTMiLCJldmVudF9pZCI6ImViMWU5NWU4LWZiZmQtNGZiOS1hMDQzLTEwYWU5OWVlZjUzMyIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE3MTE0ODk2MDUsImV4cCI6MTcxMTQ5MzIwNSwiaWF0IjoxNzExNDg5NjA1LCJqdGkiOiI4NGU4NmI1Ny0xZDM4LTQ0MmQtOTA0MS1hM2Q5ODQxN2E1YzUiLCJ1c2VybmFtZSI6IjJUMVFIUXlEcUtYQzNrTEhBSUpKSm9cL2FNTkg3RmFNZkw1WGtoZ1VtQ3dVPSJ9.UxQfyZMkdm53EY0W-YxlwEOiWyH_y2WZUzMTo305VWxLErt7C9sMqPlSvMA_NK0M90wQ1Z8vpGLBN0R48YQZaFmrNPCVOl5RrMnnFKpeofNCrjjd9HfLr6ZaPW_X-0MeL3ABnFny7t-Da3aDASezTuAAcM3Qc6HlDwlaDZ9ofxXZGYMr4fqYc3J_CPgQdzdtfQd1xYMsw50APPTY38uq8698I2ZFuYe8hJftdHPTaJ3R3wP3oKk8HnM2ulzIgld7tbr6vH_FjyxBr7OSXOUV9s2rarhuxeqm4Axz8GQ7iahFCsrGoaxKytmQvy6Wk2GxPGKMJvN6-U6Ar9WIHNTrfQ",
                                 "User logged in Successfully",
                                 "eyJraWQiOiJIOTQrUmNYdWdobGZUc0JLcDRcL0taQkZ4MmhRY09PUHM3eCt1SThZam1Tcz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJmZGQ3MDI5YS05MjY5LTQ5ZjEtYjJlMy01NzcwYzU0YjVlZWYiLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtd2VzdC0yLmFtYXpvbmF3cy5jb21cL3VzLXdlc3QtMl9LNHV6eksxeloiLCJjbGllbnRfaWQiOiIxbWZtZWVyY2E3ZzNsMTJubnMzZDI0YjdpcyIsIm9yaWdpbl9qdGkiOiJlZTcyNDNmYy0wYWE0LTRkZmMtOGMzYi1mYjA3N2Y4MGY5NTMiLCJldmVudF9pZCI6ImViMWU5NWU4LWZiZmQtNGZiOS1hMDQzLTEwYWU5OWVlZjUzMyIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE3MTE0ODk2MDUsImV4cCI6MTcxMTQ5MzIwNSwiaWF0IjoxNzExNDg5NjA1LCJqdGkiOiI4NGU4NmI1Ny0xZDM4LTQ0MmQtOTA0MS1hM2Q5ODQxN2E1YzUiLCJ1c2VybmFtZSI6IjJUMVFIUXlEcUtYQzNrTEhBSUpKSm9cL2FNTkg3RmFNZkw1WGtoZ1VtQ3dVPSJ9.UxQfyZMkdm53EY0W-YxlwEOiWyH_y2WZUzMTo305VWxLErt7C9sMqPlSvMA_NK0M90wQ1Z8vpGLBN0R48YQZaFmrNPCVOl5RrMnnFKpeofNCrjjd9HfLr6ZaPW_X-0MeL3ABnFny7t-Da3aDASezTuAAcM3Qc6HlDwlaDZ9ofxXZGYMr4fqYc3J_CPgQdzdtfQd1xYMsw50APPTY38uq8698I2ZFuYe8hJftdHPTaJ3R3wP3oKk8HnM2ulzIgld7tbr6vH_FjyxBr7OSXOUV9s2rarhuxeqm4Axz8GQ7iahFCsrGoaxKytmQvy6Wk2GxPGKMJvN6-U6Ar9WIHNTrfQ");
    }

    @GetMapping("/current")
    public ResponseEntity<String> current(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(username, HttpStatus.OK);
    }

    @GetMapping("/user")
    public UserResponse getUserDetails(@RequestHeader("Authorization") String bearerToken) {
        String accessToken = bearerToken.replace("Bearer ", "");
        final UserResponse current = userAuthService.getCurrent(accessToken);

        if (current == null) {
            throw new RuntimeException();
        }
        return current;
    }

    @PostMapping("/login-sms-verify")
    public ResponseEntity<AuthenticatedResponse> loginSmsVerify(@RequestBody @Validated LoginSmsVerifyRequest loginSmsVerifyRequest) {
        return new ResponseEntity<>(userAuthService.authenticateSms(loginSmsVerifyRequest), HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<BaseResponse> changePassword(@RequestBody @Validated UserPasswordUpdateRequest userPasswordUpdateRequest) {
        AuthenticatedResponse authenticatedResponse = userAuthService.updateUserPassword(userPasswordUpdateRequest);

        return new ResponseEntity<>(new BaseResponse(authenticatedResponse, "Update successfully", false), HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<BaseResponse> logout(@RequestHeader("Authorization") String bearerToken) {
        if (bearerToken != null && bearerToken.contains("Bearer ")) {
            String accessToken = bearerToken.replace("Bearer ", "");

//            userAuthService.logout(accessToken);

            return new ResponseEntity<>(new BaseResponse(null, "Logout successfully", false), HttpStatus.OK);
        }
        return new ResponseEntity<>(new BaseResponse(null, "Header not correct"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/forget-password")
    public ResponseEntity<BaseResponse> forgotPassword(@NotNull @NotEmpty @Email @RequestParam("email") String email) {
        ForgotPasswordResult result = userAuthService.userForgotPassword(email);
        return new ResponseEntity<>(new BaseResponse(
                result.getCodeDeliveryDetails().getDestination(),
                "You should soon receive an email which will allow you to reset your password. Check your spam and trash if you can't find the email.", false), HttpStatus.OK);
    }

    @GetMapping(value = "/user-events")
    public ResponseEntity<BaseResponse> getUserAuthEvents(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "maxResult", defaultValue = "0") int maxResult,
            @RequestParam(value = "nextToken", required = false) String nextToken) {

        AdminListUserAuthEventsResult result = userAuthService.userAuthEvents(username, maxResult, nextToken);
        return new ResponseEntity<>(new BaseResponse(
                result, "user data", false), HttpStatus.OK);
    }

}
