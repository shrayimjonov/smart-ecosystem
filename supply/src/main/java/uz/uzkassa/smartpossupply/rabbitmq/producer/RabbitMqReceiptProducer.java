package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.ReceiptSendPayload;
import uz.uzkassa.smartpossupply.dto.receipt.SmartposReceiptDTO;
import uz.uzkassa.smartpossupply.dto.receipt.TerminalReceiptDTO;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 13.09.2024
 */
@Component
public class RabbitMqReceiptProducer extends RabbitMqBaseProducer {

    public void sendReceiptToQueue(ReceiptSendPayload payload) {
        sendToQueue(payload, RECEIPT_SEND_TO_SMARTPOS_QUEUE, RECEIPT_SEND_TO_SMARTPOS_PAYLOAD);
    }

    public void sendDividedReceiptToQueue(SmartposReceiptDTO payload, int delay) {
        sendToQueue(payload, DIVIDED_RECEIPT_SEND_TO_SMARTPOS_QUEUE, DIVIDED_RECEIPT_SEND_TO_SMARTPOS_PAYLOAD, delay);
    }

    public void sendToQueue(TerminalReceiptDTO payload) {
        sendToQueue(payload, TERMINAL_RECEIPT_SAVE_QUEUE, TERMINAL_RECEIPT_SAVE_PAYLOAD);
    }
}
