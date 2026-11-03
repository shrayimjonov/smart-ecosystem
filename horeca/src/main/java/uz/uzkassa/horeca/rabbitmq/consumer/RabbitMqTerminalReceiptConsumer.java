package uz.uzkassa.horeca.rabbitmq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.constants.MqConstants;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptDTO;
import uz.uzkassa.horeca.service.ReceiptService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.05.2024
 */
@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RabbitMqTerminalReceiptConsumer implements MqConstants {

    ReceiptService receiptService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = TERMINAL_RECEIPT_SAVE_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = TERMINAL_RECEIPT_SAVE_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void saveReceipt(TerminalReceiptDTO payload) {
        log.info("Terminal receipt save rabbitMq");
        try {
            receiptService.create(payload);
        } catch (Exception e) {
            log.error("Terminal receipt save rabbitMq error: {}", payload);
            throw new AmqpRejectAndDontRequeueException("Terminal receipt save rabbitMq: " + e.getMessage());
        }
    }
}
