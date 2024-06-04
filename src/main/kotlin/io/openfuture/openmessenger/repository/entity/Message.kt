package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "message")
@Entity
class Message {
    @Id
    var id: Int = 0
    var body: String? = null
    var sender: String? = null
    var recipient: String? = null
    var contentType: String? = null
    var receivedAt: LocalDateTime? = null
    var sentAt: LocalDateTime? = null
    var privateChatId: Int? = null
    var groupChatId: Int? = null
}