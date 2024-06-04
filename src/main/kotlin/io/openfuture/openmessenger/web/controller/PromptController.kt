package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.assistant.model.ConversationNotes
import io.openfuture.openmessenger.assistant.model.Reminder
import io.openfuture.openmessenger.service.AiProcessor
import io.openfuture.openmessenger.service.dto.AiRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/ai")
@RestController
class PromptController(
    val aiProcessor: AiProcessor
) {

    @PostMapping("/generateNotes")
    fun generateNotes(
        @RequestBody request: AiRequest
    ): ConversationNotes? {
        return aiProcessor.generateNotes(request)
    }

    @PostMapping("/generateReminders")
    fun generateReminders(
        @RequestBody request: AiRequest
    ): Reminder? {
        return aiProcessor.generateReminder(request)
    }

}
