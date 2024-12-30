package io.openfuture.openmessenger.service.dto

import javax.validation.constraints.NotBlank

data class LoginSmsVerifyRequest(
    val sessionId: @NotBlank String? = null,
    val username: @NotBlank String? = null,
    val sms: @NotBlank String? = null
)