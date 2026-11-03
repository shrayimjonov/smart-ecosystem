package uz.uzkassa.silen.config;

import io.minio.MinioClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class MinioConfiguration {

    @Value("${application.minio.url}")
    private String host;

    @Value("${application.minio.username}")
    private String username;

    @Value("${application.minio.password}")
    private String password;

    @Value("${application.minio.bucket}")
    private String bucket;

    @Bean
    MinioClient minioClient() {
        return MinioClient
            .builder()
            .endpoint(getHost())
            .credentials(getUsername(), getPassword())
            .build();
    }
}
