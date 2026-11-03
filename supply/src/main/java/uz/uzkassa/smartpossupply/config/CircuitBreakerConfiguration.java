package uz.uzkassa.smartpossupply.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.uzkassa.smartpossupply.integration.IntegrationType;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.time.Duration;
import java.util.Arrays;

@Configuration
public class CircuitBreakerConfiguration {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> integrationCircuitBreakerCustomizer() {
        String[] ids = Arrays.stream(IntegrationType.values())
                .map(Enum::name)
                .toArray(String[]::new);

        return factory -> factory.configure(builder -> builder
                        .circuitBreakerConfig(CircuitBreakerConfig.custom()
                                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                                .slidingWindowSize(10)
                                .failureRateThreshold(50)
                                .waitDurationInOpenState(Duration.ofSeconds(30))
                                .permittedNumberOfCallsInHalfOpenState(5)
                                .ignoreExceptions(BadRequestException.class)
                                .build())
                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                // RestTemplate read timeout is 30s; keep this higher so RestTemplate fires first
                                .timeoutDuration(Duration.ofSeconds(35))
                                .build()),
                ids);
    }
}
