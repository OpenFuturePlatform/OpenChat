package io.openfuture.openmessenger.service

import io.openfuture.openmessenger.assistant.model.ConversationNotes
import io.openfuture.openmessenger.assistant.model.Reminder
import io.openfuture.openmessenger.assistant.model.Todos
import io.openfuture.openmessenger.service.dto.AssistantRequest
import io.openfuture.openmessenger.service.dto.GetAllNotesRequest

interface AssistantService {
    fun generateNotes(assistantRequest: AssistantRequest): ConversationNotes?
    fun generateReminder(assistantRequest: AssistantRequest): Reminder
    fun generateTodos(assistantRequest: AssistantRequest): Todos

    fun getAllNotes(getAllNotesRequest: GetAllNotesRequest): List<ConversationNotes>
}