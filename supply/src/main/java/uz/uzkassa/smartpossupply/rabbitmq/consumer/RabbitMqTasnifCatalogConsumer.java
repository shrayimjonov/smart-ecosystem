package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.CreateCatalogFromTasnifCatalogPayload;
import uz.uzkassa.smartpossupply.dto.payload.ProductPackageSyncPayload;
import uz.uzkassa.smartpossupply.dto.payload.SyncTasnifCatalogByProductPayload;
import uz.uzkassa.smartpossupply.dto.payload.SyncTasnifCatalogPayload;
import uz.uzkassa.smartpossupply.service.CategoryService;
import uz.uzkassa.smartpossupply.service.ProductSynchronisationService;

@Component
@Slf4j
public class RabbitMqTasnifCatalogConsumer implements MqConstants {

    private final CategoryService categoryService;
    private final ProductSynchronisationService productSynchronisationService;

    public RabbitMqTasnifCatalogConsumer(CategoryService categoryService, ProductSynchronisationService productSynchronisationService) {
        this.categoryService = categoryService;
        this.productSynchronisationService = productSynchronisationService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = SYNC_TASNIF_CATALOG_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = SYNC_TASNIF_CATALOG_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void syncTasnifCatalogQueue(SyncTasnifCatalogPayload syncTasnifCatalogPayload) {
        try {
            productSynchronisationService.syncTasnifCatalog(syncTasnifCatalogPayload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Sync tasnif catalog queue error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = CREATE_CATALOG_FROM_TASNIF_CATALOG_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = CREATE_CATALOG_FROM_TASNIF_CATALOG_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void createCatalogFromTasnifCatalogQueue(CreateCatalogFromTasnifCatalogPayload createCatalogFromTasnifCatalogPayload) {
        try {
            categoryService.createCatalogFromTasnifCatalogQueue(createCatalogFromTasnifCatalogPayload);
        } catch (Exception e) {
            log.error("Create tasnif catalog queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Create tasnif catalog queue error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = SYNC_TASNIF_CATALOG_BY_PRODUCT_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = SYNC_TASNIF_CATALOG_BY_PRODUCT_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void syncTasnifCatalogByProductQueue(SyncTasnifCatalogByProductPayload syncTasnifCatalogByProductPayload) {
        try {
            productSynchronisationService.syncTasnifCatalogByProduct(syncTasnifCatalogByProductPayload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Sync tasnif catalog by product queue error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = SYNC_PRODUCT_PACKAGE_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = SYNC_PRODUCT_PACKAGE_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void syncTasnifProductPackage(ProductPackageSyncPayload payload) {
        try {
            productSynchronisationService.syncPackage(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Sync tasnif package queue error: " + e.getMessage());
        }
    }
}
