package io.openfuture.openmessenger.service.dto

import java.time.LocalDateTime

data class AiRequest(
    val chatId: Int,
    val isGroup: Boolean,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)
