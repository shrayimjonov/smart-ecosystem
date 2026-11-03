package uz.uzkassa.cts.rabbitmq.producer;


import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.launcher.LauncherRabbitPaginationRequestDto;

@Component
public class RabbitMqLauncherProducer extends RabbitMqBaseProducer {

    public void launcherSync(LauncherRabbitPaginationRequestDto payload) {
        sendToQueue(payload, LAUNCHER_QUEUE, LAUNCHER_PAYLOAD);
    }
}
