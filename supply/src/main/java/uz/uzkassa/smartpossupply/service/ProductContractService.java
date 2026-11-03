package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.product.ProductContract;
import uz.uzkassa.smartpossupply.domain.product.ProductContractWarehouse;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.productcontract.ProductContractDTO;
import uz.uzkassa.smartpossupply.dto.productcontract.ProductContractDetailDTO;
import uz.uzkassa.smartpossupply.dto.productcontract.ProductContractListDTO;
import uz.uzkassa.smartpossupply.dto.productcontract.ProductContractLookupDTO;
import uz.uzkassa.smartpossupply.dto.report.ProductContractReportDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.interfaces.Crud;
import uz.uzkassa.smartpossupply.interfaces.LookUp;
import uz.uzkassa.smartpossupply.repository.ProductContractRepository;
import uz.uzkassa.smartpossupply.repository.ProductContractWarehouseRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.07.2023
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductContractService extends BaseService implements Crud<Long, ProductContractDTO, ProductContractDetailDTO, ProductContractListDTO, BaseFilter>, LookUp<ProductContractLookupDTO, BaseFilter> {

    ProductContractRepository productContractRepository;
    ProductContractWarehouseRepository productContractWarehouseRepository;

    @Override
    public void validate(ProductContractDTO productContractDTO) {
        if (StringUtils.isEmpty(productContractDTO.getName())) {
            throw validationService.badRequestExceptionThrow("Name is required").get();
        }
        if (productContractDTO.getBasePrice() == null || productContractDTO.getBasePrice().compareTo(BigDecimal.ZERO) == 0) {
            throw validationService.badRequestExceptionThrow("Base price is required").get();
        }
        if (productContractDTO.getPrice() == null || productContractDTO.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            throw validationService.badRequestExceptionThrow("Price is required").get();
        }
        if (productContractDTO.getProfitRate() == null || productContractDTO.getProfitRate().compareTo(BigDecimal.ZERO) == 0) {
            throw validationService.badRequestExceptionThrow("Profit rate is required").get();
        }
        if (productContractDTO.getProductId() == null) {
            throw validationService.badRequestExceptionThrow("Product is required").get();
        }
        if (productContractDTO.getBranchId() == null) {
            throw validationService.badRequestExceptionThrow("Branch is required").get();
        }
        if (productContractDTO.getId() == null && productContractRepository.existsByNameAndBasePriceAndProfitRateAndBranchIdAndDeletedIsFalse(productContractDTO.getName(), productContractDTO.getBasePrice(), productContractDTO.getProfitRate(), productContractDTO.getBranchId())) {
            throw validationService.conflictExceptionThrow("Контракт на продукт с таким данными уже существует.").get();
        }
        if (productContractDTO.getId() != null && productContractRepository.existsByNameAndBasePriceAndProfitRateAndBranchIdAndDeletedIsFalseAndIdNot(productContractDTO.getName(), productContractDTO.getBasePrice(), productContractDTO.getProfitRate(), productContractDTO.getBranchId(), productContractDTO.getId())) {
            throw validationService.conflictExceptionThrow("Контракт на продукт с таким данными уже существует.").get();
        }
    }

    @Override
    @Transactional
    public Long create(ProductContractDTO productContractDTO) {
        this.validate(productContractDTO);
        return this.createEntity(productContractDTO).getId();
    }

    private ProductContract createEntity(ProductContractDTO productContractDTO) {
        ProductContract productContract = new ProductContract();
        productContract.setName(productContractDTO.getName());
        productContract.setBasePrice(productContractDTO.getBasePrice());
        productContract.setPrice(productContractDTO.getPrice());
        productContract.setProfitRate(productContractDTO.getProfitRate());
        productContract.setProductId(productContractDTO.getProductId());
        productContract.setBranchId(productContractDTO.getBranchId());
        productContractRepository.save(productContract);
        return productContract;
    }

    @Transactional
    public ProductContract createOrGet(ProductContractDTO productContractDTO) {
        //this.validate(productContractDTO);
        ProductContract productContract = productContractRepository.findFirstByNameAndBasePriceAndProfitRateAndBranchIdAndDeletedIsFalse(productContractDTO.getName(), productContractDTO.getBasePrice(), productContractDTO.getProfitRate(), productContractDTO.getBranchId()).orElse(null);
        if (productContract == null) {
            productContract = this.createEntity(productContractDTO);
        }
        return productContract;
    }

    @Override
    @Transactional
    public Long update(Long id, ProductContractDTO productContractDTO) {
        productContractDTO.setId(id);
        this.validate(productContractDTO);
        return productContractRepository.findById(id).map(productContract -> {
            productContract.setName(productContractDTO.getName());
            productContract.setBasePrice(productContractDTO.getBasePrice());
            productContract.setProfitRate(productContractDTO.getProfitRate());
            productContract.setPrice(productContractDTO.getPrice());
            productContract.setProductId(productContractDTO.getProductId());
            productContract.setBranchId(productContractDTO.getBranchId());
            productContractRepository.save(productContract);
            return productContract.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(ProductContract.class.getSimpleName(), "id", id));
    }

    @Override
    public ProductContractDetailDTO get(Long id) {
        return productContractRepository.findById(id).map(productContract -> {
                    ProductContractDetailDTO productContractDetailDTO = new ProductContractDetailDTO();
                    productContractDetailDTO.setId(productContract.getId());
                    productContractDetailDTO.setName(productContract.getName());
                    productContractDetailDTO.setBasePrice(productContract.getBasePrice());
                    productContractDetailDTO.setPrice(productContract.getPrice());
                    productContractDetailDTO.setProfitRate(productContract.getProfitRate());
                    productContractDetailDTO.setProduct(productContract.getProduct().toCommonDTO());
                    productContractDetailDTO.setBranch(productContract.getBranch().toCommonDTO());
                    return productContractDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(ProductContract.class.getSimpleName(), "id", id));
    }

    @Override
    public Page<ProductContractListDTO> getList(BaseFilter filter) {
        ResultList<ProductContract> resultList = productContractRepository.getResultList(filter);
        List<ProductContractListDTO> result = resultList
                .getList()
                .stream()
                .map(productContract -> {
                    ProductContractListDTO productContractListDTO = new ProductContractListDTO();
                    productContractListDTO.setId(productContract.getId());
                    productContractListDTO.setName(productContract.getName());
                    productContractListDTO.setBasePrice(productContract.getBasePrice());
                    productContractListDTO.setPrice(productContract.getPrice());
                    productContractListDTO.setProfitRate(productContract.getProfitRate());
                    productContractListDTO.setProduct(productContract.getProduct().toProductCommonDTO());
                    productContractListDTO.setBranch(productContract.getBranch().toCommonDTO());
                    if (filter.getWarehouseId() != null) {
                        productContractListDTO.setQty(productContractWarehouseRepository.getQty(filter.getWarehouseId(), productContract.getId()));
                    } else {
                        productContractListDTO.setQty(productContractWarehouseRepository.getQty(productContract.getId()));
                    }
                    return productContractListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    public List<ProductContractLookupDTO> lookUp(BaseFilter filter) {
        return productContractRepository.lookUp(filter).stream().map(productContract -> {
            ProductContractLookupDTO productContractLookupDTO = new ProductContractLookupDTO();
            productContractLookupDTO.setId(productContract.getId());
            productContractLookupDTO.setName(productContract.getName());
            productContractLookupDTO.setProfitRate(productContract.getProfitRate());
            productContractLookupDTO.setBasePrice(productContract.getBasePrice());
            productContractLookupDTO.setPrice(productContract.getPrice());
            if (filter.getWarehouseId() != null) {
                productContractLookupDTO.setQty(productContractWarehouseRepository.getQty(filter.getWarehouseId(), productContract.getId()));
            } else {
                productContractLookupDTO.setQty(productContractWarehouseRepository.getQty(productContract.getId()));
            }
            return productContractLookupDTO;
        }).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        productContractRepository.findById(id).map(productContract -> {
            BigDecimal qty = productContractWarehouseRepository.getQty(productContract.getId());
            if (qty.compareTo(BigDecimal.ZERO) > 0) {
                throw validationService.badRequestExceptionThrow("Вы не можете удалить контракт, у которого есть в склады '" + productContract.getName() + "', Количество " + qty).get();
            }
            productContract.setDeleted(true);
            productContractRepository.save(productContract);
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(ProductContract.class.getSimpleName(), "id", id));
    }

    public StockProductQtyStatisticsDTO getQtyStatistics(StockFilter filter) {
        return productContractRepository.getQtyStatistics(filter);
    }

    @Transactional
    public void updateQty(Long productContractId) {
        for (ProductContractWarehouse warehouseProduct : productContractWarehouseRepository.findAllByProductContractId(productContractId)) {
            StockFilter filter = new StockFilter();
            filter.setCompanyId(warehouseProduct.getProductContract().getProduct().getCompanyId());
            filter.setProductContractId(productContractId);
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
            productContractWarehouseRepository.save(warehouseProduct);
        }
    }

    public Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter) {
        return productContractRepository.getQtyDetails(filter);
    }


    public Page<ProductContractReportDTO> getReport(BalanceReportFilter filter) {
        ResultList<ProductContractWarehouse> resultList = productContractWarehouseRepository.getResultList(filter);
        List<ProductContractReportDTO> result = resultList
                .getList()
                .stream()
                .map(productContractReport -> {
                    ProductContractReportDTO productContractReportDTO = new ProductContractReportDTO();
                    productContractReportDTO.setId(productContractReport.getId());
                    productContractReportDTO.setProduct(productContractReport.getProductContract().getProduct().toProductCommonDTO());
                    productContractReportDTO.setQty(productContractReport.getQty());
                    productContractReportDTO.setBranch(productContractReport.getProductContract().getProduct().getBranch().toCommonDTO());
                    productContractReportDTO.setWarehouse(productContractReport.getWarehouse().toCommonDTO());
                    productContractReportDTO.setProductContract(productContractReport.getProductContract().toCommonDTO());
                    productContractReportDTO.setPrice(Optional.ofNullable(productContractReport.getProductContract().getPrice()).orElse(BigDecimal.ZERO));
                    productContractReportDTO.setTotalPrice(Optional.ofNullable(productContractReportDTO.getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(productContractReportDTO.getQty()).orElse(BigDecimal.ZERO)));
                    productContractReportDTO.setHasMark(productContractReport.getProductContract().getProduct().isHasMark());
                    productContractReportDTO.setCapacity(productContractReport.getProductContract().getProduct().getCapacity());
                    productContractReportDTO.setProductType(productContractReport.getProductContract().getProduct().getProductType());
                    productContractReportDTO.setBlockBarcode(productContractReport.getProductContract().getProduct().getBlockBarcode());
                    productContractReportDTO.setBoxBarcode(productContractReport.getProductContract().getProduct().getBoxBarcode());
                    if (productContractReport.getProductContract().getProduct().getParentId() != null) {
                        productContractReportDTO.setParent(productContractReport.getProductContract().getProduct().getParent().toProductCommonDTO());
                        if (StringUtils.isEmpty(productContractReportDTO.getBlockBarcode())) {
                            productContractReportDTO.setBlockBarcode(productContractReport.getProductContract().getProduct().getBarcode());
                        }
                    }
                    return productContractReportDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public BigDecimal getReportTotalQty(BalanceReportFilter filter) {
        return productContractWarehouseRepository.getReportTotalQty(filter);
    }
}
