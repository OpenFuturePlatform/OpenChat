package io.openfuture.openmessanger.service.dto

import javax.validation.constraints.NotBlank

data class LoginRequest(
    val email: @NotBlank String? = null,
    val password: @NotBlank String? = null
)