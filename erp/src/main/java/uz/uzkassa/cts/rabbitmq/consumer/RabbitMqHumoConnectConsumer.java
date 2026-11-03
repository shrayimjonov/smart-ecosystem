package uz.uzkassa.cts.rabbitmq.consumer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectLastReceiptRequestDTO;
import uz.uzkassa.cts.service.HumoConnectService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqHumoConnectConsumer implements MqConstants {

    HumoConnectService humoConnectService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = HUMO_CONNECT_UPDATE_LAST_RECEIPT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = HUMO_CONNECT_UPDATE_LAST_RECEIPT_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void create(HumoConnectLastReceiptRequestDTO humoConnectLastReceiptRequestDTO) {
        log.info("Received message to update humo connect last receipt: {}", humoConnectLastReceiptRequestDTO);
        try {
            humoConnectService.updateLastReceipt(humoConnectLastReceiptRequestDTO);
        } catch (Exception e) {
            log.error("Received message to update humo connect last receipt queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Received message to update humo connect last receipt queue error: " + e.getMessage());
        }
    }
}
