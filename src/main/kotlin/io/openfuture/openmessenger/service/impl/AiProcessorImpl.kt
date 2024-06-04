package io.openfuture.openmessenger.service.impl

import aj.org.objectweb.asm.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.openfuture.openmessenger.assistant.gemini.GeminiService
import io.openfuture.openmessenger.assistant.model.ConversationNotes
import io.openfuture.openmessenger.assistant.model.Reminder
import io.openfuture.openmessenger.assistant.model.ReminderItem
import io.openfuture.openmessenger.assistant.model.Todos
import io.openfuture.openmessenger.repository.MessageJdbcRepository
import io.openfuture.openmessenger.repository.entity.Message
import io.openfuture.openmessenger.service.AiProcessor
import io.openfuture.openmessenger.service.GroupChatService
import io.openfuture.openmessenger.service.PrivateChatService
import io.openfuture.openmessenger.service.dto.AiRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AiProcessorImpl(
    val geminiService: GeminiService,
    val groupChatService: GroupChatService,
    val privateChatService: PrivateChatService,
    val messageJdbcRepository: MessageJdbcRepository
) : AiProcessor {

    override fun generateNotes(aiRequest: AiRequest): ConversationNotes? {
        val participants: List<String>? = if (aiRequest.isGroup) {
            groupChatService.get(aiRequest.chatId)?.groupParticipants?.map { it.participant!! }
        } else privateChatService.getParticipants(aiRequest.chatId)?.map { it.username!! }

        val messages = loadMessageHistory(aiRequest)
        val prompt = messages.map { message: Message -> message.sender + ": " +  message.body }
            .joinToString(separator = "\n")

        val result = geminiService.chat("Retrieve a short key notes separated with * from a following conversation s$prompt")
        return ConversationNotes(
            if (aiRequest.isGroup) null else aiRequest.chatId,
            if (aiRequest.isGroup) aiRequest.chatId else null,
            participants,
            null,
            LocalDateTime.now(),
            1,
            aiRequest.startTime,
            aiRequest.endTime,
            result!!.split("*")
        )
    }

    override fun generateReminder(aiRequest: AiRequest): Reminder {
        val participants: List<String>? = if (aiRequest.isGroup) {
            groupChatService.get(aiRequest.chatId)?.groupParticipants?.map { it.participant!! }
        } else privateChatService.getParticipants(aiRequest.chatId)?.map { it.username!! }

        val objectMapper = jacksonObjectMapper()

        val messages = loadMessageHistory(aiRequest)
        val prompt = messages.map { message: Message -> message.sender + ": " +  message.body }
            .joinToString(separator = "\n")

        println(prompt)

        val result = geminiService.chat("Retrieve any task that should be reminded in following format or array $PROMPT_FOR_REMINDER result is just json, nothing else out of " +
                "this " +
                "conversation $prompt")

        println(result)

        val reminderItemList = objectMapper.readValue<List<ReminderItem>>(result!!)

        return Reminder(
            if (aiRequest.isGroup) null else aiRequest.chatId,
            if (aiRequest.isGroup) aiRequest.chatId else null,
            participants,
            null,
            LocalDateTime.now(),
            1,
            aiRequest.startTime,
            aiRequest.endTime,
            if (reminderItemList.isEmpty()) listOf(ReminderItem(LocalDateTime.now(), result)) else emptyList()
        )
    }

    private fun loadMessageHistory(aiRequest: AiRequest): List<Message> {
        return if (aiRequest.isGroup) {
            messageJdbcRepository.findByGroupChatIdAndSentAtBetween(
                aiRequest.chatId,
                LocalDateTime.from(aiRequest.startTime),
                LocalDateTime.from(aiRequest.endTime),
                Sort.by("sentAt").ascending()
            )
        } else messageJdbcRepository.findByPrivateChatIdAndSentAtBetween(
            aiRequest.chatId,
            LocalDateTime.from(aiRequest.startTime),
            LocalDateTime.from(aiRequest.endTime),
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