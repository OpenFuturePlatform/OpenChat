package io.openfuture.openmessenger.service.job

import com.amazonaws.services.transcribe.AmazonTranscribe
import com.amazonaws.services.transcribe.model.GetTranscriptionJobRequest
import com.amazonaws.services.transcribe.model.GetTranscriptionJobResult
import java.util.concurrent.TimeUnit

class TranscriptionJobResult(
    val transcriptionJobName: String,
    val amazonTranscribe: AmazonTranscribe
) : Runnable {

    override fun run() {
        var getJobRequest: GetTranscriptionJobRequest = GetTranscriptionJobRequest()
            .withTranscriptionJobName(transcriptionJobName)

        var getJobResponse: GetTranscriptionJobResult = amazonTranscribe.getTranscriptionJob(getJobRequest)

        var status = getJobResponse.transcriptionJob.transcriptionJobStatus

        while (!status.equals("COMPLETED") || !status.equals("FAILED")) {
            getJobRequest = GetTranscriptionJobRequest()
                .withTranscriptionJobName(transcriptionJobName)
            getJobResponse = amazonTranscribe.getTranscriptionJob(getJobRequest)
            status = getJobResponse.transcriptionJob.transcriptionJobStatus
        }

        try {
            TimeUnit.SECONDS.sleep(5)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

}

