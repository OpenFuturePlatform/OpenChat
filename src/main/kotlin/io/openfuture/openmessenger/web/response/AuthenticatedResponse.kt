package io.openfuture.openmessenger.web.response

import java.io.Serializable

data class AuthenticatedResponse(
    val username: String? = null,
    val accessToken: String? = null,
    val idToken: String? = null,
    val refreshToken: String? = null
) : Serializable 