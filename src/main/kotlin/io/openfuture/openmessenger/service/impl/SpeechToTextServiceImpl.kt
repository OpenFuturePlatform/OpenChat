package io.openfuture.openmessenger.service.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.openfuture.openmessenger.repository.AttachmentRepository
import io.openfuture.openmessenger.service.AttachmentService
import io.openfuture.openmessenger.service.SpeechToTextService
import io.openfuture.openmessenger.service.TranscribeService
import io.openfuture.openmessenger.service.dto.transcript.TranscriptionResponse
import org.springframework.stereotype.Service

@Service
class SpeechToTextServiceImpl(
    val transcribeService: TranscribeService,
    val attachmentService: AttachmentService,
    val attachmentRepository: AttachmentRepository
) : SpeechToTextService {

    override fun extractTranscript(attachmentId: Int): String {
        val objectMapper = jacksonObjectMapper()
        val attachmentResponses = attachmentRepository.get(listOf(attachmentId))
        val fileName: String = attachmentResponses?.get(0)?.name!!
        val resultFile = transcribeService.extractText(fileName)
        val last = resultFile?.split("/")?.last()

        val download = attachmentService.download(last)
        val result = objectMapper.readValue<TranscriptionResponse>(download!!)
        return result.results.transcripts.first().transcript
    }

}