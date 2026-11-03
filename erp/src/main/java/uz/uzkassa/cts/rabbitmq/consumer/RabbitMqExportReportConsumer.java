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
import uz.uzkassa.cts.dto.payload.ExportReportPayload;
import uz.uzkassa.cts.security.jwt.TokenProvider;
import uz.uzkassa.cts.service.ExportReportService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqExportReportConsumer implements MqConstants {

    ExportReportService exportReportService;
    TokenProvider tokenProvider;

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = EXPORT_REPORT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = EXPORT_REPORT_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void exportReport(ExportReportPayload payload, @Header(AUTHORIZATION) String token) {
        try {
            SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(token));
            exportReportService.generateReport(payload);
        } catch (Exception e) {
            log.error("Creating export-report error: ", e);
            throw new AmqpRejectAndDontRequeueException("Creating export-report error: " + e.getMessage());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
