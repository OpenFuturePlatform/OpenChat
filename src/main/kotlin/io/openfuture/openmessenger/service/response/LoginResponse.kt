package io.openfuture.openmessenger.service.response

data class LoginResponse(
    var token: String? = null,
    var message: String? = null,
    val userId: String? = null,
    var refreshToken: String? = null
)