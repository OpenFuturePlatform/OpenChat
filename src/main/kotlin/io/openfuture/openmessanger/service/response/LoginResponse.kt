package io.openfuture.openmessanger.service.response

data class LoginResponse(
    var token: String? = null,
    var message: String? = null,
    var refreshToken: String? = null
)