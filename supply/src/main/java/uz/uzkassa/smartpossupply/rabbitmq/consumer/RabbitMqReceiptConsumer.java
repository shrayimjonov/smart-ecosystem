package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.ReceiptSendPayload;
import uz.uzkassa.smartpossupply.dto.receipt.SmartposReceiptDTO;
import uz.uzkassa.smartpossupply.dto.receipt.TerminalReceiptDTO;
import uz.uzkassa.smartpossupply.security.jwt.TokenProvider;
import uz.uzkassa.smartpossupply.service.ReceiptService;
import uz.uzkassa.smartpossupply.service.SmartposReceiptService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.03.2023 14:46
 */

@Component
@Slf4j
public class RabbitMqReceiptConsumer implements MqConstants {

    private final SmartposReceiptService smartposReceiptService;
    private final ReceiptService receiptService;
    private final TokenProvider tokenProvider;

    public RabbitMqReceiptConsumer(SmartposReceiptService smartposReceiptService, ReceiptService receiptService, TokenProvider tokenProvider) {
        this.smartposReceiptService = smartposReceiptService;
        this.receiptService = receiptService;
        this.tokenProvider = tokenProvider;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = RECEIPT_SEND_TO_SMARTPOS_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = RECEIPT_SEND_TO_SMARTPOS_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void send(ReceiptSendPayload payload) {
        try {
            smartposReceiptService.sendToSmartpos(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Receipt send error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = DIVIDED_RECEIPT_SEND_TO_SMARTPOS_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = DIVIDED_RECEIPT_SEND_TO_SMARTPOS_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void sendDividedReceipt(SmartposReceiptDTO payload) {
        try {
            smartposReceiptService.sendReceipt(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Receipt send error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = TERMINAL_RECEIPT_SAVE_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = TERMINAL_RECEIPT_SAVE_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void save(TerminalReceiptDTO payload, @Header(AUTHORIZATION) String token) {
        SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(token));
        try {
            receiptService.create(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Receipt save error: " + e.getMessage());
        }
    }
}
