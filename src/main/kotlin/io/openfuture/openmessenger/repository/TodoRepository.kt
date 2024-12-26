package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.AssistantTodoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TodoRepository : JpaRepository<AssistantTodoEntity, Long> {
    fun findAllByAuthorAndChatId(author: String, chatId: Int): List<AssistantTodoEntity>
    fun findAllByAuthorAndGroupChatId(author: String, chatId: Int): List<AssistantTodoEntity>
}