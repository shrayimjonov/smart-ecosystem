package uz.uzkassa.smartpos.rabbitmq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.CompanyRegisterBranchesDTO;
import uz.uzkassa.smartpos.dto.TransferCatalogDTO;
import uz.uzkassa.smartpos.dto.catalog.BatchUpdateDTO;
import uz.uzkassa.smartpos.dto.catalog.Product1CCreateDto;
import uz.uzkassa.smartpos.dto.fashion.FashionRegisterCreateDto;
import uz.uzkassa.smartpos.dto.mq.*;
import uz.uzkassa.smartpos.dto.nic.InvoiceCallback;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.sales.SalesRabbitMQPayload;
import uz.uzkassa.smartpos.dto.warehouse.MarkingDetailUpdateStatusDto;
import uz.uzkassa.smartpos.dto.warehouse.ReceiptMarkingsPayload;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionTransferUpdateStatusDto;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.service.*;
import uz.uzkassa.smartpos.service.fashion.FashionRegisterService;
import uz.uzkassa.smartpos.service.warehouse.MarkingWarehouseService;
import uz.uzkassa.smartpos.social.TelegramBotApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMqConsumer implements MqConstants {
    private final ReceiptService receiptService;
    private final SupplyReceiptService supplyReceiptService;
    private final ProductService productService;
    private final ProductBranchService productBranchService;
    private final RegisterService registerService;
    private final CompanyService companyService;
    private final MarkingService markingService;
    private final InvoiceService invoiceService;
    private final LauncherService launcherService;
    private final CategoryService categoryService;
    private final PaymentService paymentService;
    private final FashionRegisterService fashionRegisterService;
    private final ProductItemService productItemService;
    private final TelegramBotApi telegramBotApi;
    private final FashionTransferService fashionTransferService;
    private final MarkingWarehouseService markingWarehouseService;
    private final SalesService salesService;
    private final ObjectMapper objectMapper;
    private final OneCCompanyService oneCCompanyService;
    private final TelegramService telegramService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = PRODUCT_CREATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = PRODUCT_CREATE_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void productImportQueue(Product1CCreateDto payload) {
        try {
            productService.createFor1C(payload);
        } catch (Exception e) {
            log.error("{} exception", PRODUCT_CREATE_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = RECEIPT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = RECEIPT_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void receiptQueue(ReceiptDTO payload) {
        try {
            receiptService.save(payload, null);
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException violationException) {
                Map<String, String> violations = new HashMap<>();
                if (violationException.getCause() != null && violationException.getCause() instanceof ConstraintViolationException constraintViolationException) {
                    Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
                    if (CollectionUtils.isNotEmpty(constraintViolations)) {
                        constraintViolations.forEach(constraintViolation -> {
                            violations.put("path", Objects.toString(constraintViolation.getPropertyPath()));
                            violations.put("message", constraintViolation.getMessage());
                            violations.put("value", Objects.toString(constraintViolation.getInvalidValue()));
                        });
                    }
                } else {
                    violations.put("message", violationException.getMessage());
                }
                sendMessageToTelegramBot(RECEIPT_QUEUE, e, violations);
            } else {
                sendMessageToTelegramBot(RECEIPT_QUEUE, e, payload);
            }
            log.error("{} exception", RECEIPT_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SUPPLY_RECEIPT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SUPPLY_RECEIPT_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void supplyReceiptQueue(ReceiptDTO dto) {
        try {
            supplyReceiptService.saveProductsUsingSupplyReceipt(dto);
        } catch (Exception e) {
            log.error("{} exception", SUPPLY_RECEIPT_QUEUE, e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = REGISTER_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = REGISTER_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void registerQueue(RegisterPayload payload) {
        try {
            registerService.doRegisterOperation(payload);
        } catch (Exception e) {
            sendMessageToTelegramBot(REGISTER_QUEUE, e, payload);
            log.error("{} exception", REGISTER_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = REGISTER_UPDATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = REGISTER_UPDATE_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void registerUpdateQueue(RegisterItemPayload payload) {
        try {
            registerService.updateRegister(payload);
        } catch (Exception e) {
            log.error("{} exception", REGISTER_UPDATE_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = INVOICE_UPDATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = INVOICE_UPDATE_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void invoiceUpdateQueue(InvoiceCallback payload) {
        try {
            invoiceService.onUpdateJob(payload);
        } catch (Exception e) {
            log.error("{} exception", INVOICE_UPDATE_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = BALANCE_HISTORY_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = BALANCE_HISTORY_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void balanceHistoryQueue(RegisterPayload payload) {
        try {
            registerService.createHistory(payload);
        } catch (Exception e) {
            log.error("{} exception", BALANCE_HISTORY_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = VAT_UPDATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = VAT_UPDATE_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void categoryVatUpdate(BatchUpdateDTO payload) {
        try {
            productBranchService.vatUpdate(payload);
        } catch (Exception e) {
            log.error("{} exception", VAT_UPDATE_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = COMMITTENT_TIN_UPDATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = COMMITTENT_TIN_UPDATE_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void committentTinUpdate(BatchUpdateDTO payload) {
        try {
            productBranchService.committentTinUpdate(payload);
        } catch (Exception e) {
            log.error("{} exception", COMMITTENT_TIN_UPDATE_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = PRODUCT_ORIGIN_UPDATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = PRODUCT_ORIGIN_UPDATE_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void productOriginUpdate(BatchUpdateDTO payload) {
        try {
            productBranchService.productOriginUpdate(payload);
        } catch (Exception e) {
            log.error("{} exception", PRODUCT_ORIGIN_UPDATE_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = CATEGORY_DELETE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = CATEGORY_DELETE_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void productOriginUpdate(CategoryDeletePayload payload) {
        try {
            categoryService.delete(payload);
        } catch (Exception e) {
            log.error("{} exception", CATEGORY_DELETE_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = CREATE_HOME_SERVICE_PRODUCT),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = CREATE_HOME_SERVICE_PRODUCT),
            containerFactory = "rabbitListenerContainerFactory")
    public void createHomeServiceProduct(Long branchId) {
        try {
            productService.createHomeServiceProduct(branchId);
        } catch (Exception e) {
            log.error("{} exception", CREATE_HOME_SERVICE_PRODUCT, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = CREATE_FASHION_CATALOG),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = CREATE_FASHION_CATALOG),
            containerFactory = "rabbitListenerContainerFactory")
    public void createFashionCatalog(final Long branchId) {
        try {
            categoryService.createFashionCatalog(branchId);
        } catch (Exception e) {
            log.error("{} exception", CREATE_FASHION_CATALOG, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = DELETE_PRODUCT_REFERENCES),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = DELETE_PRODUCT_REFERENCES),
            containerFactory = "rabbitListenerContainerFactory")
    public void deleteAllProductBranchReferences(DeleteProductBranchReferencesPayload payload) {
        try {
            productBranchService.deleteAllProductBranchReferences(payload.id(), payload.branchId());
        } catch (Exception e) {
            log.error("{} exception", DELETE_PRODUCT_REFERENCES, e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = MARK_UPDATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = MARK_UPDATE_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void markUpdate(BatchUpdateDTO payload) {
        try {
            productBranchService.markUpdate(payload);
        } catch (Exception e) {
            log.error("{} exception", MARK_UPDATE_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = CREATE_PAYMENT),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = CREATE_PAYMENT),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void createPaymentConsumer(final PaymentPayload payload) {
        try {
            paymentService.createPayment(payload);
        } catch (Exception e) {
            log.error("{} exception", CREATE_PAYMENT, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = CONFIRM_PAYMENT),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = CONFIRM_PAYMENT),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void confirmPaymentConsumer(final PaymentPayload payload) {
        try {
            paymentService.confirmPayment(payload);
        } catch (Exception e) {
            log.error("{} exception", CONFIRM_PAYMENT, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = RECEIPT_KAFKA_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = RECEIPT_KAFKA_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMin")
    public void receiptKafkaQueueConsumer(Payload payload) {
        try {
            receiptService.sendReceiptToKafka(payload.getId());
        } catch (Exception e) {
            sendMessageToTelegramBot(RECEIPT_KAFKA_QUEUE, e, payload);
            log.error("{} exception", RECEIPT_KAFKA_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = UPDATE_PINFL_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = UPDATE_PINFL_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void updatePinflQueueConsumer(Payload payload) {
        try {
            companyService.updatePinFl(payload);
        } catch (Exception e) {
            log.error("{} exception", UPDATE_PINFL_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = INCOME_BATCH_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = INCOME_BATCH_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void saveIncomeOfProductMarkings(Long id) {
        try {
            markingService.saveIncomeOfProductMarkings(id);
        } catch (Exception e) {
            log.error("{} exception", INCOME_BATCH_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = RECEIPT_MARKING_BATCH_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = RECEIPT_MARKING_BATCH_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void markingQueue(ReceiptMarkingsPayload value) {
        try {
            markingService.saveReceiptMarkings(value);
        } catch (Exception e) {
            log.error("{} exception", RECEIPT_MARKING_BATCH_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = UNBLOCK_TERMINALS_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = UNBLOCK_TERMINALS_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void unblockTerminalsQueue(CompanyUnblockPayload payload) {
        try {
            launcherService.unblockTerminals(payload.getCompanyIds());
        } catch (Exception e) {
            log.error("{} exception", UNBLOCK_TERMINALS_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = BRANCH_REGISTER_WITH_APAY),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = BRANCH_REGISTER_WITH_APAY),
            containerFactory = "rabbitListenerContainerFactory")
    public void unblockTerminalsQueue(CompanyRegisterBranchesDTO payload) {
        try {
            companyService.sendToApay(payload);
        } catch (Exception e) {
            log.error("{} exception", BRANCH_REGISTER_WITH_APAY, e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = FASHION_REGISTER_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = FASHION_REGISTER_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void fashionRegister(final FashionRegisterCreateDto payload) {
        try {
            fashionRegisterService.save(payload);
        } catch (Exception e) {
            log.error("{} exception", FASHION_REGISTER_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = FASHION_REPORT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = FASHION_REPORT_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void fashionRegister(final Long id) {
        try {
            fashionRegisterService.sendReceiptWarehouseDataToKafka(id);
        } catch (Exception e) {
            log.error("{} exception", FASHION_REPORT_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = UPDATE_PRODUCT_PRICES_BY_INCOME_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = UPDATE_PRODUCT_PRICES_BY_INCOME_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void updateProductPricesByIncome(final Long id) {
        try {
            productService.updateProductPricesByIncome(id);
        } catch (Exception e) {
            log.error("{} exception", UPDATE_PRODUCT_PRICES_BY_INCOME_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = PRODUCT_ITEMS_TRANSFER_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = PRODUCT_ITEMS_TRANSFER_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void productItemsTransferListener(final TransferCatalogDTO transferDto) {
        try {
            productItemService.transferItems(transferDto);
        } catch (Exception e) {
            log.error("{} exception", PRODUCT_ITEMS_TRANSFER_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = DELETE_FASHION_DATA_BY_COLOR_GROUP),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = DELETE_FASHION_DATA_BY_COLOR_GROUP),
            containerFactory = "rabbitListenerContainerFactory")
    public void deleteFashionDataByColorGroup(final Long id) {
        try {
            productItemService.deleteFashionDataByColorGroup(id);
        } catch (Exception e) {
            log.error("{} exception", DELETE_FASHION_DATA_BY_COLOR_GROUP, e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = DELETE_FASHION_DATA_BY_PRODUCT_ITEM),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = DELETE_FASHION_DATA_BY_PRODUCT_ITEM),
            containerFactory = "rabbitListenerContainerFactory")
    public void deleteFashionDataByProductItem(final Long id) {
        try {
            productItemService.deleteFashionDataByProductItem(id);
        } catch (Exception e) {
            log.error("{} exception", DELETE_FASHION_DATA_BY_PRODUCT_ITEM, e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = FASHION_TRANSFER_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = FASHION_TRANSFER_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void fashionTransferQueue(final FashionTransferUpdateStatusDto dto) {
        try {
            fashionTransferService.updateStatus(dto.id(), dto);
        } catch (Exception e) {
            log.error("{} exception", FASHION_TRANSFER_QUEUE, e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = MARKING_INCOME_SOLD_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = MARKING_INCOME_SOLD_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void markingWarehouseOperationQueue(final MarkingDetailUpdateStatusDto dto) {
        try {
            markingWarehouseService.updateStatus(dto, false, true, true);
        } catch (Exception e) {
            log.error("{} exception", MARKING_INCOME_SOLD_QUEUE, e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = RECEIPT_AUTO_PRINT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = RECEIPT_AUTO_PRINT_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void markingWarehouseOperationQueue(final Long id) {
        try {
            salesService.sendAutoPrint(id);
        } catch (Exception e) {
            sendMessageToTelegramBot(RECEIPT_AUTO_PRINT_QUEUE, e, id);
            log.error("{} exception", RECEIPT_AUTO_PRINT_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SALES_WAREHOUSE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SALES_WAREHOUSE_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void salesWarehouseQueue(String uid) {
        try {
            registerService.registerSales(uid);
        } catch (Exception e) {
            sendMessageToTelegramBot(SALES_WAREHOUSE_QUEUE, e, uid);
            log.error("{} exception", SALES_WAREHOUSE_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SALES_CLICKHOUSE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SALES_CLICKHOUSE_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void salesClickhouseQueue(SalesRabbitMQPayload payload) {
        try {
            salesService.sendToClickhouse(payload);
        } catch (Exception e) {
            sendMessageToTelegramBot(SALES_CLICKHOUSE_QUEUE, e, payload);
            log.error("{} exception", SALES_CLICKHOUSE_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SALES_RECEIPT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SALES_RECEIPT_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void createReceiptFromSales(String uid) {
        try {
            receiptService.createReceiptFromSales(uid);
        } catch (Exception e) {
            sendMessageToTelegramBot(SALES_RECEIPT_QUEUE, e, uid);
            log.error("{} exception", SALES_RECEIPT_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SALES_FROM_RECEIPT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SALES_FROM_RECEIPT_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void createSaleFromReceipt(String uid) {
        try {
            salesService.createSalesFromReceipt(uid);
        } catch (Exception e) {
            sendMessageToTelegramBot(SALES_FROM_RECEIPT_QUEUE, e, uid);
            log.error("{} exception", SALES_FROM_RECEIPT_QUEUE, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = ONE_C_RECEIPT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = ONE_C_RECEIPT_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void createOneCCompany(Long id) {
        try {
            oneCCompanyService.create(id);
        } catch (Exception e) {
            log.error("{} exception", ONE_C_RECEIPT_QUEUE, e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = NON_FISCAL_RECEIPT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = NON_FISCAL_RECEIPT_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void sendNonFiscalReceiptToTelegram(Long id) {
        try {
            telegramService.sendNonFiscalReceiptMessage(id);
        } catch (Exception e) {
            log.error("{} exception", NON_FISCAL_RECEIPT_QUEUE, e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = RECEIPT_WRONG_PAYMENTS_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = RECEIPT_WRONG_PAYMENTS_QUEUE),
            containerFactory = "rabbitListenerContainerFactory")
    public void sendWrongPaymentsToTelegram(String payload) {
        try {
            telegramService.sendWrongPaymentsToTelegram(payload);
        } catch (Exception e) {
            log.error("{} exception", RECEIPT_WRONG_PAYMENTS_QUEUE, e);
        }
    }

    private void sendMessageToTelegramBot(String queue, Exception exception, Object argument) {
        try {
            StringBuilder text = new StringBuilder();
            text.append("<b>Type:</b> ").append("#RabbitMQ\n");
            text.append("<b>Queue:</b> #").append(queue).append("\n");
            try {
                text.append("<b>Argument:</b> ").append(objectMapper.writeValueAsString(argument)).append("\n");
            } catch (JsonProcessingException e) {
                log.error("{} exception", exception, e);
            }
            text.append("<b>Exception:</b> ").append(exception.getMessage()).append("\n");
            if (exception.getCause() != null) {
                text.append("<b>Cause:</b> ").append(exception.getCause().getMessage()).append("\n");
            }
            telegramBotApi.sendMessageToAdminChannel(text.toString());
        } catch (Exception ignored) {
        }
    }
}
