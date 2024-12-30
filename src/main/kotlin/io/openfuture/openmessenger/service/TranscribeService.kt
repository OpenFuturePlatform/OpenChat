package io.openfuture.openmessenger.service

interface TranscribeService {
    fun extractText(filename: String): String?
}