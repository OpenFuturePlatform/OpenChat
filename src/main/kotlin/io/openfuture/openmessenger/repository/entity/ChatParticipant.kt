package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.*

@Entity
@Table(name = "chat_participant")
class ChatParticipant(chatId: Int? = 0, username: String? = null) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    var chatId: Int? = 0

    var username: String? = null

}