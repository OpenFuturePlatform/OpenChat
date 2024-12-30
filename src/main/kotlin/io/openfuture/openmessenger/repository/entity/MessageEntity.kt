package io.openfuture.openmessenger.repository.entity

import java.time.LocalDateTime

class MessageEntity {
    var id: Int = 0
    var body: String?
    var sender: String
    var recipient: String? = null
    var contentType: MessageContentType
    var receivedAt: LocalDateTime? = null
    var sentAt: LocalDateTime
    var privateChatId: Int? = null
    var groupChatId: Int? = null

    constructor(
        id: Int,
        body: String?,
        sender: String,
        recipient: String?,
        contentType: MessageContentType,
        receivedAt: LocalDateTime?,
        sentAt: LocalDateTime,
        privateChatId: Int?
    ) {
        this.id = id
        this.body = body
        this.sender = sender
        this.recipient = recipient
        this.contentType = contentType
        this.receivedAt = receivedAt
        this.sentAt = sentAt
        this.privateChatId = privateChatId
    }

    constructor(
        body: String?,
        sender: String,
        recipient: String?,
        contentType: MessageContentType,
        receivedAt: LocalDateTime?,
        sentAt: LocalDateTime,
        privateChatId: Int?
    ) {
        this.body = body
        this.sender = sender
        this.recipient = recipient
        this.contentType = contentType
        this.receivedAt = receivedAt
        this.sentAt = sentAt
        this.privateChatId = privateChatId
    }

    constructor(
        body: String?,
        sender: String,
        contentType: MessageContentType,
        sentAt: LocalDateTime,
        groupChat: Int?
    ) {
        this.body = body
        this.sender = sender
        this.contentType = contentType
        this.sentAt = sentAt
        groupChatId = groupChat
    }

    constructor(
        id: Int,
        body: String?,
        sender: String,
        contentType: MessageContentType,
        sentAt: LocalDateTime,
        groupChat: Int?
    ) {
        this.id = id
        this.body = body
        this.sender = sender
        privateChatId = -1 //mute errors on client
        recipient = "" //mute errors on client
        receivedAt = LocalDateTime.now() //mute errors on client
        this.contentType = contentType
        this.sentAt = sentAt
        groupChatId = groupChat
    }
}