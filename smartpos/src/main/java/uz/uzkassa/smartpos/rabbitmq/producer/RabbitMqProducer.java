package uz.uzkassa.smartpos.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;

import java.io.Serializable;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMqProducer implements MqConstants {
    private final RabbitTemplate rabbitTemplate;

    public <T extends Serializable> void send(final T payload, String queue) {
        sendWithDelayTime(payload, queue, 1000L);
    }

    public <T extends Serializable> void sendWithType(final T payload, String queue, String type) {
        sendWithTypeAndDelayTime(payload, queue, type, 1000L);
    }


    public <T extends Serializable> void sendWithDelayTime(final T payload, String queue, Long delayTime) {
        rabbitTemplate.convertAndSend(DELAY_EXCHANGE_NAME, queue, payload, message -> {
            message.getMessageProperties().setHeader(X_DELAY, delayTime);
            return message;
        });
    }

    public <T extends Serializable> void sendWithTypeAndDelayTime(final T payload, String queue, String type, Long delayTime) {
        rabbitTemplate.convertAndSend(DELAY_EXCHANGE_NAME, queue, payload, message -> {
            message.getMessageProperties().setHeader(TYPE_ID, type);
            message.getMessageProperties().setHeader(X_DELAY, delayTime);
            return message;
        });
    }
}
