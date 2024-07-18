package io.openfuture.openmessenger.service.dto


data class KeyResponse(
    val blockchain: String,
    var privateKey: String,
    var address: String
)
