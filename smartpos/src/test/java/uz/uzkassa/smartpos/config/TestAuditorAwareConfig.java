package uz.uzkassa.smartpos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class TestAuditorAwareConfig {
    @Bean
    public AuditorAware<String> springSecurityAuditorAware() {
        return () -> Optional.of("test");
    }
}
