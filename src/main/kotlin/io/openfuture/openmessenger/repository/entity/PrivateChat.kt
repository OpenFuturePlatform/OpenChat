package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.*

@Entity
@Table(name = "private_chat")
class PrivateChat(type: String? = null) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    @Column(name = "type")
    val type: String? = null

    @OneToMany(mappedBy = "chatId")
    var chatParticipants: List<ChatParticipant>? = null
}