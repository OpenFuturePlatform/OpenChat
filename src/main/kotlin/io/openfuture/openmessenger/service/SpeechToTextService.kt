package io.openfuture.openmessenger.service

interface SpeechToTextService {
    fun extractTranscript(attachmentId: Int): String
    fun getTranscript(attachmentId: String): String
}