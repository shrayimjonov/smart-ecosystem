package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.*;
import uz.uzkassa.silen.dto.ProductDTO;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderDTO;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderProductDTO;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.onec.Order1CDTO;
import uz.uzkassa.silen.integration.onec.OrderProduct1CDTO;
import uz.uzkassa.silen.repository.pg.*;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Order1CService extends BaseService {
    StoreRepository storeRepository;
    ProductService productService;
    PurchaseOrderService purchaseOrderService;
    ContractRepository contractRepository;
    ProductRepository productRepository;
    PurchaseOrderProductService purchaseOrderProductService;
    SerialNumberRepository serialNumberRepository;
    VatRateRepository vatRateRepository;
    CustomerRepository customerRepository;

    @Transactional
    public Order1CDTO save(Order1CDTO order1CDTO) {
        final Organization organization = getCurrentOrganization();
        final Store store = storeRepository.findById(order1CDTO.getWarehouseId()).orElseThrow(() -> new BadRequestException("Склад не найден"));
        final Customer customer = customerRepository.findFirstByOrganizationIdAndTinAndDeletedIsFalse(getCurrentOrganizationId(), order1CDTO.getCustomerTin())
            .orElseThrow(() -> new BadRequestException("Клиент не найден"));
        Contract contract = null;
        if (order1CDTO.getContract() != null) {
            contract = contractRepository.findFirstByOrganizationIdAndCustomerTinAndNumberAndDateAndDeletedFalse(organization.getId(), order1CDTO.getCustomerTin(), order1CDTO.getContract().getNumber(), order1CDTO.getContract().getDate())
                .orElseThrow(() -> new BadRequestException("Контракт с клиентом не найден"));
        }
        PurchaseOrderDTO orderDTO = new PurchaseOrderDTO();
        orderDTO.setFromOrganizationId(organization.getId());
        orderDTO.setCustomerId(customer.getId());
        orderDTO.setStoreId(store.getId());
        if (contract != null) {
            orderDTO.setContractId(contract.getId());
        }
        orderDTO.setOrderDate(order1CDTO.getOrderDate());
        orderDTO.setNumber(order1CDTO.getOrderNumber());
        Long purchaseOrderId = purchaseOrderService.create(orderDTO);

        for (OrderProduct1CDTO order1CProductsItem : order1CDTO.getProducts()) {
            // create if not exists
            ProductDTO orCreateProduct = findOrCreateProduct(order1CProductsItem, organization);
            if (orCreateProduct == null) continue;

            PurchaseOrderProductDTO orderProductDTO = new PurchaseOrderProductDTO();
            orderProductDTO.setOrderId(purchaseOrderId);
            orderProductDTO.setProductId(orCreateProduct.getId());
            orderProductDTO.setProfitRate(order1CProductsItem.getProfitRate());
            orderProductDTO.setCount(order1CProductsItem.getCount().intValue());
            orderProductDTO.setSumma(order1CProductsItem.getSumma());
            orderProductDTO.setBaseSumma(order1CProductsItem.getSumma());
            orderProductDTO.setWithoutVat(order1CProductsItem.getDeliverySumWithVat());
            vatRateRepository.findFirstByCodeAndDeletedIsFalse(String.valueOf(order1CProductsItem.getVatRate()))
                .ifPresent(vatRate -> orderProductDTO.setVatRate(vatRate.getId()));
            serialNumberRepository.findBySerialNumberAndOrganizationId(order1CProductsItem.getSerial(), organization.getId())
                .ifPresent(serialNumber -> orderProductDTO.setSerialId(serialNumber.getId()));

            try {
                purchaseOrderProductService.create(orderProductDTO);
            } catch (BadRequestException e) {
                log.error("Purchase order product is not created {}", e.getMessage());
            }
        }
        return order1CDTO;
    }

    @Transactional
    public ProductDTO findOrCreateProduct(OrderProduct1CDTO order1CProducts, Organization organization) {
        Optional<Product> productOptional = productRepository.findFirstByBarcodeAndOrganizationIdAndDeletedIsFalse(order1CProducts.getBarcode(), organization.getId());
        if (productOptional.isPresent()) {
            return productOptional.get().toDto();
        } else {
            ProductDTO silenProductDTO = new ProductDTO();
            if (organization.getProductGroup().getProductTypes().length > 0) {
                silenProductDTO.setType(organization.getProductGroup().getProductTypes()[0]);
            }
            silenProductDTO.setBarcode(order1CProducts.getBarcode());
            silenProductDTO.setOrganizationId(organization.getId());
            silenProductDTO.setName(order1CProducts.getName());
            silenProductDTO.setShortName(order1CProducts.getName());
            silenProductDTO.setBarcode(order1CProducts.getBarcode());
            silenProductDTO.setCatalogCode(order1CProducts.getCatalogCode());
            silenProductDTO.setCatalogName(order1CProducts.getCatalogName());
            silenProductDTO.setPackageCode(order1CProducts.getPackageCode());
            silenProductDTO.setPackageName(order1CProducts.getPackageName());
            silenProductDTO.setPrice(order1CProducts.getBaseSumma());
            silenProductDTO.setVatRate(order1CProducts.getVatRate());
            silenProductDTO.setQtyInAggregation(0);
            silenProductDTO.setQtyInBlock(0);
            silenProductDTO.setBasePrice(order1CProducts.getBaseSumma());
            silenProductDTO.setProfitRate(order1CProducts.getProfitRate());
            try {
                return productService.save(silenProductDTO);
            } catch (BadRequestException e) {
                log.error("Product is not created {}", e.getMessage());
            }
            return null;
        }
    }
}
