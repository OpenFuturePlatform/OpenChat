package io.openfuture.openmessanger.repository

import io.openfuture.openmessanger.repository.entity.MessageAttachment
import org.springframework.data.jpa.repository.JpaRepository

interface MessageAttachmentRepository : JpaRepository<MessageAttachment, Int> {
    fun findAllByMessageId(message: Int): List<MessageAttachment>
}