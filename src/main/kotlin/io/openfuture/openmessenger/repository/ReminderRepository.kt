package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.AssistantReminderEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReminderRepository : JpaRepository<AssistantReminderEntity, Long> {
    fun findAllByAuthorAndChatId(author: String, chatId: Int): List<AssistantReminderEntity>
    fun findAllByAuthorAndGroupChatId(author: String, chatId: Int): List<AssistantReminderEntity>
}