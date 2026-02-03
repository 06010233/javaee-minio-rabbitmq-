// 完整文件: src/main/java/com/example/flight/config/MinioConfig.java

package com.example.flight.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    // 注意：请确保你的 application.yml 文件中使用的配置键是这些
    // 例如: minio.endpoint, minio.access-key, minio.secret-key
    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        // 返回一个配置好的 MinIO 客户端实例
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}