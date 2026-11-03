package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerial;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerialWarehouse;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.report.ProductSerialReportDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialDetailDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialListDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialLookUpDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.ProductSerialStatus;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.ProductSerialFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.interfaces.CrudAdvanced;
import uz.uzkassa.smartpossupply.repository.ProductSerialRepository;
import uz.uzkassa.smartpossupply.repository.ProductSerialWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.StockAdjustmentProductRepository;
import uz.uzkassa.smartpossupply.repository.StockTransferProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.05.2023
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductSerialService extends BaseService implements CrudAdvanced<Long, ProductSerialDTO, ProductSerialDetailDTO, ProductSerialListDTO, ProductSerialFilter, ProductSerialStatus, ProductSerialLookUpDTO> {

    ProductSerialRepository productSerialRepository;
    ProductSerialWarehouseRepository productSerialWarehouseRepository;
    StockTransferProductRepository stockTransferProductRepository;
    StockAdjustmentProductRepository stockAdjustmentProductRepository;

    @Override
    public void validate(ProductSerialDTO productSerialDTO) {
        if (StringUtils.isEmpty(productSerialDTO.getSerialNumber())) {
            throw validationService.badRequestExceptionThrow("Серия номер требуется").get();
        }
        if (productSerialDTO.getProductId() == null) {
            throw validationService.badRequestExceptionThrow("Продукт требуется").get();
        }
        if (productSerialDTO.getId() == null && productSerialRepository.existsByProductIdAndSerialNumberAndCompanyIdAndDeletedIsFalse(productSerialDTO.getProductId(), productSerialDTO.getSerialNumber(), getCurrentCompanyId())) {
            throw validationService.conflictExceptionThrow(localizationService.localize(MessageConstants.ENTITY_ALREADY_EXIST_BY, new Object[]{
                    localizationService.localize(ProductSerial.class.getSimpleName()), productSerialDTO.getSerialNumber()
            })).get();
        }
        if (productSerialDTO.getId() != null && productSerialRepository.existsByProductIdAndSerialNumberAndCompanyIdAndDeletedIsFalseAndIdNot(productSerialDTO.getProductId(), productSerialDTO.getSerialNumber(), getCurrentCompanyId(), productSerialDTO.getId())) {
            throw validationService.conflictExceptionThrow(localizationService.localize(MessageConstants.ENTITY_ALREADY_EXIST_BY, new Object[]{
                    localizationService.localize(ProductSerial.class.getSimpleName()), productSerialDTO.getSerialNumber()
            })).get();
        }
    }

    @Override
    @Transactional
    public Long create(ProductSerialDTO productSerialDTO) {
        this.validate(productSerialDTO);
        return this.createEntity(productSerialDTO).getId();
    }

    private ProductSerial createEntity(ProductSerialDTO productSerialDTO) {
        ProductSerial productSerial = new ProductSerial();
        productSerial.setSerialNumber(productSerialDTO.getSerialNumber());
        productSerial.setProductId(productSerialDTO.getProductId());
        productSerial.setQty(productSerialDTO.getQty());
        productSerial.setBasePrice(productSerialDTO.getBaseSumma());
        productSerial.setCompanyId(getCurrentCompanyId());
        productSerialRepository.save(productSerial);
        return productSerial;
    }

    @Transactional
    public ProductSerial createOrGet(ProductSerialDTO productSerialDTO) {
        ProductSerial productSerial = productSerialRepository.findFirstByProductIdAndSerialNumberAndCompanyIdAndDeletedIsFalse(productSerialDTO.getProductId(), productSerialDTO.getSerialNumber(), getCurrentCompanyId()).orElse(null);
        if (productSerial == null) {
            productSerial = createEntity(productSerialDTO);
        }
        productSerial.setBasePrice(productSerialDTO.getBaseSumma());
        return productSerialRepository.save(productSerial);
    }

    @Override
    @Transactional
    public Long update(Long id, ProductSerialDTO productSerialDTO) {
        return productSerialRepository.findById(id).map(productSerial -> {
            if (ProductSerialStatus.PRODUCED.equals(productSerial.getStatus())) {
                throw validationService.badRequestExceptionThrow("Серия уже завершён").get();
            }
            productSerial.setProductId(productSerialDTO.getProductId());
            productSerial.setSerialNumber(productSerialDTO.getSerialNumber());
            productSerial.setQty(productSerialDTO.getQty());
            productSerial.setBasePrice(productSerialDTO.getBaseSumma());
            productSerialRepository.save(productSerial);
            return productSerial.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(ProductSerial.class.getSimpleName(), "id", id));
    }


    @Override
    public ProductSerialDetailDTO get(Long id) {
        return productSerialRepository.findById(id).map(serial -> {
            ProductSerialDetailDTO productSerialDetailDTO = new ProductSerialDetailDTO();
            productSerialDetailDTO.setId(serial.getId());
            productSerialDetailDTO.setSerialNumber(serial.getSerialNumber());
            productSerialDetailDTO.setCreatedDate(serial.getCreatedDate());
            productSerialDetailDTO.setQty(serial.getQty());
            productSerialDetailDTO.setBasePrice(serial.getBasePrice());
            productSerialDetailDTO.setStatus(serial.getStatus());
            productSerialDetailDTO.setProduct(serial.getProduct().toProductCommonDTO());
            return productSerialDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(ProductSerial.class.getSimpleName(), "id", id));
    }

    @Override
    public Page<ProductSerialListDTO> getList(ProductSerialFilter filter) {
        return productSerialRepository.getResultList(filter)
                .map(serial -> {
                    ProductSerialListDTO productSerialListDTO = new ProductSerialListDTO();
                    productSerialListDTO.setId(serial.getId());
                    productSerialListDTO.setSerialNumber(serial.getSerialNumber());
                    productSerialListDTO.setQty(serial.getQty());
                    productSerialListDTO.setBasePrice(serial.getBasePrice());
                    productSerialListDTO.setCreatedDate(serial.getCreatedDate());
                    productSerialListDTO.setStatus(serial.getStatus());
                    productSerialListDTO.setProduct(serial.getProduct().toProductCommonDTO());
                    return productSerialListDTO;
                });
    }

    @Override
    public List<ProductSerialLookUpDTO> lookUp(ProductSerialFilter filter) {
        return productSerialRepository.getResultList(filter)
                .stream()
                .map(productSerial -> {
                    ProductSerialLookUpDTO productSerialLookUpDTO = new ProductSerialLookUpDTO();
                    productSerialLookUpDTO.setId(productSerial.getId());
                    productSerialLookUpDTO.setProduct(productSerial.getProduct().toProductCommonDTO());
                    productSerialLookUpDTO.setSerialNumber(productSerial.getSerialNumber());
                    productSerialLookUpDTO.setBaseSumma(productSerial.getBasePrice());
                    if (filter.getWarehouseId() != null) {
                        productSerialLookUpDTO.setQty(productSerialWarehouseRepository.getQty(filter.getWarehouseId(), productSerial.getId()));
                    } else {
                        productSerialLookUpDTO.setQty(productSerialWarehouseRepository.getQty(productSerial.getId()));
                    }
                    return productSerialLookUpDTO;
                })
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        productSerialRepository.findById(id).map(productSerial -> {
            BigDecimal qty = productSerialWarehouseRepository.getQty(productSerial.getId());
            if (qty.compareTo(BigDecimal.ZERO) > 0) {
                throw validationService.badRequestExceptionThrow("Вы не можете удалить Серия, у которого есть в склады '" + productSerial.getName() + "', Количество " + qty).get();
            }
            productSerial.setDeleted(true);
            productSerialRepository.save(productSerial);
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(ProductSerial.class.getSimpleName(), "id", id));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, ProductSerialStatus status) {
        ProductSerial productSerial = productSerialRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(ProductSerial.class.getSimpleName(), "id", id));
        if (ProductSerialStatus.PRODUCED.equals(productSerial.getStatus())) {
            throw validationService.badRequestExceptionThrow("Серия уже завершён").get();
        } else {
            productSerial.setStatus(status);
            productSerialRepository.save(productSerial);
        }
    }

    public StockProductQtyStatisticsDTO getQtyStatistics(StockFilter filter) {
        return productSerialRepository.getQtyStatistics(filter);
    }

    @Transactional
    public void updateQty(Long productSerialId) {
        for (ProductSerialWarehouse warehouseProduct : productSerialWarehouseRepository.findAllByProductSerialId(productSerialId)) {
            StockFilter filter = new StockFilter();
            filter.setCompanyId(warehouseProduct.getProductSerial().getCompanyId());
            filter.setProductSerialId(productSerialId);
            filter.setWarehouseId(warehouseProduct.getWarehouseId());
            filter.setBranchId(warehouseProduct.getWarehouse().getBranchId());
            StockProductQtyStatisticsDTO statisticsDTO = this.getQtyStatistics(filter);
            BigDecimal qty = BigDecimal.ZERO;
            qty = qty.add(Optional.ofNullable(statisticsDTO.getStockInTotal()).orElse(BigDecimal.ZERO));
            qty = qty.subtract(Optional.ofNullable(statisticsDTO.getStockOutTotal()).orElse(BigDecimal.ZERO));
            qty = qty.add(Optional.ofNullable(statisticsDTO.getStockReturnTotal()).orElse(BigDecimal.ZERO));
            qty = qty.subtract(Optional.ofNullable(statisticsDTO.getStockAdjustmentTotal()).orElse(BigDecimal.ZERO));
            qty = qty.subtract(Optional.ofNullable(statisticsDTO.getStockTransferFromTotal()).orElse(BigDecimal.ZERO));
            qty = qty.add(Optional.ofNullable(statisticsDTO.getStockTransferToTotal()).orElse(BigDecimal.ZERO));
            warehouseProduct.setQty(qty);
            productSerialWarehouseRepository.save(warehouseProduct);
        }
    }

    public Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter) {
        return switch (filter.getTransferType()) {
            case ADJUSTMENT -> stockAdjustmentProductRepository.getQtyDetails(filter);
            case TRANSFER_FROM, TRANSFER_TO -> stockTransferProductRepository.getQtyDetails(filter);
            default -> productSerialRepository.getQtyDetails(filter);
        };
    }

    public Page<ProductSerialReportDTO> getReport(BalanceReportFilter filter) {
        ResultList<ProductSerialWarehouse> resultList = productSerialWarehouseRepository.getResultList(filter);
        List<ProductSerialReportDTO> result = resultList
                .getList()
                .stream()
                .map(productSerialWarehouse -> {
                    ProductSerialReportDTO productSerialReportDTO = new ProductSerialReportDTO();
                    productSerialReportDTO.setId(productSerialWarehouse.getId());
                    productSerialReportDTO.setProduct(productSerialWarehouse.getProductSerial().getProduct().toProductCommonDTO());
                    productSerialReportDTO.setQty(productSerialWarehouse.getQty());
                    productSerialReportDTO.setBranch(productSerialWarehouse.getProductSerial().getProduct().getBranch().toCommonDTO());
                    productSerialReportDTO.setWarehouse(productSerialWarehouse.getWarehouse().toCommonDTO());
                    productSerialReportDTO.setProductSerial(productSerialWarehouse.getProductSerial().toCommonDTO());
                    productSerialReportDTO.setPrice(productSerialWarehouse.getProductSerial().getProduct().getPrice());
                    productSerialReportDTO.setTotalPrice(Optional.ofNullable(productSerialReportDTO.getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(productSerialReportDTO.getQty()).orElse(BigDecimal.ZERO)));
                    productSerialReportDTO.setHasMark(productSerialWarehouse.getProductSerial().getProduct().isHasMark());
                    productSerialReportDTO.setCapacity(productSerialWarehouse.getProductSerial().getProduct().getCapacity());
                    productSerialReportDTO.setProductType(productSerialWarehouse.getProductSerial().getProduct().getProductType());
                    productSerialReportDTO.setBlockBarcode(productSerialWarehouse.getProductSerial().getProduct().getBlockBarcode());
                    productSerialReportDTO.setBoxBarcode(productSerialWarehouse.getProductSerial().getProduct().getBoxBarcode());
                    if (productSerialWarehouse.getProductSerial().getProduct().getParentId() != null) {
                        productSerialReportDTO.setParent(productSerialWarehouse.getProductSerial().getProduct().getParent().toProductCommonDTO());
                        if (StringUtils.isEmpty(productSerialReportDTO.getBlockBarcode())) {
                            productSerialReportDTO.setBlockBarcode(productSerialWarehouse.getProductSerial().getProduct().getBarcode());
                        }
                    }
                    return productSerialReportDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public BigDecimal getReportTotalQty(BalanceReportFilter filter) {
        return productSerialWarehouseRepository.getReportTotalQty(filter);
    }
}
