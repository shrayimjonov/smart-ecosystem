package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.CompanyCommittentWarehouse;
import uz.uzkassa.smartpossupply.domain.order.OrderProduct;
import uz.uzkassa.smartpossupply.domain.product.ProductContractWarehouse;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerialWarehouse;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregation;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregationBlock;
import uz.uzkassa.smartpossupply.domain.stock.StockProductMark;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustment;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustmentProduct;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransfer;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransferProduct;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseProduct;
import uz.uzkassa.smartpossupply.dto.aggregationcodes.AggregationCodeDTO;
import uz.uzkassa.smartpossupply.dto.aggregationcodes.AggregationCodeItemDTO;
import uz.uzkassa.smartpossupply.dto.aggregationcodes.AggregationCodeStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponseResult;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.payload.AggregationCodesPayload;
import uz.uzkassa.smartpossupply.dto.product.ProductQtyDTO;
import uz.uzkassa.smartpossupply.dto.report.WarehouseProductReportDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.warehouseproduct.WarehouseProductDTO;
import uz.uzkassa.smartpossupply.dto.warehouseproduct.WarehouseProductListOfflineDTO;
import uz.uzkassa.smartpossupply.dto.warehouseproduct.WarehouseProductOfflineDTO;
import uz.uzkassa.smartpossupply.dto.warehouseproduct.WarehouseProductQtyDTO;
import uz.uzkassa.smartpossupply.dto.xtrace.XtraceTokenDetailDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.filters.AggregationCodesReportFilter;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.executor.AfterCommitExecutor;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqAggregationCodesProducer;
import uz.uzkassa.smartpossupply.repository.*;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class WarehouseProductService extends BaseService {

    WarehouseProductRepository warehouseProductRepository;
    ProductContractWarehouseRepository productContractWarehouseRepository;
    ProductSerialWarehouseRepository productSerialWarehouseRepository;
    CompanyCommittentWarehouseRepository companyCommittentWarehouseRepository;
    StockProductRepository stockProductRepository;
    StockAdjustmentProductRepository stockAdjustmentProductRepository;
    StockTransferProductRepository stockTransferProductRepository;
    StockInRepository stockInRepository;
    StockProductAggregationRepository stockProductAggregationRepository;
    StockProductAggregationBlockRepository stockProductAggregationBlockRepository;
    StockProductMarkRepository stockProductMarkRepository;
    RabbitMqAggregationCodesProducer rabbitMqAggregationCodesProducer;
    AslBelgiClient aslBelgiClient;
    WarehouseRepository warehouseRepository;
    AfterCommitExecutor afterCommitExecutor;

    private static final boolean VALIDATE_USED_CODES = false;

    public Page<WarehouseProductReportDTO> getReport(BalanceReportFilter filter) {
        ResultList<WarehouseProduct> resultList = warehouseProductRepository.getResultList(filter);
        List<WarehouseProductReportDTO> result = resultList
                .getList()
                .stream()
                .map(warehouseProduct -> {
                    WarehouseProductReportDTO warehouseProductReportDTO = new WarehouseProductReportDTO();
                    warehouseProductReportDTO.setId(warehouseProduct.getId());
                    warehouseProductReportDTO.setProduct(warehouseProduct.getProduct().toProductCommonDTO());
                    warehouseProductReportDTO.setQty(warehouseProduct.getQty());
                    warehouseProductReportDTO.setReservationQty(warehouseProduct.getReservationQty());
                    warehouseProductReportDTO.setBranch(warehouseProduct.getProduct().getBranch().toCommonDTO());
                    warehouseProductReportDTO.setWarehouse(warehouseProduct.getWarehouse().toCommonDTO());
                    warehouseProductReportDTO.setHasMark(warehouseProduct.getProduct().isHasMark());
                    warehouseProductReportDTO.setPrice(warehouseProduct.getProduct().getPrice());
                    warehouseProductReportDTO.setTotalPrice(Optional.ofNullable(warehouseProduct.getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(warehouseProduct.getQty()).orElse(BigDecimal.ZERO)));
                    warehouseProductReportDTO.setCapacity(warehouseProduct.getProduct().getCapacity());
                    warehouseProductReportDTO.setProductType(warehouseProduct.getProduct().getProductType());
                    warehouseProductReportDTO.setBoxBarcode(warehouseProduct.getProduct().getBoxBarcode());
                    warehouseProductReportDTO.setBlockBarcode(warehouseProduct.getProduct().getBlockBarcode());
                    if (warehouseProduct.getProduct().getParent() != null) {
                        warehouseProductReportDTO.setParent(warehouseProduct.getProduct().getParent().toProductCommonDTO());
                        if (org.apache.commons.lang3.StringUtils.isEmpty(warehouseProductReportDTO.getBlockBarcode())) {
                            warehouseProductReportDTO.setBlockBarcode(warehouseProduct.getProduct().getParent().getBarcode());
                        }
                    }
                    return warehouseProductReportDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<WarehouseProductReportDTO> getReportList(BalanceReportFilter filter) {
        return warehouseProductRepository.getResultList(filter)
                .getList()
                .stream()
                .map(warehouseProduct -> {
                    WarehouseProductReportDTO warehouseProductReportDTO = new WarehouseProductReportDTO();
                    warehouseProductReportDTO.setId(warehouseProduct.getId());
                    warehouseProductReportDTO.setProduct(warehouseProduct.getProduct().toProductCommonDTO());
                    warehouseProductReportDTO.setQty(warehouseProduct.getQty());
                    warehouseProductReportDTO.setBranch(warehouseProduct.getProduct().getBranch().toCommonDTO());
                    warehouseProductReportDTO.setWarehouse(warehouseProduct.getWarehouse().toCommonDTO());
                    warehouseProductReportDTO.setHasMark(warehouseProduct.getProduct().isHasMark());
                    warehouseProductReportDTO.setPrice(warehouseProduct.getProduct().getPrice());
                    warehouseProductReportDTO.setTotalPrice(Optional.ofNullable(warehouseProduct.getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(warehouseProduct.getQty()).orElse(BigDecimal.ZERO)));
                    warehouseProductReportDTO.setCapacity(warehouseProduct.getProduct().getCapacity());
                    warehouseProductReportDTO.setProductType(warehouseProduct.getProduct().getProductType());
                    warehouseProductReportDTO.setBoxBarcode(warehouseProduct.getProduct().getBoxBarcode());
                    warehouseProductReportDTO.setBlockBarcode(warehouseProduct.getProduct().getBlockBarcode());
                    if (warehouseProduct.getProduct().getParent() != null) {
                        warehouseProductReportDTO.setParent(warehouseProduct.getProduct().getParent().toProductCommonDTO());
                        if (org.apache.commons.lang3.StringUtils.isEmpty(warehouseProductReportDTO.getBlockBarcode())) {
                            warehouseProductReportDTO.setBlockBarcode(warehouseProduct.getProduct().getParent().getBarcode());
                        }
                    }
                    return warehouseProductReportDTO;
                }).toList();
    }

    public BigDecimal getReportTotalQty(BalanceReportFilter filter) {
        return warehouseProductRepository.getReportTotalQty(filter);
    }

    @Transactional
    public void out(Stock stock) {
        for (StockProduct stockProduct : stock.getProducts()) {
            WarehouseProduct warehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductIdAndUnitId(stock.getWarehouseId(), stockProduct.getProductId(), stockProduct.getUnitId())
                    .orElse(null);
            if (warehouseProduct != null) {
                warehouseProduct.subtractQty(stockProduct.getQty());
                if (warehouseProduct.getQty().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestException(localizationService.localize(MessageConstants.VALIDATE_STOCK_OUT, new Object[]{stockProduct.getProduct().getName()}));
                }
                warehouseProductRepository.save(warehouseProduct);
            }

            if (stockProduct.getProductContractId() != null) {
                productContractWarehouseRepository.findFirstByWarehouseIdAndProductContractId(stock.getWarehouseId(), stockProduct.getProductContractId()).ifPresent(productContractWarehouse -> {
                    productContractWarehouse.subtractQty(stockProduct.getQty());
                    /*if (productContractWarehouse.getQty().compareTo(BigDecimal.ZERO) < 0) {
                        throw validationService.badRequestExceptionThrow("Количество в контракте " + productContractWarehouse.getProductContract().getName() + " недостаточно").get();
                    }*/
                    productContractWarehouseRepository.save(productContractWarehouse);
                });
            }

            if (stockProduct.getProductSerialId() != null) {
                productSerialWarehouseRepository.findFirstByWarehouseIdAndProductSerialId(stock.getWarehouseId(), stockProduct.getProductSerialId()).ifPresent(productSerialWarehouse -> {
                    productSerialWarehouse.subtractQty(stockProduct.getQty());
                    if (productSerialWarehouse.getQty().compareTo(BigDecimal.ZERO) < 0) {
                        throw new BadRequestException("Количество в серии " + productSerialWarehouse.getProductSerial().getName() + " недостаточно");
                    }
                    productSerialWarehouseRepository.save(productSerialWarehouse);
                });
            }

            if (stockProduct.getCompanyCommittentId() != null) {
                companyCommittentWarehouseRepository.findFirstByProductIdAndWarehouseIdAndCompanyCommittentId(stockProduct.getProductId(), stock.getWarehouseId(), stockProduct.getCompanyCommittentId()).ifPresent(companyCommittentWarehouse -> {
                    companyCommittentWarehouse.subtractQty(stockProduct.getQty());
                    /*if (companyCommittentWarehouse.getQty().compareTo(BigDecimal.ZERO) < 0) {
                        throw validationService.badRequestExceptionThrow("Количество в комитент " + companyCommittentWarehouse.getCompanyCommittent().getCommittent().getName() + " недостаточно").get();
                    }*/
                    companyCommittentWarehouseRepository.save(companyCommittentWarehouse);
                });
            }
        }
        if (stock.getOrder() != null) {
            boolean isCalculateReservation = false;
            if (OrderSource.MANUAL.equals(stock.getOrder().getOrderSource()) || OrderSource.PLAN_ROUTE.equals(stock.getOrder().getOrderSource())) {
                Optional<CompanySettings> optionalCompanySettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(stock.getOrder().getCompanyId(), GenericSettingsType.WORK_WITH_RESERVATION);
                if (optionalCompanySettings.isPresent() && optionalCompanySettings.get().isEnabled()) {
                    isCalculateReservation = true;
                }
            }
            if (isCalculateReservation) {
                for (OrderProduct product : stock.getOrder().getProducts()) {
                    WarehouseProduct warehouseProduct = warehouseProductRepository
                            .findByWarehouseIdAndProductIdAndUnitId(stock.getOrder().getWarehouseId(), product.getProductId(), product.getUnitId())
                            .orElse(null);
                    if (warehouseProduct != null) {
                        warehouseProduct.subtractReservationQty(Optional.ofNullable(product.getQty()).orElse(BigDecimal.ZERO));
                        warehouseProductRepository.saveAndFlush(warehouseProduct);
                    }
                }
                stock.getOrder().setReservationReleased(true);
            }
        }
    }

    @Transactional
    public void outFromMultiWarehouse(Stock stock) {
        for (StockProduct stockProduct : stock.getProducts()) {
            WarehouseProduct warehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductIdAndUnitId(stockProduct.getWarehouseId(), stockProduct.getProductId(), stockProduct.getUnitId())
                    .orElse(null);
            if (warehouseProduct != null) {
                warehouseProduct.subtractQty(stockProduct.getQty());
                if (warehouseProduct.getQty().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestException(localizationService.localize(MessageConstants.VALIDATE_STOCK_OUT, new Object[]{stockProduct.getProduct().getName()}));
                }
                warehouseProductRepository.save(warehouseProduct);
            }

            if (stockProduct.getProductContractId() != null) {
                productContractWarehouseRepository.findFirstByWarehouseIdAndProductContractId(stockProduct.getWarehouseId(), stockProduct.getProductContractId()).ifPresent(productContractWarehouse -> {
                    productContractWarehouse.subtractQty(stockProduct.getQty());
                    /*if (productContractWarehouse.getQty().compareTo(BigDecimal.ZERO) < 0) {
                        throw validationService.badRequestExceptionThrow("Количество в контракте " + productContractWarehouse.getProductContract().getName() + " недостаточно").get();
                    }*/
                    productContractWarehouseRepository.save(productContractWarehouse);
                });
            }

            if (stockProduct.getProductSerialId() != null) {
                productSerialWarehouseRepository.findFirstByWarehouseIdAndProductSerialId(stockProduct.getWarehouseId(), stockProduct.getProductSerialId()).ifPresent(productSerialWarehouse -> {
                    productSerialWarehouse.subtractQty(stockProduct.getQty());
                    if (productSerialWarehouse.getQty().compareTo(BigDecimal.ZERO) < 0) {
                        throw validationService.badRequestExceptionThrow("Количество в серии " + productSerialWarehouse.getProductSerial().getName() + " недостаточно").get();
                    }
                    productSerialWarehouseRepository.save(productSerialWarehouse);
                });
            }

            if (stockProduct.getCompanyCommittentId() != null) {
                companyCommittentWarehouseRepository.findFirstByProductIdAndWarehouseIdAndCompanyCommittentId(stockProduct.getProductId(), stockProduct.getWarehouseId(), stockProduct.getCompanyCommittentId()).ifPresent(companyCommittentWarehouse -> {
                    companyCommittentWarehouse.subtractQty(stockProduct.getQty());
                    /*if (companyCommittentWarehouse.getQty().compareTo(BigDecimal.ZERO) < 0) {
                        throw validationService.badRequestExceptionThrow("Количество в комитент " + companyCommittentWarehouse.getCompanyCommittent().getCommittent().getName() + " недостаточно").get();
                    }*/
                    companyCommittentWarehouseRepository.save(companyCommittentWarehouse);
                });
            }
        }
        if (stock.getOrder() != null) {
            boolean isCalculateReservation = false;
            if (OrderSource.MANUAL.equals(stock.getOrder().getOrderSource()) || OrderSource.PLAN_ROUTE.equals(stock.getOrder().getOrderSource())) {
                Optional<CompanySettings> optionalCompanySettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(stock.getOrder().getCompanyId(), GenericSettingsType.WORK_WITH_RESERVATION);
                if (optionalCompanySettings.isPresent()) {
                    if (optionalCompanySettings.get().isEnabled()) {
                        isCalculateReservation = true;
                    }
                }
            }
            if (isCalculateReservation && stock.getOrder().getStockTransfer() != null) {
                for (OrderProduct orderProduct : stock.getOrder().getProducts()) {
                    WarehouseProduct warehouseProduct = warehouseProductRepository
                            .findByWarehouseIdAndProductIdAndUnitId(orderProduct.getWarehouseId(), orderProduct.getProductId(), orderProduct.getUnitId())
                            .orElse(null);
                    if (warehouseProduct != null) {
                        warehouseProduct.subtractReservationQty(Optional.ofNullable(orderProduct.getQty()).orElse(BigDecimal.ZERO));
                        warehouseProductRepository.saveAndFlush(warehouseProduct);
                    }
                }
              stock.getOrder().setReservationReleased(true);
            }
        }
    }

    @Transactional
    public void in(Stock stock, boolean updateCodes) {
        for (StockProduct stockProduct : stock.getProducts()) {
            WarehouseProduct warehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductIdAndUnitId(stock.getWarehouseId(), stockProduct.getProductId(), stockProduct.getUnitId())
                    .orElseGet(WarehouseProduct::new);
            if (warehouseProduct.getId() == null) {
                warehouseProduct.setWarehouseId(stock.getWarehouseId());
                warehouseProduct.setProductId(stockProduct.getProductId());
                warehouseProduct.setUnitId(stockProduct.getUnitId());
            }
            warehouseProduct.addQty(stockProduct.getQty());
            warehouseProductRepository.save(warehouseProduct);

            if (stockProduct.getProductContractId() != null) {
                ProductContractWarehouse productContractWarehouse = productContractWarehouseRepository.findFirstByWarehouseIdAndProductContractId(stock.getWarehouseId(), stockProduct.getProductContractId()).orElse(new ProductContractWarehouse());
                if (productContractWarehouse.getId() == null) {
                    productContractWarehouse.setWarehouseId(stock.getWarehouseId());
                    productContractWarehouse.setProductContractId(stockProduct.getProductContractId());
                }
                productContractWarehouse.addQty(stockProduct.getQty());
                productContractWarehouseRepository.save(productContractWarehouse);
            }

            if (stockProduct.getProductSerialId() != null) {
                ProductSerialWarehouse productSerialWarehouse = productSerialWarehouseRepository.findFirstByWarehouseIdAndProductSerialId(stock.getWarehouseId(), stockProduct.getProductSerialId()).orElse(new ProductSerialWarehouse());
                if (productSerialWarehouse.getId() == null) {
                    productSerialWarehouse.setWarehouseId(stock.getWarehouseId());
                    productSerialWarehouse.setProductSerialId(stockProduct.getProductSerialId());
                }
                productSerialWarehouse.addQty(stockProduct.getQty());
                productSerialWarehouseRepository.save(productSerialWarehouse);
            }
            if (stockProduct.getCompanyCommittentId() != null) {
                CompanyCommittentWarehouse companyCommittentWarehouse = companyCommittentWarehouseRepository.findFirstByProductIdAndWarehouseIdAndCompanyCommittentId(stockProduct.getProductId(), stock.getWarehouseId(), stockProduct.getCompanyCommittentId()).orElse(new CompanyCommittentWarehouse());
                if (companyCommittentWarehouse.getId() == null) {
                    companyCommittentWarehouse.setWarehouseId(stock.getWarehouseId());
                    companyCommittentWarehouse.setProductId(stockProduct.getProductId());
                    companyCommittentWarehouse.setCompanyCommittentId(stockProduct.getCompanyCommittentId());
                }
                companyCommittentWarehouse.addQty(stockProduct.getQty());
                companyCommittentWarehouseRepository.save(companyCommittentWarehouse);
            }
        }
        if (stock.getOrder() != null) {
            boolean isCalculateReservation = false;
            if (OrderSource.MANUAL.equals(stock.getOrder().getOrderSource()) || OrderSource.PLAN_ROUTE.equals(stock.getOrder().getOrderSource())) {
                Optional<CompanySettings> optionalCompanySettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(stock.getOrder().getCompanyId(), GenericSettingsType.WORK_WITH_RESERVATION);
                if (optionalCompanySettings.isPresent() && optionalCompanySettings.get().isEnabled()) {
                    isCalculateReservation = true;
                }
            }
            if (isCalculateReservation) {
                for (OrderProduct orderProduct : stock.getOrder().getProducts()) {
                    WarehouseProduct warehouseProduct = warehouseProductRepository
                            .findByWarehouseIdAndProductIdAndUnitId(stock.getOrder().getWarehouseId(), orderProduct.getProductId(), orderProduct.getUnitId())
                            .orElse(null);
                    if (warehouseProduct != null) {
                        warehouseProduct.addReservationQty(orderProduct.getQty());
                        if (Optional.ofNullable(warehouseProduct.getQty()).orElse(BigDecimal.ZERO).subtract(warehouseProduct.getReservationQty()).compareTo(BigDecimal.ZERO) < 0) {
                            throw new BadRequestException(localizationService.localize(MessageConstants.RESERVATION_QTY_MORE_THAN_QTY_IN_WAREHOUSE, new Object[]{warehouseProduct.getProduct().getName(), Optional.ofNullable(warehouseProduct.getProduct().getBarcode()).orElse("")}));
                        }
                        warehouseProductRepository.saveAndFlush(warehouseProduct);
                    }
                }
                stock.getOrder().setReservationReleased(false);
            }
        }
    }

    @Transactional
    public void inToMultiWarehouse(Stock stock, boolean updateCodes) {
        for (StockProduct stockProduct : stock.getProducts()) {
            WarehouseProduct warehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductIdAndUnitId(stockProduct.getWarehouseId(), stockProduct.getProductId(), stockProduct.getUnitId())
                    .orElseGet(WarehouseProduct::new);
            if (warehouseProduct.getId() == null) {
                warehouseProduct.setWarehouseId(stockProduct.getWarehouseId());
                warehouseProduct.setProductId(stockProduct.getProductId());
                warehouseProduct.setUnitId(stockProduct.getUnitId());
            }
            warehouseProduct.addQty(stockProduct.getQty());
            warehouseProductRepository.save(warehouseProduct);

            if (stockProduct.getProductContractId() != null) {
                ProductContractWarehouse productContractWarehouse = productContractWarehouseRepository.findFirstByWarehouseIdAndProductContractId(stockProduct.getWarehouseId(), stockProduct.getProductContractId()).orElse(new ProductContractWarehouse());
                if (productContractWarehouse.getId() == null) {
                    productContractWarehouse.setWarehouseId(stockProduct.getWarehouseId());
                    productContractWarehouse.setProductContractId(stockProduct.getProductContractId());
                }
                productContractWarehouse.addQty(stockProduct.getQty());
                productContractWarehouseRepository.save(productContractWarehouse);
            }

            if (stockProduct.getProductSerialId() != null) {
                ProductSerialWarehouse productSerialWarehouse = productSerialWarehouseRepository.findFirstByWarehouseIdAndProductSerialId(stockProduct.getWarehouseId(), stockProduct.getProductSerialId()).orElse(new ProductSerialWarehouse());
                if (productSerialWarehouse.getId() == null) {
                    productSerialWarehouse.setWarehouseId(stockProduct.getWarehouseId());
                    productSerialWarehouse.setProductSerialId(stockProduct.getProductSerialId());
                }
                productSerialWarehouse.addQty(stockProduct.getQty());
                productSerialWarehouseRepository.save(productSerialWarehouse);
            }
            if (stockProduct.getCompanyCommittentId() != null) {
                CompanyCommittentWarehouse companyCommittentWarehouse = companyCommittentWarehouseRepository.findFirstByProductIdAndWarehouseIdAndCompanyCommittentId(stockProduct.getProductId(), stockProduct.getWarehouseId(), stockProduct.getCompanyCommittentId()).orElse(new CompanyCommittentWarehouse());
                if (companyCommittentWarehouse.getId() == null) {
                    companyCommittentWarehouse.setWarehouseId(stockProduct.getWarehouseId());
                    companyCommittentWarehouse.setProductId(stockProduct.getProductId());
                    companyCommittentWarehouse.setCompanyCommittentId(stockProduct.getCompanyCommittentId());
                }
                companyCommittentWarehouse.addQty(stockProduct.getQty());
                companyCommittentWarehouseRepository.save(companyCommittentWarehouse);
            }
        }
        if (stock.getOrder() != null) {
            boolean isCalculateReservation = false;
            if (OrderSource.MANUAL.equals(stock.getOrder().getOrderSource()) || OrderSource.PLAN_ROUTE.equals(stock.getOrder().getOrderSource())) {
                Optional<CompanySettings> optionalCompanySettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(stock.getOrder().getCompanyId(), GenericSettingsType.WORK_WITH_RESERVATION);
                if (optionalCompanySettings.isPresent()) {
                    if (optionalCompanySettings.get().isEnabled()) {
                        isCalculateReservation = true;
                    }
                }
            }
            if (isCalculateReservation) {
                for (OrderProduct orderProduct : stock.getOrder().getProducts()) {
                    WarehouseProduct warehouseProduct = warehouseProductRepository
                            .findByWarehouseIdAndProductIdAndUnitId(orderProduct.getWarehouseId(), orderProduct.getProductId(), orderProduct.getUnitId())
                            .orElse(null);
                    if (warehouseProduct != null) {
                        warehouseProduct.addReservationQty(Optional.ofNullable(orderProduct.getQty()).orElse(BigDecimal.ZERO));
                        if (Optional.ofNullable(warehouseProduct.getQty()).orElse(BigDecimal.ZERO).subtract(warehouseProduct.getReservationQty()).compareTo(BigDecimal.ZERO) < 0) {
                            throw new BadRequestException(localizationService.localize(MessageConstants.RESERVATION_QTY_MORE_THAN_QTY_IN_WAREHOUSE, new Object[]{warehouseProduct.getProduct().getName(), Optional.ofNullable(warehouseProduct.getProduct().getBarcode()).orElse("")}));
                        }
                        warehouseProductRepository.saveAndFlush(warehouseProduct);
                    }
                }
                stock.getOrder().setReservationReleased(false);
            }
        }
    }

    @Transactional
    public void out(StockAdjustment stockAdjustment) {
        for (StockAdjustmentProduct stockAdjustmentProduct : stockAdjustment.getProducts()) {
            WarehouseProduct warehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductIdAndUnitId(
                            stockAdjustment.getWarehouseId(),
                            stockAdjustmentProduct.getProductId(),
                            stockAdjustmentProduct.getUnitId()
                    )
                    .orElseThrow(
                            validationService.entityNotFoundExceptionThrow(
                                    WarehouseProduct.class.getSimpleName(),
                                    "Warehouse product",
                                    stockAdjustment.getWarehouseId()
                            )
                    );
            warehouseProduct.subtractQty(stockAdjustmentProduct.getQty());
            if (warehouseProduct.getQty().compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException(localizationService.localize(MessageConstants.VALIDATE_STOCK_OUT, new Object[]{stockAdjustmentProduct.getProduct().getName()}));
            }
            warehouseProductRepository.save(warehouseProduct);


            if (stockAdjustmentProduct.getProductSerialId() != null) {
                ProductSerialWarehouse productSerialWarehouse = productSerialWarehouseRepository.findFirstByWarehouseIdAndProductSerialId(stockAdjustment.getWarehouseId(), stockAdjustmentProduct.getProductSerialId()).orElseGet(ProductSerialWarehouse::new);
                if (productSerialWarehouse.getId() == null) {
                    productSerialWarehouse.setWarehouseId(stockAdjustment.getWarehouseId());
                    productSerialWarehouse.setProductSerialId(stockAdjustmentProduct.getProductSerialId());
                }
                productSerialWarehouse.subtractQty(stockAdjustmentProduct.getQty());
                if (productSerialWarehouse.getQty().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestException("Количество в серии " + productSerialWarehouse.getProductSerial().getName() + " недостаточно");
                }
                productSerialWarehouseRepository.save(productSerialWarehouse);
            }
        }
    }

    @Transactional
    public void in(StockAdjustment stockAdjustment) {
        for (StockAdjustmentProduct stockAdjustmentProduct : stockAdjustment.getProducts()) {
            WarehouseProduct warehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductIdAndUnitId(
                            stockAdjustment.getWarehouseId(),
                            stockAdjustmentProduct.getProductId(),
                            stockAdjustmentProduct.getUnitId()
                    )
                    .orElseThrow(
                            validationService.entityNotFoundExceptionThrow(
                                    WarehouseProduct.class.getSimpleName(),
                                    "Warehouse product",
                                    stockAdjustment.getWarehouseId()
                            )
                    );

            warehouseProduct.addQty(stockAdjustmentProduct.getQty());
            warehouseProductRepository.save(warehouseProduct);

            if (stockAdjustmentProduct.getProductSerialId() != null) {
                ProductSerialWarehouse productSerialWarehouse = productSerialWarehouseRepository.findFirstByWarehouseIdAndProductSerialId(stockAdjustment.getWarehouseId(), stockAdjustmentProduct.getProductSerialId()).orElse(new ProductSerialWarehouse());
                if (productSerialWarehouse.getId() == null) {
                    productSerialWarehouse.setWarehouseId(stockAdjustment.getWarehouseId());
                    productSerialWarehouse.setProductSerialId(stockAdjustmentProduct.getProductSerialId());
                }
                productSerialWarehouse.addQty(stockAdjustmentProduct.getQty());
                productSerialWarehouseRepository.save(productSerialWarehouse);
            }
        }
    }

    @Transactional
    public void out(StockTransfer stockTransfer) {
        for (StockTransferProduct stockTransferProduct : stockTransfer.getProducts()) {
            WarehouseProduct fromWarehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductIdAndUnitId(
                            stockTransfer.getFromWarehouseId(),
                            stockTransferProduct.getFromProductId(),
                            stockTransferProduct.getUnitId()
                    )
                    .orElse(null);

            if (fromWarehouseProduct != null) {
                fromWarehouseProduct.subtractQty(stockTransferProduct.getQty());
                if (fromWarehouseProduct.getQty().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestException(localizationService.localize(MessageConstants.VALIDATE_STOCK_OUT, new Object[]{stockTransferProduct.getFromProduct().getName()}));
                }
                warehouseProductRepository.save(fromWarehouseProduct);

                if (stockTransferProduct.getProductSerialId() != null) {
                    ProductSerialWarehouse productSerialWarehouse = productSerialWarehouseRepository.findFirstByWarehouseIdAndProductSerialId(stockTransfer.getFromWarehouseId(), stockTransferProduct.getProductSerialId()).orElse(new ProductSerialWarehouse());
                    if (productSerialWarehouse.getId() == null) {
                        productSerialWarehouse.setWarehouseId(stockTransfer.getFromWarehouseId());
                        productSerialWarehouse.setProductSerialId(stockTransferProduct.getProductSerialId());
                    }
                    productSerialWarehouse.subtractQty(stockTransferProduct.getQty());
                    if (productSerialWarehouse.getQty().compareTo(BigDecimal.ZERO) < 0) {
                        throw new BadRequestException(localizationService.localize(MessageConstants.VALIDATE_STOCK_OUT, new Object[]{stockTransferProduct.getFromProduct().getName()}));
                    }
                    productSerialWarehouseRepository.save(productSerialWarehouse);
                }
            }

            WarehouseProduct toWarehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductIdAndUnitId(
                            stockTransfer.getToWarehouseId(),
                            stockTransferProduct.getToProductId(),
                            stockTransferProduct.getUnitId()
                    )
                    .orElseGet(WarehouseProduct::new);
            if (toWarehouseProduct.getId() == null) {
                toWarehouseProduct.setWarehouseId(stockTransfer.getToWarehouseId());
                toWarehouseProduct.setProductId(stockTransferProduct.getToProductId());
                toWarehouseProduct.setUnitId(stockTransferProduct.getUnitId());
            }
            toWarehouseProduct.addQty(stockTransferProduct.getQty());
            warehouseProductRepository.save(toWarehouseProduct);

            if (stockTransferProduct.getProductSerialId() != null) {
                ProductSerialWarehouse productSerialWarehouse = productSerialWarehouseRepository.findFirstByWarehouseIdAndProductSerialId(stockTransfer.getToWarehouseId(), stockTransferProduct.getProductSerialId()).orElse(new ProductSerialWarehouse());
                if (productSerialWarehouse.getId() == null) {
                    productSerialWarehouse.setWarehouseId(stockTransfer.getToWarehouseId());
                    productSerialWarehouse.setProductSerialId(stockTransferProduct.getProductSerialId());
                }
                productSerialWarehouse.addQty(stockTransferProduct.getQty());
                productSerialWarehouseRepository.save(productSerialWarehouse);
            }
        }
    }

    @Transactional
    public void in(StockTransfer stockTransfer) {
        for (StockTransferProduct stockTransferProduct : stockTransfer.getProducts()) {
            WarehouseProduct fromWarehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductIdAndUnitId(
                            stockTransfer.getFromWarehouseId(),
                            stockTransferProduct.getFromProductId(),
                            stockTransferProduct.getUnitId()
                    )
                    .orElseGet(WarehouseProduct::new);

            if (fromWarehouseProduct.getId() == null) {
                fromWarehouseProduct.setProductId(stockTransferProduct.getFromProductId());
                fromWarehouseProduct.setWarehouseId(stockTransfer.getFromWarehouseId());
                fromWarehouseProduct.setUnitId(stockTransferProduct.getUnitId());
            }
            fromWarehouseProduct.addQty(stockTransferProduct.getQty());
            warehouseProductRepository.save(fromWarehouseProduct);

            if (stockTransferProduct.getProductSerialId() != null) {
                ProductSerialWarehouse fromProductSerialWarehouse = productSerialWarehouseRepository.findFirstByWarehouseIdAndProductSerialId(stockTransfer.getFromWarehouseId(), stockTransferProduct.getProductSerialId()).orElse(new ProductSerialWarehouse());
                if (fromProductSerialWarehouse.getId() == null) {
                    fromProductSerialWarehouse.setWarehouseId(stockTransfer.getToWarehouseId());
                    fromProductSerialWarehouse.setProductSerialId(stockTransferProduct.getProductSerialId());
                }
                fromProductSerialWarehouse.addQty(stockTransferProduct.getQty());
                productSerialWarehouseRepository.save(fromProductSerialWarehouse);
            }


            WarehouseProduct toWarehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductIdAndUnitId(
                            stockTransfer.getToWarehouseId(),
                            stockTransferProduct.getToProductId(),
                            stockTransferProduct.getUnitId()
                    )
                    .orElseThrow(
                            validationService.entityNotFoundExceptionThrow(
                                    WarehouseProduct.class.getSimpleName(),
                                    "ProductId",
                                    stockTransferProduct.getFromProductId()
                            )
                    );
            toWarehouseProduct.subtractQty(stockTransferProduct.getQty());
            if (toWarehouseProduct.getQty().compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException(localizationService.localize(MessageConstants.VALIDATE_STOCK_OUT, new Object[]{stockTransferProduct.getToProduct().getName()}));
            }
            warehouseProductRepository.save(toWarehouseProduct);

            if (stockTransferProduct.getProductSerialId() != null) {
                ProductSerialWarehouse productSerialWarehouse = productSerialWarehouseRepository.findFirstByWarehouseIdAndProductSerialId(stockTransfer.getToWarehouseId(), stockTransferProduct.getProductSerialId()).orElse(new ProductSerialWarehouse());
                if (productSerialWarehouse.getId() == null) {
                    productSerialWarehouse.setWarehouseId(stockTransfer.getToWarehouseId());
                    productSerialWarehouse.setProductSerialId(stockTransferProduct.getProductSerialId());
                }
                productSerialWarehouse.subtractQty(stockTransferProduct.getQty());
                if (productSerialWarehouse.getQty().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestException(localizationService.localize(MessageConstants.VALIDATE_STOCK_OUT, new Object[]{stockTransferProduct.getToProduct().getName()}));
                }
                productSerialWarehouseRepository.save(productSerialWarehouse);
            }
        }
    }

    @Transactional
    public void create(WarehouseProductDTO warehouseProductDTO) {
        WarehouseProduct warehouseProduct = warehouseProductRepository.findByWarehouseIdAndProductIdAndUnitId(warehouseProductDTO.getWarehouseId(), warehouseProductDTO.getProductId(), getUnit().getId()).orElse(null);
        if (warehouseProduct == null) {
            warehouseProduct = new WarehouseProduct();
            warehouseProduct.setWarehouseId(warehouseProductDTO.getWarehouseId());
            warehouseProduct.setProductId(warehouseProductDTO.getProductId());
            warehouseProduct.setUnitId(getUnit().getId());
        }
        warehouseProduct.setQty(warehouseProductDTO.getQty());
        warehouseProductRepository.save(warehouseProduct);
    }

    public Page<StockProductQtyDetailListDTO> getProductQtyDetails(StockFilter filter) {
        return switch (filter.getTransferType()) {
            case ADJUSTMENT -> stockAdjustmentProductRepository.getQtyDetails(filter);
            case TRANSFER_FROM, TRANSFER_TO -> stockTransferProductRepository.getQtyDetails(filter);
            default -> stockProductRepository.getQtyDetails(filter);
        };
    }

    public WarehouseProductQtyDTO getProductQty(BaseFilter filter) {
        if (filter.getBranchId() == null) {
            throw new BadRequestException("Branch is required!");
        }
        if (filter.getWarehouseId() == null) {
            throw new BadRequestException("Warehouse is required!");
        }
        List<WarehouseProduct> warehouseProductList = warehouseProductRepository.findAllByBranchIdAndWarehouseIdAndDeletedIsFalse(filter.getBranchId(), filter.getWarehouseId());
        if (CollectionUtils.isEmpty(warehouseProductList)) {
            return null;
        }
        WarehouseProductQtyDTO warehouseProductQtyDTO = new WarehouseProductQtyDTO();
        warehouseProductQtyDTO.setBranch(warehouseProductList.getFirst().getWarehouse().getBranch().toCommonDTO());
        warehouseProductQtyDTO.setWarehouse(warehouseProductList.getFirst().getWarehouse().toCommonDTO());
        warehouseProductQtyDTO.setProducts(warehouseProductList.stream().map(warehouseProduct -> {
            ProductQtyDTO productQtyDTO = new ProductQtyDTO();
            productQtyDTO.setId(warehouseProduct.getProduct().getId());
            productQtyDTO.setName(warehouseProduct.getProduct().getName());
            productQtyDTO.setBarcode(warehouseProduct.getProduct().getBarcode());
            productQtyDTO.setVatBarcode(warehouseProduct.getProduct().getVatBarcode());
            productQtyDTO.setCatalogName(warehouseProduct.getProduct().getCatalogName());
            productQtyDTO.setQty(warehouseProduct.getQty());
            return productQtyDTO;
        }).toList());
        return warehouseProductQtyDTO;
    }

    public StockProductQtyStatisticsDTO getProductQtyStatistics(StockFilter filter) {
        return stockProductRepository.getProductQtyStatistics(filter);
    }

    public List<WarehouseProductOfflineDTO> getForOffline(Set<Long> branchIds) {
        List<Long> warehouseIds = warehouseRepository.findActiveIdsByBranchIdIn(branchIds);

        return warehouseProductRepository.findAllByWarehouseIdInAndDeletedFalse(warehouseIds)
                .stream()
                .collect(Collectors.groupingBy(WarehouseProduct::getWarehouseId))
                .entrySet()
                .stream()
                .map(entry -> new WarehouseProductOfflineDTO(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(warehouseProduct -> new WarehouseProductListOfflineDTO(
                                        warehouseProduct.getId(),
                                        warehouseProduct.getProductId(),
                                        Optional.ofNullable(warehouseProduct.getQty()).orElse(BigDecimal.ZERO),
                                        Optional.ofNullable(warehouseProduct.getReservationQty()).orElse(BigDecimal.ZERO)
                                ))
                                .toList()
                ))
                .toList();
    }

    public Page<AggregationCodeDTO> getCodes(AggregationCodesReportFilter filter) {
        if (filter.getProductId() == null) {
            throw validationService.badRequestExceptionThrow("Product id is required", "productId is null").get();
        }
        if (!StringUtils.hasText(filter.getCodeType())) {
            throw validationService.badRequestExceptionThrow("Code type is required").get();
        }
        List<AggregationCodeDTO> codeList = new ArrayList<>();
        long count = 0;

        if (CodeType.valueOf(filter.getCodeType()) == CodeType.AGGREGATION) {
            ResultList<StockProductAggregation> resultList = stockProductAggregationRepository.getCodes(filter);
            count = resultList.getCount();
            resultList.getList()
                    .stream()
                    .collect(Collectors.groupingBy(stockProductAggregation -> stockProductAggregation.getStockProduct().getStock().getTransferDate().toLocalDate()))
                    .forEach((stockDate, stockProductAggregationList) -> {
                        AggregationCodeDTO aggregationCodeDTO = new AggregationCodeDTO();
                        aggregationCodeDTO.setStockDate(stockDate);
                        aggregationCodeDTO.setItems(stockProductAggregationList.stream().map(stockProductAggregation -> {
                            AggregationCodeItemDTO aggregationCodeItemDTO = new AggregationCodeItemDTO();
                            aggregationCodeItemDTO.setId(stockProductAggregation.getId());
                            aggregationCodeItemDTO.setCode(stockProductAggregation.getCode());
                            aggregationCodeItemDTO.setQty(stockProductAggregation.getQty());
                            aggregationCodeItemDTO.setQtyInPackage(stockProductAggregation.getStockProduct().getQtyInPackage());
                            aggregationCodeItemDTO.setPackageType(stockProductAggregation.getPackageType());
                            aggregationCodeItemDTO.setStockDate(stockProductAggregation.getStockProduct().getStock().getTransferDate());
                            if (stockProductAggregation.getStatus() != null) {
                                aggregationCodeItemDTO.setStatus(stockProductAggregation.getStatus().getText());
                            }
                            aggregationCodeItemDTO.setOwnerName(stockProductAggregation.getOwnerName());
                            aggregationCodeItemDTO.setOwnerTin(stockProductAggregation.getOwnerTin());
                            aggregationCodeItemDTO.setComment(stockProductAggregation.getComment());
                            return aggregationCodeItemDTO;
                        }).toList());
                        codeList.add(aggregationCodeDTO);
                    });
        }

        if (CodeType.valueOf(filter.getCodeType()) == CodeType.BLOCK) {
            ResultList<StockProductAggregationBlock> resultList = stockProductAggregationBlockRepository.getCodes(filter);
            count = resultList.getCount();
            resultList.getList()
                    .stream()
                    .collect(Collectors.groupingBy(stockProductAggregationBlock -> stockProductAggregationBlock.getStockProduct().getStock().getTransferDate().toLocalDate()))
                    .forEach((stockDate, stockProductAggregationBlockList) -> {
                        AggregationCodeDTO aggregationCodeDTO = new AggregationCodeDTO();
                        aggregationCodeDTO.setStockDate(stockDate);
                        aggregationCodeDTO.setItems(stockProductAggregationBlockList.stream().map(stockProductAggregationBlock -> {
                            AggregationCodeItemDTO aggregationCodeItemDTO = new AggregationCodeItemDTO();
                            aggregationCodeItemDTO.setId(stockProductAggregationBlock.getId());
                            aggregationCodeItemDTO.setCode(stockProductAggregationBlock.getCode());
                            aggregationCodeItemDTO.setQty(stockProductAggregationBlock.getQty());
                            aggregationCodeItemDTO.setQtyInPackage(stockProductAggregationBlock.getStockProduct().getQtyInPackage());
                            aggregationCodeItemDTO.setPackageType(stockProductAggregationBlock.getPackageType());
                            aggregationCodeItemDTO.setStockDate(stockProductAggregationBlock.getStockProduct().getStock().getTransferDate());
                            if (stockProductAggregationBlock.getStatus() != null) {
                                aggregationCodeItemDTO.setStatus(stockProductAggregationBlock.getStatus().getText());
                            }
                            aggregationCodeItemDTO.setOwnerName(stockProductAggregationBlock.getOwnerName());
                            aggregationCodeItemDTO.setOwnerTin(stockProductAggregationBlock.getOwnerTin());
                            aggregationCodeItemDTO.setComment(stockProductAggregationBlock.getComment());
                            return aggregationCodeItemDTO;
                        }).toList());
                        codeList.add(aggregationCodeDTO);
                    });
        }

        if (CodeType.valueOf(filter.getCodeType()) == CodeType.MARK) {
            ResultList<StockProductMark> resultList = stockProductMarkRepository.getCodes(filter);
            count = resultList.getCount();
            resultList.getList()
                    .stream()
                    .collect(Collectors.groupingBy(stockProductMark -> stockProductMark.getStockProduct().getStock().getTransferDate().toLocalDate()))
                    .forEach((stockDate, stockProductMarkList) -> {
                        AggregationCodeDTO aggregationCodeDTO = new AggregationCodeDTO();
                        aggregationCodeDTO.setStockDate(stockDate);
                        aggregationCodeDTO.setItems(stockProductMarkList.stream().map(stockProductMark -> {
                            AggregationCodeItemDTO aggregationCodeItemDTO = new AggregationCodeItemDTO();
                            aggregationCodeItemDTO.setId(stockProductMark.getId());
                            aggregationCodeItemDTO.setCode(stockProductMark.getCode());
                            aggregationCodeItemDTO.setQty(stockProductMark.getQty());
                            aggregationCodeItemDTO.setQtyInPackage(stockProductMark.getStockProduct().getQtyInPackage());
                            aggregationCodeItemDTO.setPackageType(stockProductMark.getPackageType());
                            aggregationCodeItemDTO.setStockDate(stockProductMark.getStockProduct().getStock().getTransferDate());
                            if (stockProductMark.getStatus() != null) {
                                aggregationCodeItemDTO.setStatus(stockProductMark.getStatus().getText());
                            }
                            aggregationCodeItemDTO.setOwnerName(stockProductMark.getOwnerName());
                            aggregationCodeItemDTO.setOwnerTin(stockProductMark.getOwnerTin());
                            aggregationCodeItemDTO.setComment(stockProductMark.getComment());
                            return aggregationCodeItemDTO;
                        }).toList());
                        codeList.add(aggregationCodeDTO);
                    });
        }
        return new PageImpl<>(codeList, filter.getSortedPageable(), count);
    }

    public AggregationCodeStatisticsDTO getCodeStatistics(AggregationCodesReportFilter filter) {
        if (filter.getProductId() == null) {
            throw validationService.badRequestExceptionThrow("Product id is required", "productId is null").get();
        }
        return stockProductRepository.getAggregationCodeStatistics(filter);
    }

    @Transactional
    public void updateCodes(StockProduct stockProduct, boolean out) {
        Set<Long> aggregationCodes = new LinkedHashSet<>();
        Set<Long> blocks = new LinkedHashSet<>();
        Set<Long> marks = new LinkedHashSet<>();

        Set<Long> parentPallets = new LinkedHashSet<>();
        Set<Long> parentBoxes = new LinkedHashSet<>();
        Set<Long> parentBlocks = new LinkedHashSet<>();

        List<AggregationCodesPayload> payloads = new ArrayList<>();

        int foundCodes = 0;
        int processedCodes = 0;
        int skippedCodes = 0;

        List<String> aggregationCodeList = stockProduct.getStockProductAggregations().stream()
                .map(StockProductAggregation::getCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(aggregationCodeList)) {
            aggregationCodeList = stockProductAggregationRepository.findAllCodesByStockProductIdAndDeletedIsFalse(stockProduct.getId());
        }
        if (CollectionUtils.isNotEmpty(aggregationCodeList)) {
            List<StockProductAggregation> aggregations = new ArrayList<>();
            for (List<String> part : Lists.partition(aggregationCodeList, 500)) {
                aggregations.addAll(stockProductAggregationRepository.findAllByCodeAndProductIdAndDeleteIsFalse(part, stockProduct.getProductId(), TransferType.IN));
            }
            foundCodes += aggregations.size();
            log.info("========================================================= Code qty: {} =========================================================", aggregations.size());
            for (StockProductAggregation stockProductAggregation : aggregations) {
                if (stockProductAggregation.getPackageType() == null) {
                    skippedCodes++;
                    log.warn("Package type is null, code skipped. id: {}, originalCode: {}", stockProductAggregation.getId(), stockProductAggregation.getOriginalCode());
                    continue;
                }
                switch (stockProductAggregation.getPackageType()) {
                    case PALLET:
                    case LEVEL3: {
                        if (out && stockProductAggregation.isUsed()) {
                            log.warn("Этот код: {} был использован", stockProductAggregation.getOriginalCode());
                            if (VALIDATE_USED_CODES) {
                                String errorMessage = "Этот код: " + stockProductAggregation.getOriginalCode() + " был использован";
                                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), errorMessage).get();
                            }
                        }
                        parentPallets.add(stockProductAggregation.getId());
                        processedCodes++;
                        break;
                    }
                    case BOX:
                    case LEVEL2: {
                        if (out && stockProductAggregation.isUsed()) {
                            log.warn("Этот код: {} был использован", stockProductAggregation.getOriginalCode());
                            if (VALIDATE_USED_CODES) {
                                String errorMessage = "Этот код: " + stockProductAggregation.getOriginalCode() + " был использован";
                                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), errorMessage).get();
                            }
                        }
                        parentBoxes.add(stockProductAggregation.getId());
                        if (!stockProductAggregation.isParent() && stockProductAggregation.getPallet() != null) {
                            aggregationCodes.add(stockProductAggregation.getPallet().getId());
                        }
                        processedCodes++;
                        break;
                    }
                    default:
                        skippedCodes++;
                        log.warn("Unsupported package type: {}, code skipped. id: {}, originalCode: {}", stockProductAggregation.getPackageType(), stockProductAggregation.getId(), stockProductAggregation.getOriginalCode());
                }
            }
        }

        // ============================ blocks (BLOCK) ============================
        List<String> blockCodeList = stockProduct.getStockProductAggregationBlocks().stream()
                .map(StockProductAggregationBlock::getCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(blockCodeList)) {
            blockCodeList = stockProductAggregationBlockRepository.findAllCodesByStockProductIdAndDeletedIsFalse(stockProduct.getId());
        }
        if (CollectionUtils.isNotEmpty(blockCodeList)) {
            List<StockProductAggregationBlock> aggregationBlocks = new ArrayList<>();
            for (List<String> part : Lists.partition(blockCodeList, 500)) {
                aggregationBlocks.addAll(stockProductAggregationBlockRepository.findAllByCodeAndProductIdAndDeleteIsFalse(part, stockProduct.getProductId(), TransferType.IN));
            }
            foundCodes += aggregationBlocks.size();
            log.info("========================================================= Code qty: {} =========================================================", aggregationBlocks.size());
            for (StockProductAggregationBlock block : aggregationBlocks) {
                if (block.getPackageType() == null) {
                    skippedCodes++;
                    log.warn("Package type is null, code skipped. id: {}, originalCode: {}", block.getId(), block.getOriginalCode());
                    continue;
                }
                switch (block.getPackageType()) {
                    case BLOCK:
                    case LEVEL1: {
                        if (out && block.isUsed()) {
                            log.warn("Этот код: {} был использован", block.getOriginalCode());
                            if (VALIDATE_USED_CODES) {
                                String errorMessage = "Этот код: " + block.getOriginalCode() + " был использован";
                                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), errorMessage).get();
                            }
                        }
                        parentBlocks.add(block.getId());
                        if (!block.isParent()) {
                            if (block.getPallet() != null) {
                                aggregationCodes.add(block.getPallet().getId());
                            }
                            if (block.getBox() != null) {
                                StockProductAggregation box = block.getBox();
                                aggregationCodes.add(box.getId());
                                if (!box.isParent() && box.getPallet() != null) {
                                    aggregationCodes.add(box.getPallet().getId());
                                }
                            }
                        }
                        processedCodes++;
                        break;
                    }
                    default:
                        skippedCodes++;
                        log.warn("Unsupported package type: {}, code skipped. id: {}, originalCode: {}", block.getPackageType(), block.getId(), block.getOriginalCode());
                }
            }
        }

        // ============================ marks ============================
        List<String> markCodeList = stockProduct.getStockProductMarks().stream()
                .map(StockProductMark::getCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(markCodeList)) {
            markCodeList = stockProductMarkRepository.findAllCodesByStockProductIdAndDeletedIsFalse(stockProduct.getId());
        }
        if (CollectionUtils.isNotEmpty(markCodeList)) {
            List<StockProductMark> stockProductMarks = new ArrayList<>();
            for (List<String> part : Lists.partition(markCodeList, 500)) {
                stockProductMarks.addAll(stockProductMarkRepository.findAllByCodeAndProductIdAndDeleteIsFalse(part, stockProduct.getProductId(), TransferType.IN));
            }
            foundCodes += stockProductMarks.size();
            log.info("========================================================= Code qty: {} =========================================================", stockProductMarks.size());
            for (StockProductMark mark : stockProductMarks) {
                if (out && mark.isUsed()) {
                    log.warn("Этот код: {} был использован", mark.getOriginalCode());
                    if (VALIDATE_USED_CODES) {
                        String errorMessage = "Этот код: " + mark.getOriginalCode() + " был использован";
                        throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), errorMessage).get();
                    }
                }
                marks.add(mark.getId());
                if (mark.getBox() != null) {
                    aggregationCodes.add(mark.getBox().getId());
                    StockProductAggregation box = stockProductAggregationRepository.findById(mark.getBox().getId()).orElse(null);
                    if (box != null && !box.isParent() && box.getPallet() != null) {
                        aggregationCodes.add(box.getPallet().getId());
                    }
                }
                if (mark.getBlock() != null) {
                    StockProductAggregationBlock block = mark.getBlock();
                    blocks.add(block.getId());
                    if (!block.isParent()) {
                        if (block.getBox() != null) {
                            StockProductAggregation box = block.getBox();
                            aggregationCodes.add(box.getId());
                            if (!box.isParent() && box.getPallet() != null) {
                                aggregationCodes.add(box.getPallet().getId());
                            }
                        }
                        if (block.getPallet() != null) {
                            aggregationCodes.add(block.getPallet().getId());
                        }
                    }
                }
                processedCodes++;
            }
        }

        if (CollectionUtils.isNotEmpty(parentPallets)) {
            Lists.partition(new ArrayList<>(parentPallets), 10).forEach(palletIds -> payloads.add(new AggregationCodesPayload(palletIds, out, PackageType.PALLET)));
        }
        if (CollectionUtils.isNotEmpty(parentBoxes)) {
            Lists.partition(new ArrayList<>(parentBoxes), 50).forEach(boxIds -> payloads.add(new AggregationCodesPayload(boxIds, out, PackageType.BOX)));
        }
        if (CollectionUtils.isNotEmpty(parentBlocks)) {
            Lists.partition(new ArrayList<>(parentBlocks), 50).forEach(blockIds -> payloads.add(new AggregationCodesPayload(blockIds, out, PackageType.BLOCK)));
        }

        // synchronous DB update stays inside the transaction
        this.updateCodes(new ArrayList<>(aggregationCodes), new ArrayList<>(blocks), new ArrayList<>(marks), out);

        // Case 2: publish parent-code batches to RabbitMQ only after the transaction commits
        if (CollectionUtils.isNotEmpty(payloads)) {
            afterCommitExecutor.execute(() -> {
                for (AggregationCodesPayload payload : payloads) {
                    rabbitMqAggregationCodesProducer.update(payload);
                    log.info("Published aggregation codes batch after commit. packageType: {}, out: {}, qty: {}", payload.getPackageType(), payload.isOut(), payload.getIds().size());
                }
            });
        }

        log.info("updateCodes finished. stockProductId: {}, out: {}, found: {}, processed: {}, skipped: {}", stockProduct.getId(), out, foundCodes, processedCodes, skippedCodes);
    }

    @Transactional
    public void updateCodes(List<Long> aggregationCodes, List<Long> blockCodes, List<Long> markCodes, boolean out) {
        if (CollectionUtils.isNotEmpty(aggregationCodes)) {
            stockProductAggregationRepository.updateCodes(aggregationCodes, out);
        }
        if (CollectionUtils.isNotEmpty(blockCodes)) {
            stockProductAggregationBlockRepository.updateCodes(blockCodes, out);
        }
        if (CollectionUtils.isNotEmpty(markCodes)) {
            stockProductMarkRepository.updateCodes(markCodes, out);
        }
    }

    @Transactional
    public void updateBoxCodes(List<Long> boxIds, boolean out) {
        long currentTime = System.currentTimeMillis();
        log.info("========================================= UPDATE BOX CODES STARTED =========================================");
        List<Long> aggregationCodes = new ArrayList<>();
        List<Long> blockCodes = new ArrayList<>();
        List<Long> markCodes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(boxIds)) {
            //box-block-mark
            List<Long> childBlockIds = stockProductAggregationBlockRepository.findAllByBoxIdInAndDeletedIsFalse(boxIds);
            if (CollectionUtils.isNotEmpty(childBlockIds)) {
                blockCodes.addAll(childBlockIds);
                markCodes.addAll(stockProductMarkRepository.findAllByBlockIdInAndDeletedIsFalse(childBlockIds));
            }
            //box-mark
            aggregationCodes.addAll(boxIds);
            List<Long> childMarkIds = stockProductMarkRepository.findAllByBoxIdInAndDeletedIsFalse(boxIds);
            markCodes.addAll(childMarkIds);
        }
        this.updateCodes(aggregationCodes, blockCodes, markCodes, out);
        log.info("========================================= UPDATE BOX CODES ENDED: {} =========================================", System.currentTimeMillis() - currentTime);
    }

    @Transactional
    public void updateBlockCodes(List<Long> blockIds, boolean out) {
        long currentTime = System.currentTimeMillis();
        log.info("========================================= UPDATE BLOCK CODES STARTED =========================================");
        List<Long> blockCodes = new ArrayList<>();
        List<Long> markCodes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(blockIds)) {
            blockCodes.addAll(blockIds);
            List<Long> childMarkIds = stockProductMarkRepository.findAllByBlockIdInAndDeletedIsFalse(blockIds);
            markCodes.addAll(childMarkIds);
        }
        this.updateCodes(null, blockCodes, markCodes, out);
        log.info("========================================= UPDATE BLOCK CODES ENDED: {} =========================================", System.currentTimeMillis() - currentTime);
    }

    @Transactional
    public void updatePalletCodes(List<Long> palletIds, boolean out) {
        long currentTime = System.currentTimeMillis();
        log.info("========================================= UPDATE PALLET CODES STARTED =========================================");
        List<Long> aggregationCodes = new ArrayList<>();
        List<Long> blockCodes = new ArrayList<>();
        List<Long> markCodes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(palletIds)) {
            aggregationCodes.addAll(palletIds);
            //pallet-box-block-mark
            List<Long> childBoxIds = stockProductAggregationRepository.findAllByPalletIdInAndDeletedIsFalse(palletIds);
            if (CollectionUtils.isNotEmpty(childBoxIds)) {
                aggregationCodes.addAll(childBoxIds);
                List<Long> childBlockIds = stockProductAggregationBlockRepository.findAllByBoxIdInAndDeletedIsFalse(childBoxIds);
                if (CollectionUtils.isNotEmpty(childBlockIds)) {
                    blockCodes.addAll(childBlockIds);
                    markCodes.addAll(stockProductMarkRepository.findAllByBlockIdInAndDeletedIsFalse(childBlockIds));
                }
                markCodes.addAll(stockProductMarkRepository.findAllByBoxIdInAndDeletedIsFalse(childBoxIds));
            }
            List<Long> childMarkIds = stockProductMarkRepository.findAllByBoxIdInAndDeletedIsFalse(childBoxIds);
            markCodes.addAll(childMarkIds);

            // pallet-block-mark
            List<Long> childBlockIds = stockProductAggregationBlockRepository.findAllByPalletIdInAndDeletedIsFalse(palletIds);
            if (CollectionUtils.isNotEmpty(childBlockIds)) {
                blockCodes.addAll(childBlockIds);
                markCodes.addAll(stockProductMarkRepository.findAllByBlockIdInAndDeletedIsFalse(childBlockIds));
            }

            //pallet-mark
            markCodes.addAll(stockProductMarkRepository.findAllByPalletIdInAndDeletedIsFalse(palletIds));
        }
        this.updateCodes(aggregationCodes, blockCodes, markCodes, out);
        log.info("========================================= UPDATE PALLET CODES ENDED: {} =========================================", System.currentTimeMillis() - currentTime);
    }

    @Transactional
    public void updateCodes(AggregationCodesPayload payload) {
        if (PackageType.isPallet(payload.getPackageType())) {
            this.updatePalletCodes(payload.getIds(), payload.isOut());
        }
        if (PackageType.isBox(payload.getPackageType())) {
            this.updateBoxCodes(payload.getIds(), payload.isOut());
        }
        if (PackageType.isBlock(payload.getPackageType())) {
            this.updateBlockCodes(payload.getIds(), payload.isOut());
        }
    }

    @Transactional
    public boolean checkStockStatus(Stock stock) {
        if (!stockProductRepository.existsByStockIdAndDeletedIsFalseAndCheckedIsFalse(stock.getId())) {
            stock.setStatus(StockStatus.DRAFT);
            stockInRepository.save(stock);
            return true;
        }
        return false;
    }

    @Transactional
    public void updateQty(Long productId) {
        for (WarehouseProduct warehouseProduct : warehouseProductRepository.findAllByProductIdAndDeletedIsFalse(productId)) {
            StockFilter filter = new StockFilter();
            filter.setCompanyId(warehouseProduct.getProduct().getCompanyId());
            filter.setProductId(productId);
            filter.setWarehouseId(warehouseProduct.getWarehouse().getId());
            filter.setBranchId(warehouseProduct.getWarehouse().getBranchId());
            StockProductQtyStatisticsDTO statisticsDTO = this.getProductQtyStatistics(filter);
            BigDecimal qty = BigDecimal.ZERO;
            qty = qty.add(Optional.ofNullable(statisticsDTO.getStockInTotal()).orElse(BigDecimal.ZERO));
            qty = qty.subtract(Optional.ofNullable(statisticsDTO.getStockOutTotal()).orElse(BigDecimal.ZERO));
            qty = qty.add(Optional.ofNullable(statisticsDTO.getStockReturnTotal()).orElse(BigDecimal.ZERO));
            qty = qty.subtract(Optional.ofNullable(statisticsDTO.getStockAdjustmentTotal()).orElse(BigDecimal.ZERO));
            qty = qty.subtract(Optional.ofNullable(statisticsDTO.getStockTransferFromTotal()).orElse(BigDecimal.ZERO));
            qty = qty.add(Optional.ofNullable(statisticsDTO.getStockTransferToTotal()).orElse(BigDecimal.ZERO));
            warehouseProduct.setQty(qty);
            warehouseProductRepository.save(warehouseProduct);
        }
    }

    @Transactional
    public void batchUpdateCodeStatus() {
//        List<CompanyAggregationInfo> companyAggregationInfoList = companyAggregationInfoRepository.findAllByAslBelgiTokenIsNotNullAndDeletedIsFalseAndCompanyStatusAndCompanyDeletedIsFalse(CompanyStatus.ACTIVE);
        List<XtraceTokenDetailDTO> xtraceTokenList = xtraceTokenService.getAllXtraceTokens();
        for (XtraceTokenDetailDTO xtraceTokenDetailDTO : xtraceTokenList) {
            boolean isXtraceEnabled = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(xtraceTokenDetailDTO.getCompanyId(), GenericSettingsType.XTRACE_ENABLED).map(CompanySettings::isEnabled).orElse(false);
            AtomicReference<String> token = new AtomicReference<>();
            if (isXtraceEnabled) {
                token.set(xtraceTokenDetailDTO.getApiKey());
            } else {
                companyAggregationInfoRepository.findByCompanyId(xtraceTokenDetailDTO.getCompanyId()).ifPresent(companyAggregationInfo ->
                    token.set(companyAggregationInfo.getAslBelgiToken())
                );
            }
            BalanceReportFilter balanceReportFilter = new BalanceReportFilter();
            balanceReportFilter.setCompanyId(xtraceTokenDetailDTO.getCompanyId());
            balanceReportFilter.setSize(Integer.MAX_VALUE);
            List<WarehouseProduct> warehouseProductList = warehouseProductRepository.getResultList(balanceReportFilter).getList();
            for (WarehouseProduct warehouseProduct : warehouseProductList) {
                AggregationCodesReportFilter aggregationCodesReportFilter = new AggregationCodesReportFilter();
                aggregationCodesReportFilter.setProductId(warehouseProduct.getProductId());
                aggregationCodesReportFilter.setWarehouseId(warehouseProduct.getWarehouseId());
                aggregationCodesReportFilter.setSize(Integer.MAX_VALUE);

                // aggregation
                Map<String, StockProductAggregation> stockProductAggregationMap = stockProductAggregationRepository.getCodes(aggregationCodesReportFilter).getList().stream().collect(Collectors.toMap(StockProductAggregation::getCode, Function.identity()));
                if (!stockProductAggregationMap.isEmpty()) {
                    try {
                        CisInfoResponseResult cisInfoResponseResult = isXtraceEnabled
                                ? aslBelgiClient.checkPrivateInfo(token.get(), stockProductAggregationMap.keySet().stream().toList(), CodeType.AGGREGATION, stockProductAggregationMap.entrySet().iterator().next().getValue().getProduct().getProductType())
                                : aslBelgiClient.checkAggregatedPublicList(token.get(), stockProductAggregationMap.keySet().stream().toList(), CodeType.AGGREGATION, stockProductAggregationMap.entrySet().iterator().next().getValue().getProduct().getProductType());
                        cisInfoResponseResult.getCisInfoResponses().forEach(cisInfoResponse -> {
                            StockProductAggregation stockProductAggregation = stockProductAggregationMap.get(cisInfoResponse.getCisInfo().getOriginalCode());
                            if (StringUtils.isEmpty(cisInfoResponse.getCisInfo().getErrorCode())) {
                                stockProductAggregation.setStatus(cisInfoResponse.getCisInfo().getStatus());
                                stockProductAggregation.setOwnerName(cisInfoResponse.getCisInfo().getOwnerName());
                                stockProductAggregation.setOwnerTin(cisInfoResponse.getCisInfo().getOwnerInn());
                                stockProductAggregation.setComment(null);
                            } else {
                                stockProductAggregation.setComment(cisInfoResponse.getCisInfo().getErrorCode());
                            }
                        });
                    } catch (Exception e) {
                        stockProductAggregationMap.values().forEach(stockProductAggregation -> stockProductAggregation.setComment(e.getMessage()));
                    }
                    stockProductAggregationRepository.saveAll(stockProductAggregationMap.values());
                }

                // aggregation block
                Map<String, StockProductAggregationBlock> stockProductAggregationBlockMap = stockProductAggregationBlockRepository.getCodes(aggregationCodesReportFilter).getList().stream().collect(Collectors.toMap(StockProductAggregationBlock::getCode, Function.identity()));
                if (!stockProductAggregationBlockMap.isEmpty()) {
                    try {
                        CisInfoResponseResult cisInfoResponseResult = isXtraceEnabled
                                ? aslBelgiClient.checkPrivateInfo(token.get(), stockProductAggregationBlockMap.keySet().stream().toList(), CodeType.BLOCK, stockProductAggregationBlockMap.entrySet().iterator().next().getValue().getProduct().getProductType())
                                : aslBelgiClient.checkAggregatedPublicList(token.get(), stockProductAggregationBlockMap.keySet().stream().toList(), CodeType.BLOCK, stockProductAggregationBlockMap.entrySet().iterator().next().getValue().getProduct().getProductType());
                        cisInfoResponseResult.getCisInfoResponses().forEach(cisInfoResponse -> {
                            StockProductAggregationBlock stockProductAggregationBlock = stockProductAggregationBlockMap.get(cisInfoResponse.getCisInfo().getOriginalCode());
                            if (StringUtils.isEmpty(cisInfoResponse.getCisInfo().getErrorCode())) {
                                stockProductAggregationBlock.setStatus(cisInfoResponse.getCisInfo().getStatus());
                                stockProductAggregationBlock.setOwnerName(cisInfoResponse.getCisInfo().getOwnerName());
                                stockProductAggregationBlock.setOwnerTin(cisInfoResponse.getCisInfo().getOwnerInn());
                                stockProductAggregationBlock.setComment(null);
                            } else {
                                stockProductAggregationBlock.setComment(cisInfoResponse.getCisInfo().getErrorCode());
                            }
                        });
                    } catch (Exception e) {
                        stockProductAggregationBlockMap.values().forEach(stockProductAggregationBlock -> stockProductAggregationBlock.setComment(e.getMessage()));
                    }
                    stockProductAggregationBlockRepository.saveAll(stockProductAggregationBlockMap.values());
                }

                // mark
                Map<String, StockProductMark> stockProductMarkMap = stockProductMarkRepository.getCodes(aggregationCodesReportFilter).getList().stream().collect(Collectors.toMap(StockProductMark::getCode, Function.identity()));
                if (!stockProductMarkMap.isEmpty()) {
                    try {
                        CisInfoResponseResult cisInfoResponseResult = isXtraceEnabled
                                ? aslBelgiClient.checkPrivateInfo(token.get(), stockProductMarkMap.keySet().stream().toList(), CodeType.MARK, stockProductMarkMap.entrySet().iterator().next().getValue().getProduct().getProductType())
                                : aslBelgiClient.checkAggregatedPublicList(token.get(), stockProductMarkMap.keySet().stream().toList(), CodeType.MARK, stockProductMarkMap.entrySet().iterator().next().getValue().getProduct().getProductType());
                        cisInfoResponseResult.getCisInfoResponses().forEach(cisInfoResponse -> {
                            StockProductMark stockProductMark = stockProductMarkMap.get(cisInfoResponse.getCisInfo().getOriginalCode());
                            if (StringUtils.isEmpty(cisInfoResponse.getCisInfo().getErrorCode())) {
                                stockProductMark.setStatus(cisInfoResponse.getCisInfo().getStatus());
                                stockProductMark.setOwnerName(cisInfoResponse.getCisInfo().getOwnerName());
                                stockProductMark.setOwnerTin(cisInfoResponse.getCisInfo().getOwnerInn());
                                stockProductMark.setComment(null);
                            } else {
                                stockProductMark.setComment(cisInfoResponse.getCisInfo().getErrorCode());
                            }
                        });
                    } catch (Exception e) {
                        stockProductMarkMap.values().forEach(stockProductMark -> stockProductMark.setComment(e.getMessage()));
                    }
                    stockProductMarkRepository.saveAll(stockProductMarkMap.values());
                }
            }
        }
    }

    public BigDecimal getWarehouseQty(Long warehouseId, Long productId) {
        return warehouseProductRepository.getQty(warehouseId, productId, getUnit().getId());
    }

    public BigDecimal getWarehouseQty(Long productId) {
        return warehouseProductRepository.getQty(SecurityUtils.getCurrentWarehouseId(), productId, getUnit().getId());
    }
}