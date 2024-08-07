package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.assistant.gemini.GeminiService
import io.openfuture.openmessenger.service.SpeechToTextService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/ai")
@RestController
class SpeechToTextController(
    val geminiService: GeminiService,
    val speechToTextService: SpeechToTextService
) {

    @GetMapping("/generateSummary/{attachmentId}")
    fun generateNotes(
        @PathVariable attachmentId: Int
    ): String {
        val transcript = speechToTextService.extractTranscript(attachmentId)
        val chat = geminiService.chat("Generate a summary from the following meeting record: {$transcript}")

        return chat!!
    }

    @GetMapping("/extractTranscript/{attachmentId}")
    fun extractTranscript(@PathVariable attachmentId: Int): String {
        return speechToTextService.extractTranscript(attachmentId)
    }

}
