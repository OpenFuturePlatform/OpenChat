package io.openfuture.openmessanger.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AwsS3Config {

    private final AwsConfig awsConfig;

    @Bean
    public AmazonS3 amazonS3(AWSCredentials awsCredentials) {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.US_WEST_2)
                .build();
    }

    @Bean
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(
                awsConfig.getAccessKey(),
                awsConfig.getSecretKey()
        );
    }

}
