package com.xxx.studyamazons3.conf;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3ClientConf {
    @Value("${etsi.object-storage.url}")
    private String objectStorageUrl;
    @Value("${etsi.object-storage.access-key}")
    private String accessKey;
    @Value("${etsi.object-storage.secret-key}")
    private String secretKey;

    @Bean("amazonS3Client")
    public AmazonS3 amazonS3Client() {
        AmazonS3 client = AmazonS3ClientBuilder.standard().withEndpointConfiguration(new AwsClientBuilder
                .EndpointConfiguration(objectStorageUrl, Regions.US_EAST_1.getName()))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
        return client;
    }
}
