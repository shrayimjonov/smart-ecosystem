package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.category.EDocsGenerateDTO;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 13.01.2024
 */
@Component
public class RabbitMqGenerateEDocumentProducer extends RabbitMqBaseProducer {

    public void sendToQueue(EDocsGenerateDTO payload) {
        sendToQueue(payload, AUTOMATIC_GENERATE_E_DOCS_QUEUE, AUTOMATIC_GENERATE_E_DOCS_PAYLOAD, 1);
    }
}
