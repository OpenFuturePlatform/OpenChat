package io.openfuture.openmessenger.assistant.model

import java.time.LocalDateTime

data class Reminder(
    override val chatId: Int?,
    override val groupChatId: Int?,
    override val members: List<String>?,
    override val recipient: String?,
    override val generatedAt: LocalDateTime,
    override val version: Int,
    override val startTime: LocalDateTime,
    override val endTime: LocalDateTime,

    val reminders: List<ReminderItem>
): BaseModel

data class ReminderItem(
    val remindAt: LocalDateTime?,
    val description: String?
)