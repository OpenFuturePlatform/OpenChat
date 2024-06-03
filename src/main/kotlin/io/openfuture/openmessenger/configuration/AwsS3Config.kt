package io.openfuture.openmessenger.configuration

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsS3Config(
    private val awsConfig: AwsConfig
) {

    @Bean
    fun amazonS3(awsCredentials: AWSCredentials?): AmazonS3 {
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(awsCredentials))
            .withRegion(Regions.US_EAST_2)
            .build()
    }

    @Bean
    fun awsCredentials(): AWSCredentials {
        return BasicAWSCredentials(
            awsConfig.accessKey,
            awsConfig.secretKey
        )
    }
}