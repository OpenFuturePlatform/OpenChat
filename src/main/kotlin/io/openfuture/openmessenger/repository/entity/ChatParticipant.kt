package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.*

@Entity
@Table(name = "chat_participant")
class ChatParticipant(val chatId: Int? = 0, val username: String? = null) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

}