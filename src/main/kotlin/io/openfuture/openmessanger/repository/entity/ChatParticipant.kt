package io.openfuture.openmessanger.repository.entity

import jakarta.persistence.*

@Entity
@Table(name = "chat_participant")
class ChatParticipant(var chatId: Int?, var username: String?) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

}