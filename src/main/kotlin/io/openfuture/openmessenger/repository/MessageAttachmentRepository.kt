package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.MessageAttachment
import org.springframework.data.jpa.repository.JpaRepository

interface MessageAttachmentRepository : JpaRepository<MessageAttachment, Int> {
    fun findAllByMessageId(message: Int): List<MessageAttachment>
}