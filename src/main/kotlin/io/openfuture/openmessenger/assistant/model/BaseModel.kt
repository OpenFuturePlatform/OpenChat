package io.openfuture.openmessenger.assistant.model

import java.time.LocalDateTime

interface BaseModel {
    val chatId: Int?
    val groupChatId: Int?
    val members: List<String>?
    val recipient: String?
    val generatedAt: LocalDateTime
    val version: Int
    val startTime: LocalDateTime
    val endTime: LocalDateTime
}