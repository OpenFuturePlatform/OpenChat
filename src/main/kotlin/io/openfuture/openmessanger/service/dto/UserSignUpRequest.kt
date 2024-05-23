package io.openfuture.openmessanger.service.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class UserSignUpRequest(
    var email: @NotBlank @NotNull @Email String? = null,
    var firstName: @NotBlank @NotNull String? = null,
    var lastName: String? = null,
    var password: String? = null,
    var phoneNumber: String? = null,
    var roles: @NotNull @NotEmpty MutableSet<String>? = null
)