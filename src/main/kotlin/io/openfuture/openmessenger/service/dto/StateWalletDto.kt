package io.openfuture.openmessenger.service.dto

import java.time.LocalDateTime

data class StateWalletDto(
        val id: String?,
        val address: String?,
        val webhook: String,
        val blockchain: String,
        val lastUpdateDate: LocalDateTime?
)
