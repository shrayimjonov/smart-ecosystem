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
import uz.uzkassa.cts.dto.device.CustomerDeviceDTO;
import uz.uzkassa.cts.dto.payload.ContractReportPayload;
import uz.uzkassa.cts.service.ContractReportService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqContractReportConsumer implements MqConstants {

    ContractReportService contractReportService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = CONTRACT_REPORT_CREATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = CONTRACT_REPORT_CREATE_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void createContractReport(ContractReportPayload payload) {
        try {
            contractReportService.create(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Create contract report entity ID: " + payload.getEntityId());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = CONTRACT_REPORT_CREATE_THROUGH_REGISTER_KKM_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = CONTRACT_REPORT_CREATE_THROUGH_REGISTER_KKM_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void createContractReportThroughRegisterKKM(CustomerDeviceDTO dto) {
        try {
            contractReportService.createThroughRegister(dto);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Create contract report through register() method: {} " + dto);
        }
    }
}
