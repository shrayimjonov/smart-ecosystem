package uz.uzkassa.silen.rabbitmq.consumer;

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
import uz.uzkassa.silen.dto.UserDTO;
import uz.uzkassa.silen.dto.mq.*;
import uz.uzkassa.silen.dto.partyaggregation.AggregationStatusChangedPayload;
import uz.uzkassa.silen.rabbitmq.MqConstants;
import uz.uzkassa.silen.service.*;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqConsumer implements MqConstants {
    WarehouseService warehouseService;
    InvoiceService invoiceService;
    AcceptanceTransferActService actService;
    AggregationService aggregationService;
    BaseAggregationService baseAggregationService;
    NotificationService notificationService;
    CustomerService customerService;
    ProductService productService;
    ShipmentService shipmentService;
    BatchService batchService;
    PartyService partyService;
    ExportReportService exportReportService;
    ShipmentItemService shipmentItemService;
    StoreInItemMarkService storeInItemMarkService;
    PartyAggregationService partyAggregationService;
    AggregationHistoryService aggregationHistoryService;
    BlockService blockService;
    DailyOperationsService dailyOperationsService;
    PermissionService permissionService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SYNC_INVOICE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SYNC_INVOICE_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void syncInvoiceQueue(Payload payload) {
        try {
            invoiceService.sync(payload.getLongId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Warehouse queue notification", e);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = EXPORT_REPORT),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = EXPORT_REPORT
            ),
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void exportReport(ExportReportPayload payload) {
        try {
            exportReportService.startGenerate(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Creating export-report error: ", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = ACCEPTANCE_TRANSFER_ACT_SEND_TO_SUPPLY_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = ACCEPTANCE_TRANSFER_ACT_SEND_TO_SUPPLY_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void sendAcceptanceTransferActToSupplyQueue(Payload payload) {
        try {
            actService.sendAcceptanceTransferActToSupplyQueue(payload.getId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Send Acceptance Transfer Act To Supply Queue Error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SHIPMENT_INVOICE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SHIPMENT_INVOICE_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void shipmentInvoiceQueue(Payload payload) {
        try {
            invoiceService.createShipmentInvoice(payload.getId(), true);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Shipment Invoice Queue error", e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SHIPMENT_ACT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SHIPMENT_ACT_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void shipmentActQueue(Payload payload) {
        try {
            actService.convertShipmentToAct(payload.getId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Shipment Act Queue error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SHIPMENT_ITEMS_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SHIPMENT_ITEMS_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void shipmentItemsQueue(Payload payload) {
        try {
            shipmentService.sendClosedShipment(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Shipment Items Queue error", e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = CUSTOMER_PRODUCT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = CUSTOMER_PRODUCT_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void customerProductQueue(Payload payload) {
        try {
            customerService.createProducts(payload.getId(), payload.getLongId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Customer Product Queue error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = ADD_PRODUCT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = ADD_PRODUCT_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void addProductQueue(Payload payload) {
        try {
            customerService.createProduct(payload.getId(), payload.getExtraId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Add product queue error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = WAREHOUSE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = WAREHOUSE_ROUTE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void warehouseQueue(WarehousePayload payload) {
        try {
            warehouseService.calculate(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Warehouse queue error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = WAREHOUSE_AGGREGATION_HISTORY_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = WAREHOUSE_ROUTE),
            containerFactory = "rabbitListenerContainerFactory")
    public void createHistoryFromWarehousePayload(WarehousePayload createHistoryPayload) {
        try {
            aggregationHistoryService.create(createHistoryPayload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Create AggregationHistory from Warehouse route error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = BATCH_AGGREGATION_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = BATCH_AGGREGATION_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void batchAggregationQueue(Payload payload) {
        try {
            batchService.batchAggregationRecursion(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Batch Aggregation Queue error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = NOTIFICATION_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = NOTIFICATION_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void notificationQueue(NotificationPayload payload) {
        try {
            notificationService.createNotification(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Notification Queue error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SYNC_PACKAGES_START_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SYNC_PACKAGES_START_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void syncPackages(UserDTO payload) {
        try {
            log.debug("UserId: {}, login: {}", payload.getId(), payload.getLogin());
            productService.checkProductChanges(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("MxikListDTO queue sync", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = RESEND_AGGREGATION_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = RESEND_AGGREGATION_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void resendAggregationHasError(Payload payload) {
        try {
            aggregationService.sendAggregation(payload.getId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Resend Aggregation Has Error ", e);
        }

    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = RESEND_BLOCK_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = RESEND_BLOCK_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void resendBlockHasError(Payload payload) {
        try {
            blockService.sendAggregation(payload.getId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Resend Aggregation Has Error ", e);
        }

    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = EXPORT_PRODUCTS_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = EXPORT_PRODUCTS_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void productExport(ExportProductPayload payload) {
        try {
            productService.exportProductsToExcel(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Export products to excel file something went wrong", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = IMPORT_PRODUCTS_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = IMPORT_PRODUCTS_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void productImport(ImportProductPayload payload) {
        try {
            productService.startImport(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Import products from excel file something went wrong", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = VAT_RATE_CHANGED_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = VAT_RATE_CHANGED_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void updateProductVatRates(VatRatePayload vatRatePayload) {
        try {
            productService.updateVatRates(vatRatePayload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Products vat rates changed", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = IMPORT_FROM_BASKET_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = IMPORT_FROM_BASKET_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void importFromBasket(Payload payload) {
        try {
            productService.importFromBasket(payload.getId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Import from basket queue notification", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = PARTY_APPLIED_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = PARTY_APPLIED_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void sendPartyMarksSetApplied(Payload payload) {
        try {
            partyService.sendPartyMarksSetApplied(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Import from basket queue error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = PARTY_MARKS_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = PARTY_MARKS_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void partyMarks(Payload payload) {
        try {
            partyService.partyMarks(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Party marks queue error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SAVE_AGGREGATION_MARKS_PAYLOAD),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SAVE_AGGREGATION_MARKS_PAYLOAD),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void sendForSaveAggregationMarks(Payload payload) {
        try {
            baseAggregationService.saveMarks(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Send For Save Aggregation Marks error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SAVE_BLOCK_MARKS_PAYLOAD),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SAVE_BLOCK_MARKS_PAYLOAD),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void sendForSaveBlockMarks(Payload payload) {
        try {
            baseAggregationService.saveBlockMarks(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Send For Save Aggregation Marks error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = FILL_SHIPMENT_ITEM_ORDER),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = FILL_SHIPMENT_ITEM_ORDER),
            containerFactory = "rabbitListenerContainerFactory")
    public void sendFillShipmentItem(Payload payload) {
        try {
            shipmentItemService.fillWithBox(payload.getLongId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Send Fill Shipment Item error ", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = STORE_IN_COMPLETED_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = STORE_IN_COMPLETED_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void sendStoreInCompleted(Payload payload) {
        try {
            storeInItemMarkService.marksToStore(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Store in completing error", e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = PARTY_AGGREGATION_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = PARTY_AGGREGATION_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void sendPartyAggregation(Payload payload) {
        log.info("PARTY_AGGREGATION_QUEUE Listening  {}", payload);
        try {
            partyAggregationService.partyAggregate(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Party aggregation error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = PARTY_AGGREGATION_FINISHED_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = PARTY_AGGREGATION_FINISHED_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void sendPartyAggregationFinished(Payload payload) {
        try {
            partyAggregationService.partyAggregationFinished(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Party aggregation error", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = AGGREGATION_STATUS_CHANGED_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = AGGREGATION_STATUS_CHANGED_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void sendAggregationStatusChanged(AggregationStatusChangedPayload payload) {
        try {
            partyAggregationService.partyAggregationStatusCheck(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Party aggregation error", e);
        }
    }
    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = ROLE_CHANGED_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = ROLE_CHANGED_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void roleChangedEvent(UserRolePermissionPayload payload) {
        try {
            permissionService.roleChangedEvent(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Role changed error", e);
        }
    }
}
