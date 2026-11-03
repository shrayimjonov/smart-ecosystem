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
import uz.uzkassa.cts.dto.launcher.LauncherRabbitPaginationRequestDto;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.service.DeviceTariffService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqLauncherConsumer implements MqConstants {

    DeviceTariffService deviceTariffService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = LAUNCHER_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = LAUNCHER_QUEUE),
        containerFactory = "rabbitListenerContainerFactorySimple")
    public void launcherSync(LauncherRabbitPaginationRequestDto pagination) {
        log.info("Received message launcher device block status sync page: {}, size: {} ", pagination.getPage(), pagination.getSize());
        try {
            deviceTariffService.blockUnblockDevices(pagination);
        } catch (Exception e) {
            log.error("Received message launcher device block status queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Received message launcher device block status queue error: " + e.getMessage());
        }
    }
}
