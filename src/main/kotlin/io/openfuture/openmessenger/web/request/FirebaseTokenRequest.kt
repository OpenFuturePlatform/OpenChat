package io.openfuture.openmessenger.web.request

data class FirebaseTokenRequest(
    val userId: String,
    val token: String,
)