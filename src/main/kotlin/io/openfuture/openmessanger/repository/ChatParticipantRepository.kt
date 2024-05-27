package io.openfuture.openmessanger.repository

import io.openfuture.openmessanger.repository.entity.ChatParticipant
import org.springframework.data.jpa.repository.JpaRepository

interface ChatParticipantRepository : JpaRepository<ChatParticipant?, Int?> {
    fun findAllByChatId(chat: Int?): List<ChatParticipant?>?
}