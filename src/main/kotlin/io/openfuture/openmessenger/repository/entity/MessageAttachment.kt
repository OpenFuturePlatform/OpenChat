package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.*

@Entity
@Table(name = "message_attachment")
class MessageAttachment() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    @Column(name = "attachment_id")
    var attachmentId: Int? = null

    @Column(name = "message_id")
    var messageId: Int? = null

    constructor(
        attachmentIds : Int?,
        messageIds : Int?
    ) : this() {
        this.attachmentId = attachmentIds
        this.messageId = messageIds
    }
}