package io.openfuture.openmessanger.service.dto;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

import io.openfuture.openmessanger.annotation.PasswordValueMatch;
import io.openfuture.openmessanger.annotation.ValidPassword;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@PasswordValueMatch.List({
        @PasswordValueMatch(
                field = "password",
                fieldMatch = "passwordConfirm",
                message = "Passwords do not match!"
        )
})
@AllArgsConstructor()
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserPasswordUpdateRequest extends AuthenticatedChallengeRequest {

    @ValidPassword
    @NonNull
    @NotBlank(message = "New password is mandatory")
    private String password;


    @ValidPassword
    @NonNull
    @NotBlank(message = "Confirm Password is mandatory")
    private String passwordConfirm;
}
