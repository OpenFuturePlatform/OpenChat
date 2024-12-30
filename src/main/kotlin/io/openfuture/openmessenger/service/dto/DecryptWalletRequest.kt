package io.openfuture.openmessenger.service.dto

data class DecryptWalletRequest(
    var encryptedText: String,
    var password: String
)
