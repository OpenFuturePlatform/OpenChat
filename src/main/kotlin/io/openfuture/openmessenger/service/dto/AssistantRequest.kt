package io.openfuture.openmessenger.service.dto

import java.time.LocalDateTime

data class AssistantRequest(
    val chatId: Int,
    val isGroup: Boolean,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)
