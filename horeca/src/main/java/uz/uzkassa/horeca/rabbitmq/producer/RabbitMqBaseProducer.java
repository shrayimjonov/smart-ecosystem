package uz.uzkassa.horeca.rabbitmq.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.constants.MqConstants;


@Component
public class RabbitMqBaseProducer implements MqConstants {

    @Autowired
    protected RabbitTemplate rabbitTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    protected void sendToQueue(Object object, String queueName, String payload) {
        this.sendToQueue(object, queueName, payload, DEFAULT_DELAY);
    }

    protected void sendToQueue(Object object, String queueName, String payload, int delay) {
        try {
            String objectString = objectMapper.writeValueAsString(object);
            Message jsonMessage = MessageBuilder
                .withBody(objectString.getBytes())
                .andProperties(
                    MessagePropertiesBuilder
                        .newInstance()
                        .setContentType(MediaType.APPLICATION_JSON_VALUE)
                        .setHeader(X_DELAY, (DEFAULT_DELAY_IN_LOOP * delay))
                        .setHeader(TYPE_ID, payload)
                        .build()
                )
                .build();
            rabbitTemplate.send(DELAY_EXCHANGE_NAME, queueName, jsonMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
