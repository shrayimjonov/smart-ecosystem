package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.product.ProductCapacityDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductLookUpDTO;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.*;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class StockInProductService extends BaseService {

    StockInProductRepository stockInProductRepository;
    StockProductRepository stockProductRepository;
    StockProductAggregationRepository stockProductAggregationRepository;
    StockProductAggregationBlockRepository stockProductAggregationBlockRepository;
    ProductRepository productRepository;

    public ProductCapacityDTO getProduct(Long branchId, Long warehouseId, String barcode, String markCode, String aggregationCode, String aggregationBlockCode) {
        ProductCapacityDTO productCapacityDTO = null;
        if (StringUtils.hasText(barcode)) {
            productCapacityDTO = stockInProductRepository.getProduct(branchId, warehouseId, barcode);
            if (productCapacityDTO == null) {
                Product product = productRepository.findFirstByBarcodeAndBranchIdAndDeletedIsFalse(barcode, branchId).orElse(null);
                if (product != null) {
                    productCapacityDTO = new ProductCapacityDTO(product.getId(), product.getName(), product.getCapacity());
                }
            }
        }
        if (StringUtils.hasText(markCode)) {
            String barcodeFromMarkCode = MarkUtils.getBarcodeFromMarkCode(markCode);
            productCapacityDTO = stockInProductRepository.getProduct(branchId, warehouseId, barcodeFromMarkCode);
            if (productCapacityDTO == null) {
                Product product = productRepository.findFirstByBarcodeAndBranchIdAndDeletedIsFalse(barcodeFromMarkCode, branchId).orElse(null);
                if (product != null) {
                    productCapacityDTO = new ProductCapacityDTO(product.getId(), product.getName(), product.getCapacity());
                }
            }
        }
        if (StringUtils.hasText(aggregationCode)) {
            productCapacityDTO = stockProductAggregationRepository.getProduct(branchId, warehouseId, aggregationCode, TransferType.IN);
        }
        if (StringUtils.hasText(aggregationBlockCode)) {
            productCapacityDTO = stockProductAggregationBlockRepository.getProduct(branchId, warehouseId, aggregationBlockCode, TransferType.IN);
        }
        return productCapacityDTO;
    }

    public StockProductLookUpDTO getProductDetails(Long branchId, Long warehouseId, Long productId) {
        BaseFilter filter = new BaseFilter();
        filter.setBranchId(branchId);
        filter.setWarehouseId(warehouseId);
        return stockProductRepository.getProduct(productId, filter);
    }
}
