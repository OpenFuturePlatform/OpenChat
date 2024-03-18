package io.openfuture.openmessanger.service.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginSmsVerifyRequest {

    @NotBlank
    private String sessionId;

    @NotBlank
    private String username;

    @NotBlank
    private String sms;
}
