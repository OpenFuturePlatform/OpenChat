package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.assistant.model.ConversationNotes
import io.openfuture.openmessenger.assistant.model.Reminder
import io.openfuture.openmessenger.service.AssistantService
import io.openfuture.openmessenger.service.dto.AssistantRequest
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

}
