package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;

@Component
public class RabbitMqDeviceSwitcherProducer extends RabbitMqBaseProducer {

    public void paymentHistoryCreate(String tin) {
        sendToQueue(tin, PAYMENT_HISTORY_CREATE_QUEUE, SERVICE_PAYMENT_HISTORY_PAYLOAD);
    }

    public void deviceStatusSwitch(String tin) {
        sendToQueue(tin, DEVICE_SWITCH_DELAY_QUEUE, DEVICE_SWITCH_DELAY_PAYLOAD, 10);
    }

    public void createServiceInvoice(String tin) {
        sendToQueue(tin, SERVICE_INVOICE_QUEUE, SERVICE_INVOICE_PAYLOAD);
    }

    public void notifyAboutMonthlyServiceFee(String tin) {
        sendToQueue(tin, NOTIFY_MONTHLY_FEE_QUEUE, NOTIFY_MONTHLY_FEE_PAYLOAD);
    }

    public void notifyAboutMonthlyServiceFeeBySms(String tin) {
        sendToQueue(tin, NOTIFY_MONTHLY_FEE_BY_SMS_QUEUE, NOTIFY_MONTHLY_FEE_BY_SMS_PAYLOAD);
    }

    public void notifyBySms(String phoneNumber) {
        sendToQueue(phoneNumber, NOTIFY_BY_SMS_QUEUE, NOTIFY_BY_SMS_PAYLOAD);
    }

    public void notifyAllBySms(EtcRequestDTO payload) {
        sendToQueue(payload, NOTIFY_ALL_BY_SMS_QUEUE, NOTIFY_ALL_BY_SMS_PAYLOAD);
    }

}
