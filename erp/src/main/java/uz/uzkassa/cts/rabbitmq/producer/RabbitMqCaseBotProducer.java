package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.CaseBotPayload;

@Component
public class RabbitMqCaseBotProducer extends RabbitMqBaseProducer {

    public void send(CaseBotPayload caseBotPayload) {
        sendToQueue(caseBotPayload, CASE_TELEGRAM_BOT_QUEUE, CASE_TELEGRAM_BOT_PAYLOAD);
    }
}
