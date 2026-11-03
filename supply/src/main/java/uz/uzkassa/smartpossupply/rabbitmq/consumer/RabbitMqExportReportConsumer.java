package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.ExportReportPayload;
import uz.uzkassa.smartpossupply.security.jwt.TokenProvider;
import uz.uzkassa.smartpossupply.service.handlers.excel.CommonExportReportStrategy;

@Component
@Slf4j
public class RabbitMqExportReportConsumer implements MqConstants {

    CommonExportReportStrategy exportReportStrategy;
    TokenProvider tokenProvider;

    public RabbitMqExportReportConsumer(CommonExportReportStrategy exportReportStrategy, TokenProvider tokenProvider) {
        this.exportReportStrategy = exportReportStrategy;
        this.tokenProvider = tokenProvider;
    }

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
            exportReportStrategy.generateReport(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Creating export-report error: " + e.getMessage());
        }
    }
}
