package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.*

@Entity
@Table(name = "message_attachment")
class MessageAttachment(@Column(name = "attachment_id") var attachmentId: Int?,
                        @Column(name = "message_id") var messageId: Int?) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

}