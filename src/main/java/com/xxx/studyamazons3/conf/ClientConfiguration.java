package com.xxx.studyamazons3.conf;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * minio客户端配置
 *
 * @author xxx
 */
@Component
public class ClientConfiguration {
    @Value("${minio.endpoint}")
    private String endPoint;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;

    @Bean("minioClient")
    public MinioClient getMininClient() {
        return MinioClient.builder().endpoint(endPoint).credentials(accessKey, secretKey).build();
    }
}