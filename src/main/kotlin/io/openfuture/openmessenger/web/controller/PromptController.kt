package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.assistant.model.ConversationNotes
import io.openfuture.openmessenger.assistant.model.Reminder
import io.openfuture.openmessenger.assistant.model.Todos
import io.openfuture.openmessenger.service.AssistantService
import io.openfuture.openmessenger.service.dto.AssistantRequest
import io.openfuture.openmessenger.service.dto.GetAllNotesRequest
import io.openfuture.openmessenger.service.dto.GetAllRemindersRequest
import io.openfuture.openmessenger.service.dto.GetAllTodosRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/ai")
@RestController
class PromptController(
    val assistantService: AssistantService
) {

    @PostMapping("/generateNotes")
    fun generateNotes(
        @RequestBody request: AssistantRequest
    ): ConversationNotes? {
        return assistantService.generateNotes(request)
    }

    @PostMapping("/generateReminders")
    fun generateReminders(
        @RequestBody request: AssistantRequest
    ): Reminder? {
        return assistantService.generateReminder(request)
    }

    @PostMapping("/generateTodos")
    fun generateTodos(
        @RequestBody request: AssistantRequest
    ): Todos? {
        return assistantService.generateTodos(request)
    }

    @PostMapping("/notes")
    fun getAllNotes(@RequestBody request: GetAllNotesRequest): List<ConversationNotes> {
        return assistantService.getAllNotes(request)
    }

    @PostMapping("/todos")
    fun getAllTodos(@RequestBody request: GetAllTodosRequest): List<Todos> {
        return assistantService.getAllTodos(request)
    }

    @PostMapping("/reminders")
    fun getAllReminders(@RequestBody request: GetAllRemindersRequest): List<Reminder> {
        return assistantService.getAllReminders(request)
    }

}
