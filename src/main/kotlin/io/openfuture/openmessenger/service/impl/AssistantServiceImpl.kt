package io.openfuture.openmessenger.service.impl

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.openfuture.openmessenger.assistant.gemini.GeminiService
import io.openfuture.openmessenger.assistant.model.*
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
        objectMapper.registerModule(JavaTimeModule())

        val messages = loadMessageHistory(assistantRequest)
        val conversation = messages.map { message: Message -> message.sender + ": " +  message.body }
            .joinToString(separator = ";")

        val result = geminiService.chat("$PROMPT_FOR_REMINDER. Conversation starts here. $conversation")

        println("Result [$result]")

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
            reminderItemList.ifEmpty { emptyList() }
        )
    }

    override fun generateTodos(assistantRequest: AssistantRequest): Todos {
        val participants: List<String>? = if (assistantRequest.isGroup) {
            groupChatService.get(assistantRequest.chatId)?.groupParticipants?.map { it.participant!! }
        } else privateChatService.getParticipants(assistantRequest.chatId)?.map { it.username!! }

        val objectMapper = jacksonObjectMapper()

        val messages = loadMessageHistory(assistantRequest)
        val conversation = messages.map { message: Message -> message.sender + ": " +  message.body }
            .joinToString(separator = ";")

        println(conversation)

        val result = geminiService.chat("$PROMPT_TODOS. Conversation starts here. $conversation")

        println("Result [$result]")

        val todos = objectMapper.readValue<List<Todo>>(result!!)

        return Todos(
            if (assistantRequest.isGroup) null else assistantRequest.chatId,
            if (assistantRequest.isGroup) assistantRequest.chatId else null,
            participants,
            null,
            LocalDateTime.now(),
            1,
            assistantRequest.startTime,
            assistantRequest.endTime,
            todos.ifEmpty { emptyList() }
        )
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
        const val PROMPT_FOR_REMINDER = "Here is a conversation wrapped on quotes. " +
                "Please read and analyze if there are any reminders for participants. " +
                "In case there was mentioned anything important to be reminded about or some arrangement between participant " +
                "give me only and json output, in case multiple items, result is array, no other text with following format {\\\"remindAt\\\": \\\"exactDate time in ISO " +
                "8601\\\", " +
                "\\\"description\\\": \\\"description about topic that have a place at remindAt field\\\"}"
        const val PROMPT_TODOS = "Here is a conversation wrapped on quotes. " +
                "Please read and analyze if there are any tasks for participants. " +
                "In case there was mentioned anything important to be done or some assignment from someone " +
                "give me only and json output, in case multiple items, result is array, no other text with following format " +
                "{\\\"dueDate\\\": \\\"due date time for task in ISO 8601\\\", " +
                "\\\"description\\\": \\\"description about the task\\\"" +
                "\\\"executor\\\": \\\"who is assignee\\\"" +
                "\\\"context\\\": \\\"in which context task was raised\\\"" +
                "}"
    }

}