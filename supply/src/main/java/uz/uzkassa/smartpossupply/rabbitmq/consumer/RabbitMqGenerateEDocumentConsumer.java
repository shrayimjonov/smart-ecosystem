package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.category.EDocsGenerateDTO;
import uz.uzkassa.smartpossupply.service.EDocService;

@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RabbitMqGenerateEDocumentConsumer implements MqConstants {

    EDocService eDocService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = AUTOMATIC_GENERATE_E_DOCS_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = AUTOMATIC_GENERATE_E_DOCS_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void generateEDoc(EDocsGenerateDTO payload) {
        try {
            eDocService.generateEDocs(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Generating e-doc error: " + e.getMessage());
        }
    }
}
