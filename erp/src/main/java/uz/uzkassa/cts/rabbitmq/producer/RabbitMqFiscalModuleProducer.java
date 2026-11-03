package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;

@Component
public class RabbitMqFiscalModuleProducer extends RabbitMqBaseProducer {

    public void syncInactiveFiscalModules(String terminalId) {
        sendToQueue(terminalId, INACTIVE_FISCAL_MODULE_QUEUE, INACTIVE_FISCAL_MODULE_PAYLOAD);
    }
}
