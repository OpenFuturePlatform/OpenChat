package io.openfuture.openmessanger.service.dto;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.openfuture.openmessanger.annotation.ValidPassword;

import lombok.Data;

@Data
public class UserSignUpRequest {

    @NotBlank
    @NotNull
    @Email
    private String email;

    @NotBlank
    @NotNull
    private String firstName;

    private String lastName;

    @ValidPassword
    private String password;

    private String phoneNumber;

    @NotNull
    @NotEmpty
    private Set<String> roles;

}
