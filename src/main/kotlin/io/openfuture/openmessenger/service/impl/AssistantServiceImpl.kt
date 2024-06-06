package io.openfuture.openmessenger.service.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.openfuture.openmessenger.assistant.gemini.GeminiService
import io.openfuture.openmessenger.assistant.model.ConversationNotes
import io.openfuture.openmessenger.assistant.model.Reminder
import io.openfuture.openmessenger.assistant.model.ReminderItem
import io.openfuture.openmessenger.assistant.model.Todos
import io.openfuture.openmessenger.repository.MessageJdbcRepository
import io.openfuture.openmessenger.repository.entity.Message
import io.openfuture.openmessenger.service.AssistantService
import io.openfuture.openmessenger.service.GroupChatService
import io.openfuture.openmessenger.service.PrivateChatService
import io.openfuture.openmessenger.service.dto.AssistantRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AssistantServiceImpl(
    val geminiService: GeminiService,
    val groupChatService: GroupChatService,
    val privateChatService: PrivateChatService,
    val messageJdbcRepository: MessageJdbcRepository
) : AssistantService {

    override fun generateNotes(assistantRequest: AssistantRequest): ConversationNotes? {
        val participants: List<String>? = if (assistantRequest.isGroup) {
            groupChatService.get(assistantRequest.chatId)?.groupParticipants?.map { it.participant!! }
        } else privateChatService.getParticipants(assistantRequest.chatId)?.map { it.username!! }

        val messages = loadMessageHistory(assistantRequest)
        val prompt = messages.map { message: Message -> message.sender + ": " +  message.body }
            .joinToString(separator = "\n")

        val result = geminiService.chat("Retrieve a short key notes separated with * from a following conversation s$prompt")
        return ConversationNotes(
            if (assistantRequest.isGroup) null else assistantRequest.chatId,
            if (assistantRequest.isGroup) assistantRequest.chatId else null,
            participants,
            null,
            LocalDateTime.now(),
            1,
            assistantRequest.startTime,
            assistantRequest.endTime,
            result!!.split("*")
        )
    }

    override fun generateReminder(assistantRequest: AssistantRequest): Reminder {
        val participants: List<String>? = if (assistantRequest.isGroup) {
            groupChatService.get(assistantRequest.chatId)?.groupParticipants?.map { it.participant!! }
        } else privateChatService.getParticipants(assistantRequest.chatId)?.map { it.username!! }

        val objectMapper = jacksonObjectMapper()

        val messages = loadMessageHistory(assistantRequest)
        val prompt = messages.map { message: Message -> message.sender + ": " +  message.body }
            .joinToString(separator = "\n")

        println(prompt)

        val result = geminiService.chat("Retrieve any task that should be reminded in following format or array $PROMPT_FOR_REMINDER result is just json, nothing else out of " +
                "this " +
                "conversation $prompt")

        println(result)

        val reminderItemList = objectMapper.readValue<List<ReminderItem>>(result!!)

        return Reminder(
            if (assistantRequest.isGroup) null else assistantRequest.chatId,
            if (assistantRequest.isGroup) assistantRequest.chatId else null,
            participants,
            null,
            LocalDateTime.now(),
            1,
            assistantRequest.startTime,
            assistantRequest.endTime,
            if (reminderItemList.isEmpty()) listOf(ReminderItem(LocalDateTime.now(), result)) else emptyList()
        )
    }

    override fun generateTodos(assistantRequest: AssistantRequest): Todos {
        TODO("Not yet implemented")
    }

    private fun loadMessageHistory(assistantRequest: AssistantRequest): List<Message> {
        return if (assistantRequest.isGroup) {
            messageJdbcRepository.findByGroupChatIdAndSentAtBetween(
                assistantRequest.chatId,
                LocalDateTime.from(assistantRequest.startTime),
                LocalDateTime.from(assistantRequest.endTime),
                Sort.by("sentAt").ascending()
            )
        } else messageJdbcRepository.findByPrivateChatIdAndSentAtBetween(
            assistantRequest.chatId,
            LocalDateTime.from(assistantRequest.startTime),
            LocalDateTime.from(assistantRequest.endTime),
            Sort.by("sentAt").ascending()
        )
    }

    companion object {
        val PROMPT_FOR_REMINDER = "remindAt:2024-06-03T08:00:00,description:Reminder description"

        val PROMPT_TODOS = """
              "executor": "John",
              "description": "Complete the project proposal",
              "dueDate": "2024-06-03T17:00:00",
              "context": "Discussion at 2 PM"
        """.trimIndent()
    }

}