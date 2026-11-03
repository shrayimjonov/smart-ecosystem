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
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;
import uz.uzkassa.cts.security.jwt.TokenProvider;
import uz.uzkassa.cts.service.ImportReportService;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqImportReportConsumer implements MqConstants {

    ImportReportService importReportService;
    TokenProvider tokenProvider;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = IMPORT_REPORT_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = IMPORT_REPORT_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void importReport(ImportReportPayload payload, @Header("Authorization") String token) {
        try {
            SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(token));
            importReportService.importReportQueue(payload);
        } catch (Exception e) {
            log.error("Creating import-report error: ", e);
            throw new AmqpRejectAndDontRequeueException("Creating import-report error: " + e.getMessage());
        }
    }
}
