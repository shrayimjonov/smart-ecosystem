package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.fm.FiscalizationSyncDTO;
import uz.uzkassa.cts.dto.payload.SmartPosRemoveDevicePayload;

@Component
public class RabbitMqSmartPosProducer extends RabbitMqBaseProducer {

    public void updateCustomer(Long fiscalizationId) {
        sendToQueue(fiscalizationId, SMARTPOS_UPDATE_CUSTOMER_QUEUE, SMARTPOS_UPDATE_CUSTOMER_PAYLOAD);
    }

    public void removeDevice(SmartPosRemoveDevicePayload smartPosRemoveDevicePayload) {
        sendToQueue(smartPosRemoveDevicePayload, SMARTPOS_REMOVE_DEVICE_QUEUE, SMARTPOS_REMOVE_DEVICE_PAYLOAD);
    }

    public void syncCustomerBranch(Long customerBranchId) {
        sendToQueue(customerBranchId, SMARTPOS_SYNC_CUSTOMER_BRANCH_QUEUE, SMARTPOS_SYNC_CUSTOMER_BRANCH_PAYLOAD);
    }

    public void syncCustomer(FiscalizationSyncDTO fiscalizationSyncDTO) {
        sendToQueue(fiscalizationSyncDTO, SYNC_CUSTOMER_SMARTPOS_QUEUE, SYNC_CUSTOMER_SMARTPOS_PAYLOAD);
    }
}
