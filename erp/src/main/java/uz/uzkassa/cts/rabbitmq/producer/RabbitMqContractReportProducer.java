package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.device.CustomerDeviceDTO;
import uz.uzkassa.cts.dto.payload.ContractReportPayload;

@Component
public class RabbitMqContractReportProducer extends RabbitMqBaseProducer {

    public void createContractReport(ContractReportPayload payload) {
        sendToQueue(payload, CONTRACT_REPORT_CREATE_QUEUE, CONTRACT_REPORT_CREATE_PAYLOAD);
    }

    public void createContractReportThroughRegisterKKM(CustomerDeviceDTO dto) {
        sendToQueue(dto, CONTRACT_REPORT_CREATE_THROUGH_REGISTER_KKM_QUEUE, CONTRACT_REPORT_CREATE_THROUGH_REGISTER_KKM_PAYLOAD);
    }
}
