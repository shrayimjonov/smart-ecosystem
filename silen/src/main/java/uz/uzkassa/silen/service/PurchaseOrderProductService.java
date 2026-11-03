package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.PurchaseOrder;
import uz.uzkassa.silen.domain.PurchaseOrderProduct;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderProductDTO;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderProductDetailDTO;
import uz.uzkassa.silen.enumeration.PurchaseOrderStatus;
import uz.uzkassa.silen.repository.pg.PurchaseOrderProductRepository;
import uz.uzkassa.silen.repository.pg.PurchaseOrderRepository;

import java.util.Optional;

/**
 * Service Implementation for managing {@link PurchaseOrder}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PurchaseOrderProductService extends BaseService{
    PurchaseOrderProductRepository purchaseOrderProductRepository;
    PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public Long create(PurchaseOrderProductDTO orderProductDTO) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderProductDTO.getOrderId()).orElseThrow(notFound());
        if (!PurchaseOrderStatus.NEW.equals(purchaseOrder.getStatus())) {
            throw badRequest("Заказ уже '" + purchaseOrder.getStatus().getText() + "'").get();
        }

        purchaseOrderProductRepository.findFirstByOrderIdAndProductId(orderProductDTO.getOrderId(), orderProductDTO.getProductId())
            .ifPresent(purchaseOrderProduct -> {
                throw badRequest("Продукция уже добавлен").get();
            });

        PurchaseOrderProduct purchaseOrderProduct = new PurchaseOrderProduct();
        purchaseOrderProduct.setOrderId(orderProductDTO.getOrderId());
        purchaseOrderProduct.setProductId(orderProductDTO.getProductId());
        purchaseOrderProduct.setSerialId(orderProductDTO.getSerialId());
        purchaseOrderProduct.setQuantity(orderProductDTO.getCount());
        purchaseOrderProduct.setVatRateId(orderProductDTO.getVatRate());
        purchaseOrderProduct.setVatSum(orderProductDTO.getVatSum());
        purchaseOrderProduct.setBaseSumma(orderProductDTO.getBaseSumma());
        purchaseOrderProduct.setSumma(orderProductDTO.getSumma());
        purchaseOrderProduct.setProfitRate(orderProductDTO.getProfitRate());
        purchaseOrderProduct.setWithoutVat(orderProductDTO.getWithoutVat());
        purchaseOrderProductRepository.save(purchaseOrderProduct);
        cacheService.evictCollectionData("uz.uzkassa.silen.domain.PurchaseOrder.products", orderProductDTO.getOrderId());
        return purchaseOrderProduct.getId();
    }

    @Transactional
    public Long update(Long id, PurchaseOrderProductDTO orderProductDTO) {
        PurchaseOrderProduct purchaseOrderProduct = purchaseOrderProductRepository.findById(id).orElseThrow(notFound());
        PurchaseOrder purchaseOrder = purchaseOrderProduct.getOrder();
        if (!PurchaseOrderStatus.NEW.equals(purchaseOrder.getStatus())) {
            throw badRequest("Заказ уже '" + purchaseOrder.getStatus().getText() + "'").get();
        }
        Optional<PurchaseOrderProduct> orderProduct = purchaseOrderProductRepository.findFirstByIdNotAndOrderIdAndProductId(id, orderProductDTO.getOrderId(), orderProductDTO.getProductId());
        if (orderProduct.isPresent()) {
            throw badRequest("Продукция уже добавлен").get();
        }
        purchaseOrderProduct.setProductId(orderProductDTO.getProductId());
        purchaseOrderProduct.setSerialId(orderProductDTO.getSerialId());
        purchaseOrderProduct.setQuantity(orderProductDTO.getCount());
        purchaseOrderProduct.setVatRateId(orderProductDTO.getVatRate());
        purchaseOrderProduct.setVatSum(orderProductDTO.getVatSum());
        purchaseOrderProduct.setBaseSumma(orderProductDTO.getBaseSumma());
        purchaseOrderProduct.setSumma(orderProductDTO.getSumma());
        purchaseOrderProduct.setProfitRate(orderProductDTO.getProfitRate());
        purchaseOrderProduct.setWithoutVat(orderProductDTO.getWithoutVat());
        purchaseOrderProductRepository.save(purchaseOrderProduct);
        cacheService.evictCollectionData("uz.uzkassa.silen.domain.PurchaseOrder.products", orderProductDTO.getOrderId());
        return purchaseOrderProduct.getId();
    }

    public PurchaseOrderProductDetailDTO findOne(Long id) {
        PurchaseOrderProduct purchaseOrderProduct = purchaseOrderProductRepository.findById(id).orElseThrow(notFound());
        return purchaseOrderProduct.toDto();
    }

    @Transactional
    public void delete(Long id) {
        purchaseOrderProductRepository.findById(id).ifPresent(purchaseOrderProduct -> {
            purchaseOrderProductRepository.delete(purchaseOrderProduct);
            cacheService.evictCollectionData("uz.uzkassa.silen.domain.PurchaseOrder.products", purchaseOrderProduct.getOrderId());
        });
    }
}
