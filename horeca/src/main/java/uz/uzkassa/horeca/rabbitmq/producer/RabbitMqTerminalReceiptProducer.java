package uz.uzkassa.horeca.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.dto.payload.ClickhouseLogHistoryPayload;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptDTO;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.05.2024
 */
@Component
public class RabbitMqTerminalReceiptProducer extends RabbitMqBaseProducer {

    public void sendToQueue(TerminalReceiptDTO payload) {
        sendToQueue(payload, TERMINAL_RECEIPT_SAVE_QUEUE, TERMINAL_RECEIPT_SAVE_PAYLOAD, 3);
    }
}
