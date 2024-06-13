package io.openfuture.openmessenger.assistant.model

import java.time.LocalDateTime

data class Todos(
    override val chatId: Int?,
    override val groupChatId: Int?,
    override val members: List<String>?,
    override val recipient: String?,
    override val generatedAt: LocalDateTime,
    override val version: Int,
    override val startTime: LocalDateTime,
    override val endTime: LocalDateTime,

    val todos: List<Todo>
): BaseModel

data class Todo(
    val executor: String?,
    val description: String?,
    val dueDate: LocalDateTime?,
    val context: String?
)