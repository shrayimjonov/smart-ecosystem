package uz.uzkassa.silen.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.transaction.AfterCommitExecutor;

@Service
@RequiredArgsConstructor
public class KafkaProducerImpl<T> implements KafkaProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;
    private final AfterCommitExecutor afterCommitExecutor;

    @Override
    public void sendMessage(final String topic, final T message) {
        afterCommitExecutor.execute(() -> {
            this.kafkaTemplate.send(topic, message);
        });
    }
}
