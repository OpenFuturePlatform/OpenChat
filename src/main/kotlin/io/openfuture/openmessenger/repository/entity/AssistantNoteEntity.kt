package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.LocalDateTime.now


@Entity
@Table(name = "assistant_notes")
class AssistantNoteEntity() {
    constructor(
        author: String?,
        chatId: Int?,
        groupChatId: Int?,
        members: String?,
        recipient: String?,
        generatedAt: LocalDateTime = now(),
        version: Int = 1,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        notes: String?
    ): this() {
        this.author = author
        this.chatId = chatId
        this.groupChatId = groupChatId
        this.members = members
        this.recipient = recipient
        this.generatedAt = generatedAt
        this.version = version
        this.startTime = startTime
        this.endTime = endTime
        this.notes = notes
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var author: String? = null
    var chatId: Int? = null
    var groupChatId: Int? = null
    var members: String? = null
    var recipient: String? = null
    var generatedAt: LocalDateTime = now()
    var version: Int = 1
    var startTime: LocalDateTime? = null
    var endTime: LocalDateTime? = null
    var notes: String? = null
}
