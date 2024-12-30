package io.openfuture.openmessenger.service.impl

import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.openfuture.openmessenger.assistant.gemini.GeminiService
import io.openfuture.openmessenger.assistant.model.*
import io.openfuture.openmessenger.repository.MessageJdbcRepository
import io.openfuture.openmessenger.repository.NoteRepository
import io.openfuture.openmessenger.repository.ReminderRepository
import io.openfuture.openmessenger.repository.TodoRepository
import io.openfuture.openmessenger.repository.entity.AssistantNoteEntity
import io.openfuture.openmessenger.repository.entity.AssistantReminderEntity
import io.openfuture.openmessenger.repository.entity.AssistantTodoEntity
import io.openfuture.openmessenger.repository.entity.Message
import io.openfuture.openmessenger.service.AssistantService
import io.openfuture.openmessenger.service.GroupChatService
import io.openfuture.openmessenger.service.PrivateChatService
import io.openfuture.openmessenger.service.UserAuthService
import io.openfuture.openmessenger.service.dto.AssistantRequest
import io.openfuture.openmessenger.service.dto.GetAllNotesRequest
import io.openfuture.openmessenger.service.dto.GetAllRemindersRequest
import io.openfuture.openmessenger.service.dto.GetAllTodosRequest
import io.openfuture.openmessenger.service.response.UserResponse
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AssistantServiceImpl(
    val geminiService: GeminiService,
    val groupChatService: GroupChatService,
    val privateChatService: PrivateChatService,
    val messageJdbcRepository: MessageJdbcRepository,
    val userAuthService: UserAuthService,
    val noteRepository: NoteRepository,
    val todoRepository: TodoRepository,
    val reminderRepository: ReminderRepository
) : AssistantService {

    override fun generateNotes(assistantRequest: AssistantRequest): ConversationNotes? {
        val current = userAuthService.current()

        val participants: List<String>? = getParticipants(assistantRequest)

        getRecipient(current, assistantRequest)

        val conversation = getConversation(assistantRequest)

        val result = geminiService.chat("Retrieve a short key notes separated with * from a following conversation related to participant ${current.email}. s$conversation")

        val objectMapper = jacksonObjectMapper()

        val assistantNoteEntity = AssistantNoteEntity(
            current.email,
            if (assistantRequest.isGroup) null else assistantRequest.chatId,
            if (assistantRequest.isGroup) assistantRequest.chatId else null,
            objectMapper.writeValueAsString(participants),
            getRecipient(current, assistantRequest),
            LocalDateTime.now(),
            1,
            assistantRequest.startTime,
            assistantRequest.endTime,
            objectMapper.writeValueAsString(result!!.split("*").filter { s: String -> s.isNotEmpty() })
        )
        noteRepository.save(assistantNoteEntity)

        return ConversationNotes(
            if (assistantRequest.isGroup) null else assistantRequest.chatId,
            if (assistantRequest.isGroup) assistantRequest.chatId else null,
            participants,
            getRecipient(current, assistantRequest),
            LocalDateTime.now(),
            1,
            assistantRequest.startTime,
            assistantRequest.endTime,
            result.split("*").filter { s: String -> s.isNotEmpty() }
        )
    }

    private fun getRecipient(current: UserResponse, assistantRequest: AssistantRequest): String? = if (assistantRequest.isGroup) {
        null
    } else {
        privateChatService.getOtherUser(current.email!!, assistantRequest.chatId)?.username
    }

    override fun generateReminder(assistantRequest: AssistantRequest): Reminder {
        val current = userAuthService.current()
        val participants: List<String>? = getParticipants(assistantRequest)

        val objectMapper = jacksonObjectMapper()

        val conversation = getConversation(assistantRequest)

        var result = geminiService.chat("${PROMPT_FOR_REMINDER.format(current.email)}. Conversation starts here. $conversation")

        println("Result [$result]")

        result = result
            ?.replace("[", "")
            ?.replace("]", "")
            ?.replace("```json", "")
            ?.replace("```", "")

        val reminderItemList = objectMapper.readValue<List<ReminderItem>>("[$result]")

        val assistantReminderEntity = AssistantReminderEntity(
            current.email,
            if (assistantRequest.isGroup) null else assistantRequest.chatId,
            if (assistantRequest.isGroup) assistantRequest.chatId else null,
            objectMapper.writeValueAsString(participants),
            getRecipient(current, assistantRequest),
            LocalDateTime.now(),
            1,
            assistantRequest.startTime,
            assistantRequest.endTime,
            objectMapper.writeValueAsString(reminderItemList)
        )
        reminderRepository.save(assistantReminderEntity)

        return Reminder(
            if (assistantRequest.isGroup) null else assistantRequest.chatId,
            if (assistantRequest.isGroup) assistantRequest.chatId else null,
            participants,
            getRecipient(current, assistantRequest),
            LocalDateTime.now(),
            1,
            assistantRequest.startTime,
            assistantRequest.endTime,
            reminderItemList.ifEmpty { emptyList() }
        )
    }

    override fun generateTodos(assistantRequest: AssistantRequest): Todos {
        val current = userAuthService.current()
        val participants: List<String>? = getParticipants(assistantRequest)

        val objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())

        val conversation = getConversation(assistantRequest)

        println(conversation)

        var result = geminiService.chat("${PROMPT_TODOS.format(current.email)}. Conversation starts here. $conversation")

        println("Result [$result]")

        result = result
            ?.replace("[", "")
            ?.replace("]", "")
            ?.replace("```json", "")
            ?.replace("```", "")
        val todos = objectMapper.readValue<List<Todo>>("[$result]")

        val defaultObjectMapper = jacksonObjectMapper()

        val assistantTodoEntity = AssistantTodoEntity(
            current.email,
            if (assistantRequest.isGroup) null else assistantRequest.chatId,
            if (assistantRequest.isGroup) assistantRequest.chatId else null,
            objectMapper.writeValueAsString(participants),
            getRecipient(current, assistantRequest),
            LocalDateTime.now(),
            1,
            assistantRequest.startTime,
            assistantRequest.endTime,
            objectMapper.writeValueAsString(todos)
        )
        todoRepository.save(assistantTodoEntity)

        return Todos(
            if (assistantRequest.isGroup) null else assistantRequest.chatId,
            if (assistantRequest.isGroup) assistantRequest.chatId else null,
            participants,
            getRecipient(current, assistantRequest),
            LocalDateTime.now(),
            1,
            assistantRequest.startTime,
            assistantRequest.endTime,
            todos.ifEmpty { emptyList() }
        )
    }

    override fun getAllNotes(getAllNotesRequest: GetAllNotesRequest): List<ConversationNotes> {
        val current = userAuthService.current()
        val notes: List<AssistantNoteEntity> = if (getAllNotesRequest.isGroup) {
            noteRepository.findAllByAuthorAndGroupChatId(current.email!!, getAllNotesRequest.chatId)
        } else {
            noteRepository.findAllByAuthorAndChatId(current.email!!, getAllNotesRequest.chatId)
        }

        return notes.map { convertFromEntity(it) }
    }

    override fun getAllTodos(getAllTodosRequest: GetAllTodosRequest): List<Todos> {
        val current = userAuthService.current()
        val todos: List<AssistantTodoEntity> = if (getAllTodosRequest.isGroup) {
            todoRepository.findAllByAuthorAndGroupChatId(current.email!!, getAllTodosRequest.chatId)
        } else {
            todoRepository.findAllByAuthorAndChatId(current.email!!, getAllTodosRequest.chatId)
        }

        return todos.map { convertFromEntity(it) }
    }

    override fun getAllReminders(getAllRemindersRequest: GetAllRemindersRequest): List<Reminder> {
        val current = userAuthService.current()
        val reminders: List<AssistantReminderEntity> = if (getAllRemindersRequest.isGroup) {
            reminderRepository.findAllByAuthorAndGroupChatId(current.email!!, getAllRemindersRequest.chatId)
        } else {
            reminderRepository.findAllByAuthorAndChatId(current.email!!, getAllRemindersRequest.chatId)
        }

        return reminders.map { convertFromEntity(it) }
    }

    private fun getConversation(assistantRequest: AssistantRequest): String {
        val messages = loadMessageHistory(assistantRequest)
        val conversation = messages.map { message: Message -> message.sender + ": " + message.body }
            .joinToString(separator = ";")
        return conversation
    }

    private fun getParticipants(assistantRequest: AssistantRequest): List<String>? {
        val participants: List<String>? = if (assistantRequest.isGroup) {
            groupChatService.get(assistantRequest.chatId)?.groupParticipants?.map { it.participant!! }
        } else privateChatService.getParticipants(assistantRequest.chatId)?.map { it.username!! }
        return participants
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
                "Please read and analyze if there are any reminders for participant: %s. " +
                "In case there was mentioned anything important to be reminded about or some arrangement between participant " +
                "give me only and json output, in case multiple items, result is array, no other text with following format {\\\"remindAt\\\": \\\"exactDate time in ISO " +
                "8601\\\", " +
                "\\\"description\\\": \\\"description about topic that have a place at remindAt field\\\"}"
        const val PROMPT_TODOS = "Here is a conversation wrapped on quotes. " +
                "Please read and analyze if there are any tasks for participant: %s. " +
                "In case there was mentioned anything important to be done or some assignment from someone " +
                "give me only and json output, in case multiple items, result is array, no other text with following format " +
                "{\\\"dueDate\\\": \\\"due date time for task in ISO 8601\\\", " +
                "\\\"description\\\": \\\"description about the task\\\"" +
                "\\\"executor\\\": \\\"if participant is executor, put him here otherwise skip this task\\\"" +
                "\\\"context\\\": \\\"in which context task was raised\\\"" +
                "}. In case if dueDate is unknown and can't be extracted, put there null."
    }

    private fun convertFromEntity(entity: AssistantNoteEntity): ConversationNotes {
        val objectMapper = jacksonObjectMapper()
        val membersList: List<String> = entity.members?.let { objectMapper.readValue(it) } ?: emptyList()
        val notesList: List<String> = entity.notes?.let { objectMapper.readValue(it) } ?: emptyList()

        return ConversationNotes(
            chatId = entity.chatId,
            groupChatId = entity.groupChatId,
            members = membersList,
            recipient = entity.recipient,
            generatedAt = entity.generatedAt,
            version = entity.version,
            startTime = entity.startTime ?: LocalDateTime.now(),
            endTime = entity.endTime ?: LocalDateTime.now(),
            notes = notesList
        )
    }

    private fun convertFromEntity(entity: AssistantTodoEntity): Todos {
        val objectMapper = jacksonObjectMapper()
        val membersList: List<String> = entity.members?.let { objectMapper.readValue(it) } ?: emptyList()
        val todoList: List<Todo> = entity.todos?.let { objectMapper.readValue(it) } ?: emptyList()

        return Todos(
            chatId = entity.chatId,
            groupChatId = entity.groupChatId,
            members = membersList,
            recipient = entity.recipient,
            generatedAt = entity.generatedAt,
            version = entity.version,
            startTime = entity.startTime ?: LocalDateTime.now(),
            endTime = entity.endTime ?: LocalDateTime.now(),
            todos = todoList
        )
    }

    private fun convertFromEntity(entity: AssistantReminderEntity): Reminder {
        val objectMapper = jacksonObjectMapper()
        val membersList: List<String> = entity.members?.let { objectMapper.readValue(it) } ?: emptyList()
        val reminderList: List<ReminderItem> = entity.reminders?.let { objectMapper.readValue(it) } ?: emptyList()

        return Reminder(
            chatId = entity.chatId,
            groupChatId = entity.groupChatId,
            members = membersList,
            recipient = entity.recipient,
            generatedAt = entity.generatedAt,
            version = entity.version,
            startTime = entity.startTime ?: LocalDateTime.now(),
            endTime = entity.endTime ?: LocalDateTime.now(),
            reminders = reminderList
        )
    }

}