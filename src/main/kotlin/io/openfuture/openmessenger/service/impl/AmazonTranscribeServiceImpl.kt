package io.openfuture.openmessenger.service.impl

import com.amazonaws.services.transcribe.AmazonTranscribe
import com.amazonaws.services.transcribe.model.*
import io.openfuture.openmessenger.configuration.AwsConfig
import io.openfuture.openmessenger.service.TranscribeService
import org.springframework.stereotype.Service
import java.util.*


@Service
class AmazonTranscribeServiceImpl(
    private val amazonTranscribe: AmazonTranscribe,
    private val awsConfig: AwsConfig
): TranscribeService {

    override fun extractText(filename: String): String? {

        val transcriptionJobName = "TranscriptionJob1-$filename-${UUID.randomUUID()}"
        val outputS3BucketName = awsConfig.transcriptsBucket
        val inputS3BucketName = awsConfig.recordingsBucket

        val s3Filename = "s3://$inputS3BucketName/$filename"
        val myMedia: Media = Media().withMediaFileUri(s3Filename)

        val request: StartTranscriptionJobRequest = StartTranscriptionJobRequest()
            .withTranscriptionJobName(transcriptionJobName)
            .withLanguageCode(LanguageCode.EnUS)
            .withMedia(myMedia)
            .withOutputBucketName(outputS3BucketName)

        println(request)
        val startJobResponse: StartTranscriptionJobResult = amazonTranscribe.startTranscriptionJob(request)

        println("Created the transcription job")
        println(startJobResponse.transcriptionJob)

        val getJobRequest: GetTranscriptionJobRequest = GetTranscriptionJobRequest()
            .withTranscriptionJobName(transcriptionJobName)

        var jobStatus: String
        var getJobResponse: GetTranscriptionJobResult
        do {
            getJobResponse = amazonTranscribe.getTranscriptionJob(getJobRequest)
            jobStatus = getJobResponse.transcriptionJob.transcriptionJobStatus
            println("Current status: $jobStatus")
            if (jobStatus == TranscriptionJobStatus.COMPLETED.toString()) {
                println("Transcription job completed")
                println("Transcription: ${getJobResponse.transcriptionJob.transcript.transcriptFileUri}")
            } else if (jobStatus == TranscriptionJobStatus.FAILED.toString()) {
                println("Transcription job failed: ${getJobResponse.transcriptionJob.failureReason}")
                break
            }
            Thread.sleep(5000)
        } while (jobStatus == TranscriptionJobStatus.IN_PROGRESS.toString())

        return getJobResponse.transcriptionJob.transcript.transcriptFileUri
    }

}
