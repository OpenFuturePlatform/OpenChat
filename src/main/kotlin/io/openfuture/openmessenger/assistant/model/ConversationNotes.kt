package io.openfuture.openmessenger.assistant.model

import java.time.LocalDateTime

data class ConversationNotes(
    override val chatId: Int?,
    override val groupChatId: Int?,
    override val members: List<String>?,
    override val recipient: String?,
    override val generatedAt: LocalDateTime,
    override val version: Int,
    override val startTime: LocalDateTime,
    override val endTime: LocalDateTime,
    val notes: List<String>
): BaseModel
