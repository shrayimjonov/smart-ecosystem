package uz.uzkassa.silen.rabbitmq.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.dto.UserDTO;
import uz.uzkassa.silen.dto.mq.*;
import uz.uzkassa.silen.dto.partyaggregation.AggregationStatusChangedPayload;
import uz.uzkassa.silen.rabbitmq.MqConstants;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMqProducer implements MqConstants {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void sendSyncInvoice(Payload dto) {
        send(dto, MqConstants.SYNC_INVOICE_QUEUE, 0);
    }

    public void sendShipmentInvoice(Payload dto) {
        send(dto, MqConstants.SHIPMENT_INVOICE_QUEUE, 1000);
    }

    public void sendShipmentAct(Payload dto) {
        send(dto, MqConstants.SHIPMENT_ACT_QUEUE, 1000);
    }

    public void sendShipmentItemsToWarehouse(Payload dto) {
        send(dto, MqConstants.SHIPMENT_ITEMS_QUEUE, 5000);
    }

    public void sendCustomerProduct(Payload dto) {
        send(dto, MqConstants.CUSTOMER_PRODUCT_QUEUE, MqConstants.DEFAULT_DELAY);
    }

    public void sendAddProduct(Payload dto) {
        send(dto, MqConstants.ADD_PRODUCT_QUEUE, MqConstants.DEFAULT_DELAY);
    }

    /**
     * Migrated to Kafka
     * @param payload
     */
    @Deprecated
    public void sendWarehouse(WarehousePayload payload) {
        String payloadString;
        try {
            payloadString = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return;
        }
        Message jsonMessage = MessageBuilder.withBody(payloadString.getBytes())
            .andProperties(MessagePropertiesBuilder.newInstance().setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setHeader(X_DELAY, DEFAULT_DELAY)
                .setHeader(TYPE_ID, "warehouse_payload").build()).build();
        rabbitTemplate.send(
            DELAY_EXCHANGE_NAME,
            WAREHOUSE_ROUTE,
            jsonMessage
        );
    }

    public void sendBatchAggregation(Payload payload) {
        send(payload, BATCH_AGGREGATION_QUEUE, 1000);
    }

    public void sendNotification(NotificationPayload payload) {
        String payloadString;
        try {
            payloadString = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return;
        }
        Message jsonMessage = MessageBuilder.withBody(payloadString.getBytes())
            .andProperties(MessagePropertiesBuilder.newInstance().setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setHeader(X_DELAY, DEFAULT_DELAY)
                .setHeader(TYPE_ID, "notification_payload").build()).build();
        rabbitTemplate.send(
            DELAY_EXCHANGE_NAME,
            NOTIFICATION_QUEUE,
            jsonMessage
        );
    }

    private void send(Payload payload, String queueName, long delay) {
        String payloadString;
        try {
            payloadString = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return;
        }
        Message jsonMessage = MessageBuilder.withBody(payloadString.getBytes())
            .andProperties(MessagePropertiesBuilder.newInstance().setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setHeader(X_DELAY, delay)
                .setHeader(TYPE_ID, "payload").build()).build();
        rabbitTemplate.send(
            DELAY_EXCHANGE_NAME,
            queueName,
            jsonMessage
        );
    }

    public void syncPackages(UserDTO payload) {
        rabbitTemplate.convertAndSend(
            SYNC_PACKAGES_START_QUEUE,
            payload
        );
    }

    public void resendAggregationToTuron(String id, long delay) {
        Payload payload = new Payload();
        payload.setId(id);
        send(payload, MqConstants.RESEND_AGGREGATION_QUEUE, delay);
    }

    public void resendBlockToTuron(String id, long delay) {
        Payload payload = new Payload();
        payload.setId(id);
        send(payload, MqConstants.RESEND_BLOCK_QUEUE, delay);
    }

    public void sendProductExportTask(ExportProductPayload filter) {
        String payloadString;
        try {
            payloadString = objectMapper.writeValueAsString(filter);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return;
        }
        Message jsonMessage = MessageBuilder.withBody(payloadString.getBytes())
            .andProperties(MessagePropertiesBuilder.newInstance()
                .setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setHeader(TYPE_ID, "export_products_queue").build()).build();
        rabbitTemplate.send(
            DELAY_EXCHANGE_NAME,
            EXPORT_PRODUCTS_QUEUE,
            jsonMessage
        );
    }

    public void sendProductImportTask(ImportProductPayload filter) {
        String payloadString;
        try {
            payloadString = objectMapper.writeValueAsString(filter);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return;
        }
        Message jsonMessage = MessageBuilder.withBody(payloadString.getBytes())
            .andProperties(MessagePropertiesBuilder.newInstance()
                .setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setHeader(TYPE_ID, "import_products_queue").build()).build();
        rabbitTemplate.send(
            DELAY_EXCHANGE_NAME,
            IMPORT_PRODUCTS_QUEUE,
            jsonMessage
        );
    }

    public void sendVatRateChanged(VatRatePayload vatRatePayload) {
        String payloadString;
        try {
            payloadString = objectMapper.writeValueAsString(vatRatePayload);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return;
        }
        Message jsonMessage = MessageBuilder.withBody(payloadString.getBytes())
            .andProperties(MessagePropertiesBuilder.newInstance()
                .setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setHeader(TYPE_ID, "vat_rate_changed_queue").build()).build();
        rabbitTemplate.send(
            DELAY_EXCHANGE_NAME,
            VAT_RATE_CHANGED_QUEUE,
            jsonMessage
        );
    }

    public void sendImportFromBasket(String id) {
        Payload payload = new Payload();
        payload.setId(id);
        send(payload, MqConstants.IMPORT_FROM_BASKET_QUEUE, 0);
    }

    public void sendPartyMarksSetApplied(Payload payload) {
        send(payload, PARTY_APPLIED_QUEUE, 0);
    }

    public void sendPartyMarks(Payload payload) {
        send(payload, PARTY_MARKS_QUEUE, MqConstants.DEFAULT_DELAY);
    }

    public void sendForSaveAggregationMarks(Payload payload) {
        send(payload, MqConstants.SAVE_AGGREGATION_MARKS_PAYLOAD, 0);
    }
    public void sendForSaveAggregationMarks(Payload payload, int delayInSeconds) {
        send(payload, MqConstants.SAVE_AGGREGATION_MARKS_PAYLOAD, delayInSeconds);
    }

    public void sendForSaveBlockMarks(Payload payload) {
        send(payload, MqConstants.SAVE_BLOCK_MARKS_PAYLOAD, 0);
    }

    public void sendFillShipmentItem(Payload dto) {
        send(dto, MqConstants.FILL_SHIPMENT_ITEM_ORDER, 1000);
    }

    public void sendExportReport(ExportReportPayload payload) {
        try {
            Message jsonMessage = MessageBuilder
                .withBody(objectMapper.writeValueAsString(payload).getBytes())
                .andProperties(
                    MessagePropertiesBuilder
                        .newInstance()
                        .setContentType(MediaType.APPLICATION_JSON_VALUE)
                        .setHeader(X_DELAY, DEFAULT_DELAY)
                        .setHeader(TYPE_ID, "export_report_payload")
                        .build()
                )
                .build();
            rabbitTemplate.send(DELAY_EXCHANGE_NAME, MqConstants.EXPORT_REPORT, jsonMessage);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    public void sendStoreIn(Payload payload) {
        send(payload, STORE_IN_COMPLETED_QUEUE, 0);
    }

    public void sendPartyAggregationFinished(Payload payload) {
        send(payload, PARTY_AGGREGATION_FINISHED_QUEUE, 300000);
    }


    public void sendPartyAggregation(Payload payload) {
        send(payload, PARTY_AGGREGATION_QUEUE, DEFAULT_DELAY);
    }

    public void sendAggregationStatusChangedEvent(AggregationStatusChangedPayload payload) {
        try {
            Message jsonMessage = MessageBuilder
                .withBody(objectMapper.writeValueAsString(payload).getBytes())
                .andProperties(
                    MessagePropertiesBuilder
                        .newInstance()
                        .setContentType(MediaType.APPLICATION_JSON_VALUE)
                        .setHeader(X_DELAY, DEFAULT_DELAY)
                        .setHeader(TYPE_ID, "aggregation_state_changed_payload")
                        .build()
                )
                .build();
            rabbitTemplate.send(DELAY_EXCHANGE_NAME, MqConstants.AGGREGATION_STATUS_CHANGED_QUEUE, jsonMessage);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    public void sendRoleChangedEvent(UserRolePermissionPayload payload) {
        String payloadString;
        try {
            payloadString = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return;
        }
        Message jsonMessage = MessageBuilder.withBody(payloadString.getBytes())
                .andProperties(MessagePropertiesBuilder.newInstance().setContentType(MediaType.APPLICATION_JSON_VALUE)
                        .setHeader(X_DELAY, DEFAULT_DELAY)
                        .setHeader(TYPE_ID, "user_permission_payload").build()).build();
        rabbitTemplate.send(
                DELAY_EXCHANGE_NAME,
                ROLE_CHANGED_QUEUE,
                jsonMessage
        );
    }
}
