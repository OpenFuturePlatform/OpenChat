package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.ChatParticipant
import org.springframework.data.jpa.repository.JpaRepository

interface ChatParticipantRepository : JpaRepository<ChatParticipant, Int> {
    fun findAllByChatId(chat: Int?): List<ChatParticipant>
}