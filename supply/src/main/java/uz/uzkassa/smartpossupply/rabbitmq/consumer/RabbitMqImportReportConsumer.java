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
import uz.uzkassa.smartpossupply.dto.payload.ImportReportItemPayload;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportPayload;
import uz.uzkassa.smartpossupply.security.jwt.TokenProvider;
import uz.uzkassa.smartpossupply.service.ImportReportService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.03.2023 14:46
 */

@Component
@Slf4j
public class RabbitMqImportReportConsumer implements MqConstants {

    private final ImportReportService importReportService;
    private final TokenProvider tokenProvider;

    public RabbitMqImportReportConsumer(ImportReportService importReportService, TokenProvider tokenProvider) {
        this.importReportService = importReportService;
        this.tokenProvider = tokenProvider;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = IMPORT_REPORT_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = IMPORT_REPORT_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void importReport(ImportReportPayload payload, @Header(AUTHORIZATION) String token) {
        try {
            SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(token));
            importReportService.importReportQueue(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Creating import-report error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = IMPORT_REPORT_ITEM_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = IMPORT_REPORT_ITEM_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void importReportItem(ImportReportItemPayload payload,@Header(AUTHORIZATION) String token) {
        try {
            SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(token));
            importReportService.importReportItemQueue(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Creating import-report error: " + e.getMessage());
        }
    }
}
