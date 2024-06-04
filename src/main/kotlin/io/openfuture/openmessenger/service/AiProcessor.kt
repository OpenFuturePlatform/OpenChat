package io.openfuture.openmessenger.service

import io.openfuture.openmessenger.assistant.model.ConversationNotes
import io.openfuture.openmessenger.assistant.model.Reminder
import io.openfuture.openmessenger.assistant.model.Todos
import io.openfuture.openmessenger.service.dto.AiRequest

interface AiProcessor {
    fun generateNotes(aiRequest: AiRequest): ConversationNotes?
    fun generateReminder(aiRequest: AiRequest): Reminder
//    fun generateTodos(aiRequest: AiRequest): Todos
}