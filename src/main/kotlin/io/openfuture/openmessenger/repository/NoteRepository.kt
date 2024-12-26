package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.AssistantNoteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NoteRepository : JpaRepository<AssistantNoteEntity, Long> {
    fun findAllByAuthorAndChatId(author: String, chatId: Int): List<AssistantNoteEntity>
    fun findAllByAuthorAndGroupChatId(author: String, chatId: Int): List<AssistantNoteEntity>
}