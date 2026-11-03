package uz.uzkassa.smartpossupply.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class MinioConfiguration {

    private final ApplicationProperties applicationProperties;

    public MinioConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    MinioClient minioClient() {
        return MinioClient
            .builder()
            .endpoint(applicationProperties.getMinioStorageConfig().getHost())
            .credentials(
                applicationProperties.getMinioStorageConfig().getUsername(),
                applicationProperties.getMinioStorageConfig().getPassword()
            )
            .build();
    }
}
