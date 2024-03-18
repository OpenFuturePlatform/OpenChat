package io.openfuture.openmessanger.service.dto;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticatedChallengeRequest {

    @NotBlank
    private String sessionId;

    @NonNull
    @NotBlank(message = "username is mandatory")
    private String username;

    @NotBlank
    private String challengeType;
}
