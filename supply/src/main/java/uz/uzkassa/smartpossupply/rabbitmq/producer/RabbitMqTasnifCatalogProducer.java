package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.CreateCatalogFromTasnifCatalogPayload;
import uz.uzkassa.smartpossupply.dto.payload.ProductPackageSyncPayload;
import uz.uzkassa.smartpossupply.dto.payload.SyncTasnifCatalogByProductPayload;
import uz.uzkassa.smartpossupply.dto.payload.SyncTasnifCatalogPayload;

@Component
public class RabbitMqTasnifCatalogProducer extends RabbitMqBaseProducer {

    public void createCatalogFromTasnifCatalogQueue(CreateCatalogFromTasnifCatalogPayload payload, int delay) {
        sendToQueue(
            payload,
            CREATE_CATALOG_FROM_TASNIF_CATALOG_QUEUE,
            CREATE_CATALOG_FROM_TASNIF_CATALOG_PAYLOAD,
            delay
        );
    }

    public void createCatalogFromTasnifCatalogQueue(CreateCatalogFromTasnifCatalogPayload payload) {
        createCatalogFromTasnifCatalogQueue(payload, 2);
    }

    public void syncTasnifCatalogQueue(SyncTasnifCatalogPayload payload) {
        sendToQueue(payload, SYNC_TASNIF_CATALOG_QUEUE, SYNC_TASNIF_CATALOG_PAYLOAD);
    }

    public void syncTasnifCatalogByProductQueue(SyncTasnifCatalogByProductPayload syncTasnifCatalogByProductPayload) {
        syncTasnifCatalogByProductQueue(syncTasnifCatalogByProductPayload, 2);
    }

    public void syncTasnifCatalogByProductQueue(SyncTasnifCatalogByProductPayload payload, int delay) {
        sendToQueue(
            payload,
            SYNC_TASNIF_CATALOG_BY_PRODUCT_QUEUE,
            SYNC_TASNIF_CATALOG_BY_PRODUCT_PAYLOAD,
            delay
        );
    }

    public void syncProductPackage(ProductPackageSyncPayload payload) {
        sendToQueue(
            payload,
                SYNC_PRODUCT_PACKAGE_QUEUE,
            SYNC_PRODUCT_PACKAGE_PAYLOAD
        );
    }
}
