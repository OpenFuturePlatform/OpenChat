package io.openfuture.openmessenger.service.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class WebhookPayloadDto(
    val blockchain: String,
    val walletAddress: String,
    val userId: String?,//omit from JSON if null
    val metadata: Any?,//omit from JSON if null
    val transaction: WebhookTransactionDto
) {

    data class WebhookTransactionDto(
        val hash: String,
        val from: Set<String>,
        val to: String,
        val amount: BigDecimal,
        val date: LocalDateTime,
        val blockHeight: Long,
        val blockHash: String
    )
}
