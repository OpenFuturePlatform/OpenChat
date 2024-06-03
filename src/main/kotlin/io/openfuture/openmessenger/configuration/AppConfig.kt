package io.openfuture.openmessenger.configuration

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig(
    private val awsConfig: AwsConfig
) {

    @Bean
    fun awsCognitoIdentityProviderClient(): AWSCognitoIdentityProvider {
        return AWSCognitoIdentityProviderClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(awsConfig.accessKey, awsConfig.secretKey)))
            .withRegion(awsConfig.region)
            .build()
    }

}