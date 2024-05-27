package io.openfuture.openmessanger.repository.entity

import jakarta.persistence.*

@Entity
@Table(name = "private_chat")
class PrivateChat(
    @Column(name = "type")
    val type: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    @OneToMany(mappedBy = "chatId")
    var chatParticipants: List<ChatParticipant>? = null
}