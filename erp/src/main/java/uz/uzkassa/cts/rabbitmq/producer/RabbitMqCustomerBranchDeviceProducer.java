package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceExcelDTO;

@Component
public class RabbitMqCustomerBranchDeviceProducer extends RabbitMqBaseProducer {

    public void updatePaymentStatus(CustomerBranchDeviceExcelDTO data) {
        sendToQueue(data, SEND_CUSTOMER_BRANCH_DEVICE_QUEUE, SEND_CUSTOMER_BRANCH_DEVICE_PAYLOAD);
    }
}
