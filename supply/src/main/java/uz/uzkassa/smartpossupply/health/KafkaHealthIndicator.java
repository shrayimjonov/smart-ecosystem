package uz.uzkassa.smartpossupply.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("kafka")
@RequiredArgsConstructor
public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public Health health() {
        try {
            try (Producer<String, String> producer = kafkaTemplate.getProducerFactory().createProducer()) {
                producer.partitionsFor("health-check");
                return Health.up()
                        .withDetail("messaging", "kafka")
                        .withDetail("status", "Connection successful")
                        .build();
            }
        } catch (Exception e) {
            log.warn("Kafka health check failed", e);
            return Health.down()
                    .withDetail("messaging", "kafka")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}