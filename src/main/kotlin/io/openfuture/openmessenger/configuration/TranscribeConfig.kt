package io.openfuture.openmessenger.configuration

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.transcribe.AmazonTranscribe
import com.amazonaws.services.transcribe.AmazonTranscribeClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TranscribeConfig(
    private val awsCredentials: AWSCredentials
) {

    @Bean
    fun transcribeClient(): AmazonTranscribe {
        return AmazonTranscribeClientBuilder
            .standard()
            .withRegion(Regions.US_EAST_2)
            .withCredentials(AWSStaticCredentialsProvider(awsCredentials))
            .build()
    }

}