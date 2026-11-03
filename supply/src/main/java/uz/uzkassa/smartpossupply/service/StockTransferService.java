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
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.category.Category;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyCommittent;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.domain.order.OrderProduct;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregation;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregationBlock;
import uz.uzkassa.smartpossupply.domain.stock.StockProductMark;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransfer;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransferProduct;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransferProductAggregation;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransferProductAggregationBlock;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransferProductMark;
import uz.uzkassa.smartpossupply.domain.trip.Trip;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseExpeditor;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseProduct;
import uz.uzkassa.smartpossupply.domain.waybill.Waybill;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.category.EDocsGenerateDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductDTO;
import uz.uzkassa.smartpossupply.dto.report.WarehouseProductReportDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.ExpeditorStockTransferDetailDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.ExpeditorStockTransferProductDetailDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.ExpeditorStockTransferStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferCreateFromOrderDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferCreateFromWarehouseProductDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferDetailDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferListDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferProductAggregationsDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferProductDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferProductDetailDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferUpdateStatusDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.TerminalStockTransferDetailDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.TerminalStockTransferListDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.TerminalStockTransferProductDetailDTO;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;
import uz.uzkassa.smartpossupply.enums.StockTransferType;
import uz.uzkassa.smartpossupply.enums.TemplateType;
import uz.uzkassa.smartpossupply.enums.TemplateTypeAlias;
import uz.uzkassa.smartpossupply.enums.TripStatus;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.StockTransferFilter;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqNotificationProducer;
import uz.uzkassa.smartpossupply.repository.CategoryRepository;
import uz.uzkassa.smartpossupply.repository.OrderProductRepository;
import uz.uzkassa.smartpossupply.repository.OrderRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.repository.ProductSerialWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.StockOutRepository;
import uz.uzkassa.smartpossupply.repository.StockProductRepository;
import uz.uzkassa.smartpossupply.repository.StockTransferProductAggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.StockTransferProductAggregationRepository;
import uz.uzkassa.smartpossupply.repository.StockTransferProductMarkRepository;
import uz.uzkassa.smartpossupply.repository.StockTransferProductRepository;
import uz.uzkassa.smartpossupply.repository.StockTransferRepository;
import uz.uzkassa.smartpossupply.repository.TripRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseExpeditorRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseProductRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class StockTransferService extends BaseService {

    StockTransferRepository stockTransferRepository;
    StockOutRepository stockOutRepository;
    WarehouseProductRepository warehouseProductRepository;
    StockTransferProductRepository stockTransferProductRepository;
    StockTransferProductAggregationRepository stockTransferProductAggregationRepository;
    StockTransferProductMarkRepository stockTransferProductMarkRepository;
    StockTransferProductAggregationBlockRepository stockTransferProductAggregationBlockRepository;
    StockProductRepository stockProductRepository;
    ProductService productService;
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    StockInService stockInService;
    WarehouseRepository warehouseRepository;
    WarehouseProductService warehouseProductService;
    ProductSerialWarehouseRepository productSerialWarehouseRepository;
    CisInfoService cisInfoService;
    TripService tripService;
    OrderRepository orderRepository;
    OrderProductRepository orderProductRepository;
    WarehouseExpeditorRepository warehouseExpeditorRepository;
    OrderService orderService;
    EDocService eDocService;
    NumberingContext numberingContext;
    TripRepository tripRepository;
    PackageQuantityService packageQuantityService;
    RabbitMqNotificationProducer notificationProducer;
    ValidationContext validationContext;

    @Transactional
    public Long create(StockTransferDTO stockTransferDTO) {
        validationContext.validate(stockTransferDTO);
        EnvironmentType environmentType = SecurityUtils.getEnvironmentType();
        Company company = getCurrentCompany();
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_TRANSFER, company.getId());
        Unit unit = getUnit();
        StockTransfer stockTransfer = new StockTransfer();
        stockTransfer.setTransferNumber(numberingMap.get(NUMBER));
        stockTransfer.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stockTransfer.setCompanyId(company.getId());
        stockTransfer.setStockTransferType(StockTransferType.valueOf(Optional.ofNullable(stockTransferDTO.getStockTransferType()).orElse(StockTransferType.W2W.getCode())));
        stockTransfer.setTransferDate(Optional.ofNullable(stockTransferDTO.getTransferDate()).orElse(LocalDateTime.now()));
        stockTransfer.setDescription(stockTransferDTO.getDescription());
        stockTransfer.setFromBranchId(stockTransferDTO.getFromBranchId());
        stockTransfer.setToBranchId(stockTransferDTO.getToBranchId());
        stockTransfer.setFromWarehouseId(stockTransferDTO.getFromWarehouseId());
        stockTransfer.setToWarehouseId(stockTransferDTO.getToWarehouseId());
        stockTransfer.setExpeditorId(stockTransferDTO.getExpeditorId());
/*        if (stockTransferDTO.getTripId() != null) {
            Trip trip = tripRepository.findById(stockTransferDTO.getTripId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Trip.class.getSimpleName(), "id", stockTransferDTO.getTripId()));
            stockTransfer.setTripId(trip.getId());
            stockTransfer.setTrip(trip);
        }*/
        stockTransfer.setTotalBrutto(stockTransferDTO.getTotalBrutto());
        stockTransfer.setTotalNetto(stockTransferDTO.getTotalNetto());
        stockTransfer.setTotalQty(stockTransferDTO.getTotalQty());
        stockTransfer.setTotalPrice(stockTransferDTO.getTotalPrice());
        stockTransfer.setOrderCount(stockTransferDTO.getOrderCount());
        stockTransfer = stockTransferRepository.save(stockTransfer);

        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalBlockQty = BigDecimal.ZERO;
        Set<Long> orderIds = new HashSet<>();
        for (StockTransferProductDTO stockTransferProductDTO : stockTransferDTO.getProducts()) {
            Product fromProduct = productRepository
                    .findById(stockTransferProductDTO.getId())
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockTransferProductDTO.getId()));

            Product toProduct = productService.getProductByBarcodeAndVatBarcode(fromProduct.getBarcode(), fromProduct.getVatBarcode(), stockTransferDTO.getToBranchId());
            if (toProduct == null) {
                Category toCategory = null;
                if (StringUtils.isNotEmpty(fromProduct.getVatBarcode())) {
                    toCategory =
                            categoryRepository.findFirstByBranchIdAndCatalogCodeAndEnvironmentTypeAndDeletedIsFalse(
                                    stockTransferDTO.getToBranchId(),
                                    MarkUtils.clearVatBarcode(fromProduct.getVatBarcode()),
                                    environmentType
                            ).orElse(null);
                }
                if (toCategory == null) {
                    toCategory =
                            categoryRepository.findFirstByMainIsTrueAndBranchIdAndDeletedIsFalse(stockTransferDTO.getToBranchId()).orElse(null);
                    if (toCategory == null) {
                        toCategory =
                                categoryRepository
                                        .findFirstByBranchIdAndDeletedIsFalse(stockTransferDTO.getToBranchId())
                                        .orElseThrow(
                                                validationService.entityNotFoundExceptionThrow(Category.class.getSimpleName(), "branchId", stockTransferDTO.getToBranchId())
                                        );
                    }
                }

                if (fromProduct.getPrice() == null || fromProduct.getPrice().compareTo(BigDecimal.ZERO) == 0) {
                    throw validationService.badRequestExceptionThrow("Цена продукта равна нулю", "Цена продукта" + fromProduct.getName().concat(" ").concat(Optional.ofNullable(fromProduct.getBarcode()).orElse(" ")).concat(" равна нулю")).get();
                }

                ProductDTO productDTO = ProductDTO
                        .builder()
                        .tasnifId(fromProduct.getTasnifId())
                        .name(fromProduct.getName())
                        .brand(fromProduct.getBrand())
                        .barcode(fromProduct.getBarcode())
                        .vatBarcode(fromProduct.getVatBarcode())
                        .catalogName(fromProduct.getCatalogName())
                        .packageName(fromProduct.getPackageName())
                        .packageCode(fromProduct.getPackageCode())
                        .qtyInPackage(fromProduct.getQtyInPackage())
                        .qtyInBox(fromProduct.getQtyInBox())
                        .qtyInPallet(fromProduct.getQtyInPallet())
                        .capacity(fromProduct.getCapacity())
                        .productType(fromProduct.getProductType())
                        .unitId(fromProduct.getUnitId())
                        .vat(fromProduct.getVat())
                        .price(fromProduct.getPrice())
                        .categoryIds(List.of(toCategory.getId()))
                        .branchId(stockTransferDTO.getToBranchId())
                        .companyId(fromProduct.getCompanyId())
                        .hasMark(fromProduct.isHasMark())
                        .build();
                toProduct = productService.createAndReturn(productDTO);
            }

            StockTransferProduct stockTransferProduct = new StockTransferProduct();
            stockTransferProduct.setFromProductId(fromProduct.getId());
            stockTransferProduct.setToProductId(toProduct.getId());
            stockTransferProduct.setOrderId(stockTransferProductDTO.getOrderId());
            stockTransferProduct.setQty(stockTransferProductDTO.getQty());
            stockTransferProduct.setUnitId(unit.getId());
            stockTransferProduct.setStockTransferId(stockTransfer.getId());
            stockTransferProduct.setScannedQty(stockTransferProductDTO.getScannedQty());
            stockTransferProduct.setTotalBrutto(stockTransferProductDTO.getTotalBrutto());
            stockTransferProduct.setTotalNetto(stockTransferProductDTO.getTotalNetto());
            if (fromProduct.getQtyInPackage() != null && fromProduct.getQtyInPackage().compareTo(BigDecimal.ZERO) != 0) {
                stockTransferProduct.setBlockQty(stockTransferProductDTO.getQty().divide(fromProduct.getQtyInPackage(), RoundingMode.DOWN));
                totalBlockQty = totalBlockQty.add(stockTransferProduct.getBlockQty());
            }
            stockTransferProduct.setTotalPrice(stockTransferProductDTO.getTotalPrice());
            stockTransferProduct.setPrice(stockTransferProductDTO.getPrice());
            stockTransferProduct.setProductSerialId(stockTransferProductDTO.getProductSerialId());
            stockTransferProduct = stockTransferProductRepository.save(stockTransferProduct);

            int aggregationCount = 0;
            int markCount = 0;
            int aggregationBlockCount = 0;

            if (!CollectionUtils.isEmpty(stockTransferProductDTO.getAggregationCodes())) {
                aggregationCount = aggregationCount + stockTransferProductDTO.getAggregationCodes().size();
                List<StockTransferProductAggregation> stockTransferProductAggregations = new ArrayList<>();
                for (CheckedCodeDTO checkedCodeDTO : stockTransferProductDTO.getAggregationCodes()) {
                    StockTransferProductAggregation stockTransferProductAggregation = new StockTransferProductAggregation();
                    stockTransferProductAggregation.setStockTransferProductId(stockTransferProduct.getId());
                    stockTransferProductAggregation.setCode(checkedCodeDTO.getCode());
                    stockTransferProductAggregation.setOriginalCode(checkedCodeDTO.getOriginalCode());
                    stockTransferProductAggregation.setQty(checkedCodeDTO.getQty());
                    stockTransferProductAggregation.setProductId(stockTransferProduct.getFromProductId());
                    stockTransferProductAggregation.setPackageType(checkedCodeDTO.getPackageType());
                    stockTransferProductAggregation.setStatus(checkedCodeDTO.getStatus());
                    if (checkedCodeDTO.getOwner() != null) {
                        stockTransferProductAggregation.setOwnerName(checkedCodeDTO.getOwner().getName());
                        stockTransferProductAggregation.setOwnerTin(checkedCodeDTO.getOwner().getTin());
                    }
                    stockTransferProductAggregations.add(stockTransferProductAggregation);
                }
                stockTransferProductAggregationRepository.saveAll(stockTransferProductAggregations);
            }

            if (!CollectionUtils.isEmpty(stockTransferProductDTO.getAggregationBlockCodes())) {
                aggregationBlockCount = aggregationBlockCount + stockTransferProductDTO.getAggregationBlockCodes().size();
                List<StockTransferProductAggregationBlock> stockTransferProductAggregationBlocks = new ArrayList<>();
                for (CheckedCodeDTO checkedCodeDTO : stockTransferProductDTO.getAggregationBlockCodes()) {
                    StockTransferProductAggregationBlock stockTransferProductAggregationBlock = new StockTransferProductAggregationBlock();
                    stockTransferProductAggregationBlock.setStockTransferProductId(stockTransferProduct.getId());
                    stockTransferProductAggregationBlock.setCode(checkedCodeDTO.getCode());
                    stockTransferProductAggregationBlock.setOriginalCode(checkedCodeDTO.getOriginalCode());
                    stockTransferProductAggregationBlock.setQty(checkedCodeDTO.getQty());
                    stockTransferProductAggregationBlock.setProductId(stockTransferProduct.getFromProductId());
                    stockTransferProductAggregationBlock.setPackageType(checkedCodeDTO.getPackageType());
                    stockTransferProductAggregationBlock.setStatus(checkedCodeDTO.getStatus());
                    if (checkedCodeDTO.getOwner() != null) {
                        stockTransferProductAggregationBlock.setOwnerName(checkedCodeDTO.getOwner().getName());
                        stockTransferProductAggregationBlock.setOwnerTin(checkedCodeDTO.getOwner().getTin());
                    }
                    stockTransferProductAggregationBlocks.add(stockTransferProductAggregationBlock);
                }
                stockTransferProductAggregationBlockRepository.saveAll(stockTransferProductAggregationBlocks);
            }

            if (!CollectionUtils.isEmpty(stockTransferProductDTO.getMarkCodes())) {
                markCount = markCount + stockTransferProductDTO.getMarkCodes().size();
                List<StockTransferProductMark> stockTransferProductMarks = new ArrayList<>();
                for (CheckedCodeDTO checkedCodeDTO : stockTransferProductDTO.getMarkCodes()) {
                    StockTransferProductMark stockTransferProductMark = new StockTransferProductMark();
                    stockTransferProductMark.setStockTransferProductId(stockTransferProduct.getId());
                    stockTransferProductMark.setCode(checkedCodeDTO.getCode());
                    stockTransferProductMark.setOriginalCode(checkedCodeDTO.getOriginalCode());
                    stockTransferProductMark.setQty(checkedCodeDTO.getQty());
                    stockTransferProductMark.setProductId(stockTransferProduct.getFromProductId());
                    stockTransferProductMark.setPackageType(checkedCodeDTO.getPackageType());
                    stockTransferProductMark.setStatus(checkedCodeDTO.getStatus());
                    if (checkedCodeDTO.getOwner() != null) {
                        stockTransferProductMark.setOwnerName(checkedCodeDTO.getOwner().getName());
                        stockTransferProductMark.setOwnerTin(checkedCodeDTO.getOwner().getTin());
                    }
                    stockTransferProductMarks.add(stockTransferProductMark);
                }
                stockTransferProductMarkRepository.saveAll(stockTransferProductMarks);
            }

            if (aggregationCount > 0 || markCount > 0 || aggregationBlockCount > 0) {
                stockTransferProduct.setHasMarking(true);
                stockTransferProduct.setAggregationQty(aggregationCount);
                stockTransferProduct.setMarkQty(markCount);
                stockTransferProduct.setAggregationBlockQty(aggregationBlockCount);
                stockTransferProductRepository.save(stockTransferProduct);
            }
            totalQty = totalQty.add(stockTransferProductDTO.getQty());

            if (stockTransferProductDTO.getOrderId() != null) {
                orderIds.add(stockTransferProductDTO.getOrderId());
            }
        }

        stockTransfer.setTotalQty(totalQty);
        stockTransfer.setTotalBlockQty(totalBlockQty);
        stockTransferRepository.save(stockTransfer);
/*        if (stockTransfer.getTrip() != null) {
            tripService.updateStatus(stockTransfer.getTrip(), TripStatus.IN_PROGRESS);
        }*/

        if (!CollectionUtils.isEmpty(orderIds)) {
            this.assignTripToStock(stockTransfer, orderIds, stockTransferDTO.getTripId());
        }
        return stockTransfer.getId();
    }

    @Transactional
    public Long update(Long id, StockTransferDTO stockTransferDTO) {
        EnvironmentType environmentType = SecurityUtils.getEnvironmentType();
        StockTransfer stockTransfer = stockTransferRepository
                .findById(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", id));
        this.validateCompany(stockTransfer.getCompanyId());

        if (StockTransferStatus.DRAFT != stockTransfer.getStatus()) {
            throw validationService.badRequestExceptionThrow(
                    localizationService.localize(MessageConstants.YOU_CAN_UPDATE_ONLY, new Object[]{StockTransferStatus.DRAFT.getName()})
            ).get();
        }
        if (stockTransferDTO.getTripId() != null) {
            Trip trip = tripRepository.findById(stockTransferDTO.getTripId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Trip.class.getSimpleName(), "id", stockTransferDTO.getTripId()));
            if (stockTransferRepository.existsByTripIdAndIdNot(trip.getId(), stockTransfer.getId())) {
                throw validationService.conflictExceptionThrow("Эта поездка уже назначена для другой передача.").get();
            }
            stockTransfer.setTripId(trip.getId());
            tripService.updateStatus(trip, TripStatus.IN_PROGRESS);
        }

        stockTransfer.setTransferDate(Optional.ofNullable(stockTransferDTO.getTransferDate()).orElse(LocalDateTime.now()));
        stockTransfer.setDescription(stockTransferDTO.getDescription());
        stockTransfer.setStockTransferType(StockTransferType.valueOf(Optional.ofNullable(stockTransferDTO.getStockTransferType()).orElse(StockTransferType.W2W.getCode())));

        Branch fromBranch = branchRepository.findById(stockTransferDTO.getFromBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", stockTransferDTO.getFromBranchId()));
        stockTransfer.setFromBranch(fromBranch);
        stockTransfer.setFromBranchId(fromBranch.getId());

        Branch toBranch = branchRepository.findById(stockTransferDTO.getToBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", stockTransferDTO.getToBranchId()));
        stockTransfer.setToBranch(toBranch);
        stockTransfer.setToBranchId(toBranch.getId());

        Warehouse fromWarehouse = warehouseRepository.findById(stockTransferDTO.getFromWarehouseId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Warehouse.class.getSimpleName(), "id", stockTransferDTO.getFromWarehouseId()));
        stockTransfer.setFromWarehouse(fromWarehouse);
        stockTransfer.setFromWarehouseId(fromWarehouse.getId());

        Warehouse toWarehouse = warehouseRepository.findById(stockTransferDTO.getToWarehouseId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Warehouse.class.getSimpleName(), "id", stockTransferDTO.getToWarehouseId()));
        stockTransfer.setToWarehouse(toWarehouse);
        stockTransfer.setToWarehouseId(toWarehouse.getId());

        stockTransfer.setTotalBrutto(stockTransferDTO.getTotalBrutto());
        stockTransfer.setTotalNetto(stockTransferDTO.getTotalNetto());
        stockTransfer.setTotalQty(stockTransferDTO.getTotalQty());
        stockTransfer.setTotalPrice(stockTransferDTO.getTotalPrice());
        if (
                stockTransferDTO.getTransferNumber() != null && !stockTransferDTO.getTransferNumber().equals(stockTransfer.getTransferNumber())
        ) {
            if (
                    stockTransferRepository.existsStockTransferByTransferNumberAndCompanyIdAndIdNot(
                            stockTransferDTO.getTransferNumber(),
                            stockTransfer.getCompanyId(),
                            stockTransfer.getId()
                    )
            ) {
                throw validationService.conflictExceptionThrow(
                        localizationService.localize(
                                MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                                new Object[]{
                                        localizationService.localize(StockTransfer.class.getSimpleName()),
                                        localizationService.localize(MessageConstants.NUMBER),
                                        stockTransferDTO.getTransferNumber(),
                                }
                        )
                ).get();
            }
            stockTransfer.setTransferNumber(stockTransferDTO.getTransferNumber());
        }

        Map<Long, StockTransferProduct> stockTransferProductsMap = stockTransfer
                .getProducts()
                .stream()
                .collect(Collectors.toMap(StockTransferProduct::getId, Function.identity()));

        Unit unit = getUnit();
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalBlockQty = BigDecimal.ZERO;

        for (StockTransferProductDTO stockTransferProductDTO : stockTransferDTO.getProducts()) {
            StockTransferProduct stockTransferProduct;
            if (
                    stockTransferProductDTO.getStockTransferProductId() != null &&
                            stockTransferProductsMap.get(stockTransferProductDTO.getStockTransferProductId()) != null
            ) {
                stockTransferProduct = stockTransferProductsMap.get(stockTransferProductDTO.getStockTransferProductId());
                stockTransferProductsMap.remove(stockTransferProductDTO.getStockTransferProductId());
            } else {
                stockTransferProduct = new StockTransferProduct();
            }

            Product fromProduct = productRepository
                    .findById(stockTransferProductDTO.getId())
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockTransferProductDTO.getId()));

            Product toProduct = productService.getProductByBarcodeAndVatBarcode(fromProduct.getBarcode(), fromProduct.getVatBarcode(), stockTransferDTO.getToBranchId());
            if (toProduct == null) {
                Category toCategory = null;
                if (StringUtils.isNotEmpty(fromProduct.getVatBarcode())) {
                    toCategory =
                            categoryRepository.findFirstByBranchIdAndCatalogCodeAndEnvironmentTypeAndDeletedIsFalse(
                                    stockTransferDTO.getToBranchId(),
                                    MarkUtils.clearVatBarcode(fromProduct.getVatBarcode()),
                                    environmentType
                            ).orElse(null);
                }
                if (toCategory == null) {
                    toCategory =
                            categoryRepository.findFirstByMainIsTrueAndBranchIdAndDeletedIsFalse(stockTransferDTO.getToBranchId()).orElse(null);
                    if (toCategory == null) {
                        toCategory =
                                categoryRepository
                                        .findFirstByBranchIdAndDeletedIsFalse(stockTransferDTO.getToBranchId())
                                        .orElseThrow(
                                                validationService.entityNotFoundExceptionThrow(Category.class.getSimpleName(), "branchId", stockTransferDTO.getToBranchId())
                                        );
                    }
                }

                if (fromProduct.getPrice() == null || fromProduct.getPrice().compareTo(BigDecimal.ZERO) == 0) {
                    throw validationService.badRequestExceptionThrow("Цена продукта равна нулю", "Цена продукта" + fromProduct.getName().concat(" ").concat(fromProduct.getBarcode()).concat(" равна нулю")).get();
                }

                ProductDTO productDTO = ProductDTO
                        .builder()
                        .tasnifId(fromProduct.getTasnifId())
                        .name(fromProduct.getName())
                        .brand(fromProduct.getBrand())
                        .barcode(fromProduct.getBarcode())
                        .vatBarcode(fromProduct.getVatBarcode())
                        .catalogName(fromProduct.getCatalogName())
                        .packageName(fromProduct.getPackageName())
                        .packageCode(fromProduct.getPackageCode())
                        .qtyInPackage(fromProduct.getQtyInPackage())
                        .qtyInBox(fromProduct.getQtyInBox())
                        .qtyInPallet(fromProduct.getQtyInPallet())
                        .capacity(fromProduct.getCapacity())
                        .productType(fromProduct.getProductType())
                        .unitId(fromProduct.getUnitId())
                        .vat(fromProduct.getVat())
                        .price(fromProduct.getPrice())
                        .categoryIds(List.of(toCategory.getId()))
                        .branchId(stockTransferDTO.getToBranchId())
                        .companyId(fromProduct.getCompanyId())
                        .hasMark(fromProduct.isHasMark())
                        .build();
                toProduct = productService.createAndReturn(productDTO);
            }

            stockTransferProduct.setFromProductId(fromProduct.getId());
            stockTransferProduct.setToProductId(toProduct.getId());
            stockTransferProduct.setUnitId(unit.getId());
            stockTransferProduct.setQty(stockTransferProductDTO.getQty());
            stockTransferProduct.setStockTransferId(stockTransfer.getId());
            stockTransferProduct.setOrderId(stockTransferProductDTO.getOrderId());
            if (stockTransferProductDTO.getScannedQty() != null) {
                stockTransferProduct.setScannedQty(stockTransferProductDTO.getScannedQty());
            }
            stockTransferProduct.setTotalBrutto(stockTransferProductDTO.getTotalBrutto());
            stockTransferProduct.setTotalPrice(stockTransferProductDTO.getTotalPrice());
            stockTransferProduct.setPrice(stockTransferProductDTO.getPrice());
            stockTransferProduct.setTotalNetto(stockTransferProductDTO.getTotalNetto());
            stockTransferProduct.setProductSerialId(stockTransferProductDTO.getProductSerialId());
            if (fromProduct.getQtyInPackage() != null && fromProduct.getQtyInPackage().compareTo(BigDecimal.ZERO) != 0) {
                stockTransferProduct.setBlockQty(stockTransferProductDTO.getQty().divide(fromProduct.getQtyInPackage(), RoundingMode.DOWN));
                totalBlockQty = totalBlockQty.add(stockTransferProduct.getBlockQty());
            }
            stockTransferProduct = stockTransferProductRepository.save(stockTransferProduct);


            int aggregationCount = 0;
            int markCount = 0;
            int aggregationBlockCount = 0;

            if (stockTransferProductDTO.getAggregationCodes() != null) {
                stockTransferProductAggregationRepository.deleteAllByStockTransferProductId(stockTransferProduct.getId());
                if (!CollectionUtils.isEmpty(stockTransferProductDTO.getAggregationCodes())) {
                    aggregationCount = aggregationCount + stockTransferProductDTO.getAggregationCodes().size();
                    List<StockTransferProductAggregation> stockTransferProductAggregations = new ArrayList<>();
                    for (CheckedCodeDTO checkedCodeDTO : stockTransferProductDTO.getAggregationCodes()) {
                        StockTransferProductAggregation stockTransferProductAggregation = new StockTransferProductAggregation();
                        stockTransferProductAggregation.setStockTransferProductId(stockTransferProduct.getId());
                        stockTransferProductAggregation.setCode(checkedCodeDTO.getCode());
                        stockTransferProductAggregation.setOriginalCode(checkedCodeDTO.getOriginalCode());
                        stockTransferProductAggregation.setQty(checkedCodeDTO.getQty());
                        stockTransferProductAggregation.setProductId(stockTransferProduct.getFromProductId());
                        stockTransferProductAggregation.setPackageType(checkedCodeDTO.getPackageType());
                        stockTransferProductAggregation.setStatus(checkedCodeDTO.getStatus());
                        if (checkedCodeDTO.getOwner() != null) {
                            stockTransferProductAggregation.setOwnerName(checkedCodeDTO.getOwner().getName());
                            stockTransferProductAggregation.setOwnerTin(checkedCodeDTO.getOwner().getTin());
                        }
                        stockTransferProductAggregations.add(stockTransferProductAggregation);
                    }
                    stockTransferProductAggregationRepository.saveAll(stockTransferProductAggregations);
                }
            }

            if (stockTransferProductDTO.getAggregationBlockCodes() != null) {
                stockTransferProductAggregationBlockRepository.deleteAllByStockTransferProductId(stockTransferProduct.getId());
                if (!CollectionUtils.isEmpty(stockTransferProductDTO.getAggregationBlockCodes())) {
                    aggregationBlockCount = aggregationBlockCount + stockTransferProductDTO.getAggregationBlockCodes().size();
                    List<StockTransferProductAggregationBlock> stockTransferProductAggregationBlocks = new ArrayList<>();
                    for (CheckedCodeDTO checkedCodeDTO : stockTransferProductDTO.getAggregationBlockCodes()) {
                        StockTransferProductAggregationBlock stockTransferProductAggregationBlock = new StockTransferProductAggregationBlock();
                        stockTransferProductAggregationBlock.setStockTransferProductId(stockTransferProduct.getId());
                        stockTransferProductAggregationBlock.setCode(checkedCodeDTO.getCode());
                        stockTransferProductAggregationBlock.setOriginalCode(checkedCodeDTO.getOriginalCode());
                        stockTransferProductAggregationBlock.setQty(checkedCodeDTO.getQty());
                        stockTransferProductAggregationBlock.setProductId(stockTransferProduct.getFromProductId());
                        stockTransferProductAggregationBlock.setPackageType(checkedCodeDTO.getPackageType());
                        stockTransferProductAggregationBlock.setStatus(checkedCodeDTO.getStatus());
                        if (checkedCodeDTO.getOwner() != null) {
                            stockTransferProductAggregationBlock.setOwnerName(checkedCodeDTO.getOwner().getName());
                            stockTransferProductAggregationBlock.setOwnerTin(checkedCodeDTO.getOwner().getTin());
                        }
                        stockTransferProductAggregationBlocks.add(stockTransferProductAggregationBlock);
                    }
                    stockTransferProductAggregationBlockRepository.saveAll(stockTransferProductAggregationBlocks);
                }
            }

            if (stockTransferProductDTO.getMarkCodes() != null) {
                stockTransferProductMarkRepository.deleteAllByStockTransferProductId(stockTransferProduct.getId());
                if (!CollectionUtils.isEmpty(stockTransferProductDTO.getMarkCodes())) {
                    markCount = markCount + stockTransferProductDTO.getMarkCodes().size();
                    List<StockTransferProductMark> stockTransferProductMarks = new ArrayList<>();
                    for (CheckedCodeDTO checkedCodeDTO : stockTransferProductDTO.getMarkCodes()) {
                        StockTransferProductMark stockTransferProductMark = new StockTransferProductMark();
                        stockTransferProductMark.setStockTransferProductId(stockTransferProduct.getId());
                        stockTransferProductMark.setCode(checkedCodeDTO.getCode());
                        stockTransferProductMark.setOriginalCode(checkedCodeDTO.getOriginalCode());
                        stockTransferProductMark.setQty(checkedCodeDTO.getQty());
                        stockTransferProductMark.setProductId(stockTransferProduct.getFromProductId());
                        stockTransferProductMark.setPackageType(checkedCodeDTO.getPackageType());
                        stockTransferProductMark.setStatus(checkedCodeDTO.getStatus());
                        if (checkedCodeDTO.getOwner() != null) {
                            stockTransferProductMark.setOwnerName(checkedCodeDTO.getOwner().getName());
                            stockTransferProductMark.setOwnerTin(checkedCodeDTO.getOwner().getTin());
                        }
                        stockTransferProductMarks.add(stockTransferProductMark);
                    }
                    stockTransferProductMarkRepository.saveAll(stockTransferProductMarks);
                }
            }

            if (stockTransferProductDTO.getAggregationCodes() != null || stockTransferProductDTO.getMarkCodes() != null || stockTransferProductDTO.getAggregationBlockCodes() != null) {
                stockTransferProduct.setHasMarking(aggregationCount > 0 || markCount > 0 || aggregationBlockCount > 0);
                stockTransferProduct.setAggregationQty(aggregationCount);
                stockTransferProduct.setMarkQty(markCount);
                stockTransferProduct.setAggregationBlockQty(aggregationBlockCount);
                stockTransferProductRepository.save(stockTransferProduct);
            }
            totalQty = totalQty.add(stockTransferProductDTO.getQty());
        }
        stockTransfer.setTotalQty(totalQty);
        stockTransfer.setTotalBlockQty(totalBlockQty);
        if (!stockTransferProductsMap.isEmpty()) {
            stockTransferProductRepository.deleteAllByIds(stockTransferProductsMap.keySet());
        }
        stockTransferRepository.save(stockTransfer);
        return id;
    }

    public StockTransferDetailDTO get(Long id) {
        StockTransfer stockTransfer = stockTransferRepository
                .getDetail(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", id));
        this.validateCompany(stockTransfer.getCompanyId());
        StockTransferDetailDTO stockTransferDetailDTO = new StockTransferDetailDTO();
        stockTransferDetailDTO.setId(stockTransfer.getId());
        stockTransferDetailDTO.setTransferDate(stockTransfer.getTransferDate());
        stockTransferDetailDTO.setTransferNumber(stockTransfer.getTransferNumber());
        stockTransferDetailDTO.setStatus(stockTransfer.getStatus());
        if (stockTransfer.getExpeditorId() != null) {
            stockTransferDetailDTO.setExpeditor(stockTransfer.getExpeditor().toCommonDTO());
        }
        if (!CollectionUtils.isEmpty(stockTransfer.getWaybills())) {
            stockTransferDetailDTO.setWaybills(stockTransfer.getWaybills().stream().map(Waybill::toLinkDTO).toList());
        }
        if (StockTransferType.W2C == stockTransfer.getStockTransferType()) {
            stockTransferDetailDTO.setPlanRoutes(orderRepository.getAllPlanRoutesByStockTransferId(stockTransfer.getId()).stream().map(SimpleEntity::toCommonDTO).toList());
            stockTransferDetailDTO.setAgents(orderRepository.getAllAgentsByStockTransferId(stockTransfer.getId()).stream().map(SimpleEntity::toCommonDTO).toList());
        }
        stockTransferDetailDTO.setStockTransferType(stockTransfer.getStockTransferType());
        stockTransferDetailDTO.setFromBranch(stockTransfer.getFromBranch().toCommonDTO());
        stockTransferDetailDTO.setToBranch(stockTransfer.getToBranch().toCommonDTO());
        stockTransferDetailDTO.setFromWarehouse(stockTransfer.getFromWarehouse().toCommonDTO());
        if (stockTransfer.getFromWarehouse().getParentId() != null) {
            stockTransferDetailDTO.setFromParentWarehouse(stockTransfer.getFromWarehouse().getParent().toCommonDTO());
        }
        stockTransferDetailDTO.setToWarehouse(stockTransfer.getToWarehouse().toCommonDTO());
        if (stockTransfer.getToWarehouse().getParentId() != null) {
            stockTransferDetailDTO.setToParentWarehouse(stockTransfer.getToWarehouse().getParent().toCommonDTO());
        }
        stockTransferDetailDTO.setDescription(stockTransfer.getDescription());
        stockTransferDetailDTO.setTotalQty(stockTransfer.getTotalQty());
        stockTransferDetailDTO.setTotalPrice(stockTransfer.getTotalPrice());
        stockTransferDetailDTO.setReason(stockTransfer.getReason());
        stockTransferDetailDTO.setOtherReason(stockTransfer.getOtherReason());
        if (stockTransfer.getStock() != null) {
            stockTransferDetailDTO.setStock(stockTransfer.getStock().toCommonDTO());
        }
        stockTransferDetailDTO.setLastModifiedDate(stockTransfer.getUpdatedDate());
        stockTransferDetailDTO.setTotalBrutto(stockTransfer.getTotalBrutto());
        stockTransferDetailDTO.setTotalNetto(stockTransfer.getTotalNetto());
        if (stockTransfer.getTrip() != null) {
            stockTransferDetailDTO.setTrip(stockTransfer.getTrip().toCommonDTO());
        }
        int totalQtyInPackage = 0;
        for (StockTransferProduct stockTransferProduct : stockTransfer.getProducts()) {
            StockTransferProductDetailDTO stockTransferProductDetailDTO = new StockTransferProductDetailDTO();
            stockTransferProductDetailDTO.setStockTransferProductId(stockTransferProduct.getId());
            stockTransferProductDetailDTO.setId(stockTransferProduct.getFromProductId());
            stockTransferProductDetailDTO.setName(stockTransferProduct.getFromProduct().getName());
            stockTransferProductDetailDTO.setBarcode(stockTransferProduct.getFromProduct().getBarcode());
            stockTransferProductDetailDTO.setVatBarCode(stockTransferProduct.getFromProduct().getVatBarcode());
            stockTransferProductDetailDTO.setQtyInPackage(stockTransferProduct.getFromProduct().getQtyInPackage());
            stockTransferProductDetailDTO.setQtyInBox(stockTransferProduct.getFromProduct().getQtyInBox());
            stockTransferProductDetailDTO.setQtyInPallet(stockTransferProduct.getFromProduct().getQtyInPallet());
            stockTransferProductDetailDTO.setCapacity(stockTransferProduct.getFromProduct().getCapacity());
            stockTransferProductDetailDTO.setHasMark(stockTransferProduct.getFromProduct().isHasMark());
            stockTransferProductDetailDTO.setHasMarking(stockTransferProduct.isHasMarking());
            stockTransferProductDetailDTO.setProductType(stockTransferProduct.getFromProduct().getProductType());
            stockTransferProductDetailDTO.setQty(stockTransferProduct.getQty());
            stockTransferProductDetailDTO.setUnit(stockTransferProduct.getFromProduct().getUnit().toDTO());
            if (stockTransferProduct.getOrder() != null) {
                stockTransferProductDetailDTO.setOrder(stockTransferProduct.getOrder().toCommonDTO());
            }
            stockTransferProductDetailDTO.setAggregationQty(stockTransferProduct.getAggregationQty());
            stockTransferProductDetailDTO.setMarkQty(stockTransferProduct.getMarkQty());
            stockTransferProductDetailDTO.setAggregationBlockQty(stockTransferProduct.getAggregationBlockQty());
            stockTransferProductDetailDTO.setTotalBrutto(stockTransferProduct.getTotalBrutto());
            stockTransferProductDetailDTO.setNetto(stockTransferProduct.getFromProduct().getWeightBrutto());
            stockTransferProductDetailDTO.setBrutto(stockTransferProduct.getFromProduct().getWeightNetto());
            stockTransferProductDetailDTO.setTotalNetto(stockTransferProduct.getTotalNetto());
            stockTransferProductDetailDTO.setPrice(stockTransferProduct.getPrice());
            stockTransferProductDetailDTO.setTotalPrice(stockTransferProduct.getTotalPrice());
            stockTransferProductDetailDTO.setQty(stockTransferProduct.getQty());
            if (stockTransferProduct.getFromProduct().getQtyInPackage() != null) {
                int qtyInPackage = Optional.ofNullable(stockTransferProduct.getQty()).orElse(BigDecimal.ZERO).divide(stockTransferProduct.getFromProduct().getQtyInPackage(), RoundingMode.FLOOR).intValue();
                totalQtyInPackage = totalQtyInPackage + qtyInPackage;
                stockTransferProductDetailDTO.setTotalQtyInPackage(qtyInPackage);
            }
            stockTransferProductDetailDTO.setQtyInWarehouse(
                    warehouseProductRepository.getQty(
                            stockTransfer.getFromWarehouseId(),
                            stockTransferProduct.getFromProductId(),
                            stockTransferProduct.getUnitId()
                    )
            );
            stockTransferProductDetailDTO.setReservationQty(
                    warehouseProductRepository.getReservationQty(
                            stockTransfer.getFromWarehouseId(),
                            stockTransferProduct.getFromProductId(),
                            stockTransferProduct.getUnitId()
                    )
            );
            if (stockTransferProduct.getProductSerialId() != null) {
                stockTransferProductDetailDTO.setProductSerial(stockTransferProduct.getProductSerial().toLookUpDTO());
                stockTransferProductDetailDTO.setQtyInWarehouseBySerial(productSerialWarehouseRepository.getQty(stockTransfer.getFromWarehouseId(), stockTransferProduct.getProductSerialId()));
            }

            stockTransferProductDetailDTO.setBoxBarcode(stockTransferProduct.getFromProduct().getBoxBarcode());
            stockTransferProductDetailDTO.setBlockBarcode(stockTransferProduct.getFromProduct().getBlockBarcode());
            if (stockTransferProduct.getFromProduct().getParent() != null) {
                stockTransferProductDetailDTO.setParent(stockTransferProduct.getFromProduct().getParent().toProductCommonDTO());
                if (StringUtils.isEmpty(stockTransferProductDetailDTO.getBlockBarcode())) {
                    stockTransferProductDetailDTO.setBlockBarcode(stockTransferProduct.getFromProduct().getParent().getBarcode());
                }
            }
            stockTransferProductDetailDTO.setScannedQty(stockTransferProduct.getScannedQty());
            stockTransferProductDetailDTO.setBlockProducts(stockTransferProduct.getFromProduct().getBlockProducts());
            stockTransferDetailDTO.addProduct(stockTransferProductDetailDTO);
        }
        stockTransferDetailDTO.setTotalQtyInPackage(totalQtyInPackage);
        return stockTransferDetailDTO;
    }

    public TerminalStockTransferDetailDTO getForTerminal(Long id) {
        StockTransfer stockTransfer = stockTransferRepository
                .getDetail(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", id));
        this.validateCompany(stockTransfer.getCompanyId());
        TerminalStockTransferDetailDTO stockTransferDetailDTO = new TerminalStockTransferDetailDTO();
        stockTransferDetailDTO.setId(stockTransfer.getId());
        stockTransferDetailDTO.setTransferDate(stockTransfer.getTransferDate());
        stockTransferDetailDTO.setTransferNumber(stockTransfer.getTransferNumber());
        stockTransferDetailDTO.setStatus(stockTransfer.getStatus());
        stockTransferDetailDTO.setStockTransferType(stockTransfer.getStockTransferType());
        stockTransferDetailDTO.setFromBranch(stockTransfer.getFromBranch().toCommonDTO());
        stockTransferDetailDTO.setToBranch(stockTransfer.getToBranch().toCommonDTO());
        stockTransferDetailDTO.setFromWarehouse(stockTransfer.getFromWarehouse().toCommonDTO());
        stockTransferDetailDTO.setToWarehouse(stockTransfer.getToWarehouse().toCommonDTO());
        stockTransferDetailDTO.setDescription(stockTransfer.getDescription());
        stockTransferDetailDTO.setTotalQty(stockTransfer.getTotalQty());
        stockTransferDetailDTO.setTotalPrice(stockTransfer.getTotalPrice());
        stockTransferDetailDTO.setLastModifiedDate(stockTransfer.getUpdatedDate());
        stockTransferDetailDTO.setTotalBrutto(stockTransfer.getTotalBrutto());
        stockTransferDetailDTO.setTotalNetto(stockTransfer.getTotalNetto());
        stockTransferDetailDTO.setTotalBlockQty(stockTransfer.getTotalBlockQty());
        if (stockTransfer.getTrip() != null) {
            stockTransferDetailDTO.setTrip(stockTransfer.getTrip().toCommonDTO());
        }
        stockTransfer
                .getProducts()
                .forEach(stockTransferProduct -> {
                    TerminalStockTransferProductDetailDTO stockTransferProductDTO = new TerminalStockTransferProductDetailDTO();
                    stockTransferProductDTO.setStockTransferProductId(stockTransferProduct.getId());
                    stockTransferProductDTO.setId(stockTransferProduct.getFromProductId());
                    stockTransferProductDTO.setName(stockTransferProduct.getFromProduct().getName());
                    stockTransferProductDTO.setBarcode(stockTransferProduct.getFromProduct().getBarcode());
                    stockTransferProductDTO.setVatBarCode(stockTransferProduct.getFromProduct().getVatBarcode());
                    stockTransferProductDTO.setQtyInPackage(stockTransferProduct.getFromProduct().getQtyInPackage());
                    stockTransferProductDTO.setQtyInBox(stockTransferProduct.getFromProduct().getQtyInBox());
                    stockTransferProductDTO.setQtyInPallet(stockTransferProduct.getFromProduct().getQtyInPallet());
                    stockTransferProductDTO.setQty(stockTransferProduct.getQty());
                    stockTransferProductDTO.setUnit(stockTransferProduct.getFromProduct().getUnit().toDTO());
                    stockTransferProductDTO.setAggregationQty(stockTransferProduct.getAggregationQty());
                    stockTransferProductDTO.setMarkQty(stockTransferProduct.getMarkQty());
                    stockTransferProductDTO.setAggregationBlockQty(stockTransferProduct.getAggregationBlockQty());
                    stockTransferProductDTO.setTotalBrutto(stockTransferProduct.getTotalBrutto());
                    stockTransferProductDTO.setTotalNetto(stockTransferProduct.getTotalNetto());
                    stockTransferProductDTO.setTotalBlockQty(stockTransferProduct.getBlockQty());
                    stockTransferProductDTO.setQtyInWarehouse(
                            warehouseProductRepository.getQty(
                                    stockTransfer.getFromWarehouseId(),
                                    stockTransferProduct.getFromProductId(),
                                    stockTransferProduct.getUnitId()
                            )
                    );
                    stockTransferProductDTO.setReservationQty(
                            warehouseProductRepository.getReservationQty(
                                    stockTransfer.getFromWarehouseId(),
                                    stockTransferProduct.getFromProductId(),
                                    stockTransferProduct.getUnitId()
                            )
                    );

                    stockTransferDetailDTO.addProduct(stockTransferProductDTO);
                });
        return stockTransferDetailDTO;
    }

    public Page<StockTransferListDTO> getList(StockTransferFilter filter) {
        ResultList<StockTransfer> resultList = stockTransferRepository.getResultList(filter);
        List<StockTransferListDTO> result = resultList
                .getList()
                .stream()
                .map(stockTransfer -> {
                    StockTransferListDTO stockTransferListDTO = new StockTransferListDTO();
                    stockTransferListDTO.setId(stockTransfer.getId());
                    stockTransferListDTO.setTransferNumber(stockTransfer.getTransferNumber());
                    stockTransferListDTO.setTransferDate(stockTransfer.getTransferDate());
                    stockTransferListDTO.setFromBranch(stockTransfer.getFromBranch().toCommonDTO());
                    stockTransferListDTO.setToBranch(stockTransfer.getToBranch().toCommonDTO());
                    stockTransferListDTO.setFromWarehouse(stockTransfer.getFromWarehouse().toCommonDTO());
                    stockTransferListDTO.setToWarehouse(stockTransfer.getToWarehouse().toCommonDTO());
                    stockTransferListDTO.setStatus(stockTransfer.getStatus());
                    stockTransferListDTO.setTotalQty(stockTransfer.getTotalQty());
                    if (stockTransfer.getTrip() != null) {
                        stockTransferListDTO.setTrip(stockTransfer.getTrip().toCommonDTO());
                    }
                    if (stockTransfer.getStock() != null) {
                        stockTransferListDTO.setStock(stockTransfer.getStock().toCommonDTO());
                    }
                    return stockTransferListDTO;
                })
                .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<TerminalStockTransferListDTO> getListForTerminal(StockTransferFilter filter) {
        warehouseExpeditorRepository.findFirstByExpeditorIdAndWarehouseIdOrderByTransferDateDesc(SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentWarehouseId())
                .ifPresent(warehouseExpeditor -> filter.setToWarehouseId(warehouseExpeditor.getCarId()));
        filter.setExpeditorId(SecurityUtils.getCurrentUserId());
        if (filter.getToWarehouseId() == null) {
            return new PageImpl<>(new ArrayList<>(), filter.getSortedPageable(), 0);
        }
        ResultList<StockTransfer> resultList = stockTransferRepository.getResultList(filter);
        List<TerminalStockTransferListDTO> result = resultList
                .getList()
                .stream()
                .map(stockTransfer -> {
                    TerminalStockTransferListDTO stockTransferListDTO = new TerminalStockTransferListDTO();
                    stockTransferListDTO.setId(stockTransfer.getId());
                    stockTransferListDTO.setTransferNumber(stockTransfer.getTransferNumber());
                    stockTransferListDTO.setTransferDate(stockTransfer.getTransferDate());
                    stockTransferListDTO.setFromBranch(stockTransfer.getFromBranch().toCommonDTO());
                    stockTransferListDTO.setToBranch(stockTransfer.getToBranch().toCommonDTO());
                    stockTransferListDTO.setFromWarehouse(stockTransfer.getFromWarehouse().toCommonDTO());
                    stockTransferListDTO.setToWarehouse(stockTransfer.getToWarehouse().toCommonDTO());
                    stockTransferListDTO.setStatus(stockTransfer.getStatus());
                    stockTransferListDTO.setTotalQty(stockTransfer.getTotalQty());
                    stockTransferListDTO.setTotalPrice(stockTransfer.getTotalPrice());
                    stockTransferListDTO.setLastModifiedDate(stockTransfer.getUpdatedDate());
                    stockTransferListDTO.setTotalBrutto(stockTransfer.getTotalBrutto());
                    stockTransferListDTO.setTotalNetto(stockTransfer.getTotalNetto());
                    stockTransferListDTO.setTotalBlockQty(stockTransfer.getTotalBlockQty());
                    stockTransferListDTO.setOrderCount(stockTransfer.getOrderCount());
                    if (stockTransfer.getTrip() != null) {
                        stockTransferListDTO.setTrip(stockTransfer.getTrip().toCommonDTO());
                    }
                    return stockTransferListDTO;
                })
                .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        StockTransfer stockTransfer = stockTransferRepository
                .findById(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", id));
        this.validateCompany(stockTransfer.getCompanyId());
        if (StockTransferStatus.DRAFT != stockTransfer.getStatus()) {
            throw validationService.badRequestExceptionThrow(
                    localizationService.localize(MessageConstants.YOU_CAN_DELETE_ONLY, new Object[]{StockTransferStatus.DRAFT.getName()})
            ).get();
        }

        List<Long> stockTransferProductIds = stockTransfer
                .getProducts()
                .stream()
                .map(AbstractAuditingEntity::getId)
                .toList();

        stockTransferProductAggregationRepository.deleteAllByStockTransferProductIds(stockTransferProductIds);
        stockTransferProductMarkRepository.deleteAllByStockTransferProductIds(stockTransferProductIds);
        stockTransferProductAggregationBlockRepository.deleteAllByStockTransferProductIds(stockTransferProductIds);

        stockTransfer.setDeleted(true);
        orderRepository.updateStatusAndStockTransferByStockTransferId(stockTransfer.getId(), OrderStatus.APPROVED);
        stockTransferRepository.save(stockTransfer);
    }

    @Transactional
    public CheckStatusResponseDTO updateStatus(Long id, StockTransferUpdateStatusDTO updateStatusDTO, LocalDateTime lastModifiedDate) {
        StockTransferStatus status = updateStatusDTO.getStatus();
        StockTransfer stockTransfer = stockTransferRepository
                .findById(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", id));
        if (!stockTransfer.getUpdatedDate().equals(lastModifiedDate)) {
            return new CheckStatusResponseDTO(false, 1);
        }
/*        if (StockTransferStatus.APPROVED == status && isExpeditor() &&
                warehouseProductRepository.existsByWarehouseIdAndQtyGreaterThan(stockTransfer.getToWarehouseId(), BigDecimal.ZERO)) {
            throw validationService.badRequestExceptionThrow("Имеется товар из предыдущей передачи. ").get();
        }*/
        this.validateCompany(stockTransfer.getCompanyId());
        if (StockTransferStatus.DRAFT == stockTransfer.getStatus()) {
            if (stockTransfer.getToBranchId() == null || stockTransfer.getToWarehouseId() == null) {
                throw validationService.badRequestExceptionThrow("Branch and warehouse are required").get();
            }
        }
/*        if (StockTransferType.C2W == stockTransfer.getStockTransferType()) {
            stockTransfer.getProducts().forEach(stockTransferProduct -> {
                if (stockTransferProduct.getFromProduct().isHasMark() && !Optional.ofNullable(stockTransferProduct.getScannedQty()).orElse(BigDecimal.ZERO).equals(Optional.ofNullable(stockTransferProduct.getQty()).orElse(BigDecimal.ZERO))) {
                    throw validationService.badRequestExceptionThrow("Scanned qty is not equal to product qty").get();
                }
            });
        }*/
        User currentUser = getCurrentUser();
        if (StockTransferStatus.IN_TRANSIT == status) {
            if (currentUser.getBranchId() != null && !stockTransfer.getFromBranchId().equals(currentUser.getBranchId())) {
                throw validationService.badRequestExceptionThrow("Вы не можете отправить передачу").get();
            }
            if (currentUser.getWarehouseId() != null && !stockTransfer.getFromWarehouseId().equals(currentUser.getWarehouseId())) {
                throw validationService.badRequestExceptionThrow("Вы не можете отправить передачу").get();
            }
            if ((stockTransfer.getExpeditorId() != null)) {
                WarehouseExpeditor warehouseExpeditor = new WarehouseExpeditor();
                warehouseExpeditor.setWarehouseId(stockTransfer.getFromWarehouseId());
                warehouseExpeditor.setCarId(stockTransfer.getToWarehouseId());
                warehouseExpeditor.setStockTransferId(stockTransfer.getId());
                warehouseExpeditor.setExpeditorId(stockTransfer.getExpeditorId());
                warehouseExpeditor.setTransferDate(LocalDateTime.now());
                warehouseExpeditorRepository.save(warehouseExpeditor);
            }
            eDocService.generateDoc(new EDocsGenerateDTO(stockTransfer.getId(), EntityTypeEnum.STOCK_TRANSFER, stockTransfer.getCompanyId()));
        }
        if (StockTransferStatus.APPROVED == status) {
            Long branchId = currentUser.getBranchId();
            Long warehouseId = currentUser.getWarehouseId();
            if (isExpeditor() && warehouseId != null) {
                WarehouseExpeditor warehouseExpeditor = warehouseExpeditorRepository.findFirstByExpeditorIdAndWarehouseIdOrderByTransferDateDesc(currentUser.getId(), warehouseId).orElse(null);
                if (warehouseExpeditor != null) {
                    warehouseId = warehouseExpeditor.getCarId();
                }
            }
            if (branchId != null && !stockTransfer.getToBranchId().equals(branchId)) {
                throw validationService.badRequestExceptionThrow("Вы не можете подтвердить передачу").get();
            }
            if (warehouseId != null && !stockTransfer.getToWarehouseId().equals(warehouseId)) {
                throw validationService.badRequestExceptionThrow("Вы не можете подтвердить передачу").get();
            }
            notificationProducer.send(TemplateType.COMPLETE_STOCK_TRANSFER, getCurrentCompanyId(),
                    Map.of(TemplateTypeAlias.TRANSACTION_NUMBER, stockTransfer.getTransferNumber(), TemplateTypeAlias.CREATOR, getCurrentUserId()));
        }
        if (StockTransferStatus.APPROVED == status) {
            if (StockTransferStatus.IN_TRANSIT != stockTransfer.getStatus()) {
                throw validationService.badRequestExceptionThrow("Вы можете одобрить передачу со статусом транзит").get();
            }
            warehouseProductService.out(stockTransfer);
            //Create Stock in
            stockTransfer.setStockId(stockInService.createByStockTransfer(stockTransfer));
            if ((stockTransfer.getExpeditorId() != null)) {
                for (Order order : orderRepository.getAllByStockTransferId(stockTransfer.getId())) {
                    order.getProducts().forEach(orderProduct -> {
                        WarehouseProduct warehouseProduct = warehouseProductRepository
                                .findByWarehouseIdAndProductIdAndUnitId(order.getWarehouseId(), orderProduct.getProductId(), orderProduct.getUnitId())
                                .orElse(null);
                        if (warehouseProduct != null) {
                            warehouseProduct.subtractReservationQty(Optional.ofNullable(orderProduct.getQty()).orElse(BigDecimal.ZERO));
                            warehouseProductRepository.saveAndFlush(warehouseProduct);
                        }
                    });
                    order.setReservationReleased(true);
                    orderRepository.save(order);
                    orderRepository.updateStatusByStockTransferId(stockTransfer.getId(), OrderStatus.IN_DELIVERY);
                }
            }
            stockTransfer.setCompletedDate(LocalDateTime.now());
        } else if (StockTransferStatus.REJECTED == status) {
            if (
                    !(
                            StockTransferStatus.IN_TRANSIT.equals(stockTransfer.getStatus()) ||
                                    StockTransferStatus.APPROVED.equals(stockTransfer.getStatus())
                    )
            ) {
                throw validationService.badRequestExceptionThrow("Вы можете отклонять передачу со статусом одобрено или отправлено").get();
            }

            if (StockTransferStatus.APPROVED == stockTransfer.getStatus()) {
                warehouseProductService.in(stockTransfer);
            }
            stockTransfer.setReason(updateStatusDTO.getReason());
            stockTransfer.setOtherReason(updateStatusDTO.getOtherReason());
            if ((stockTransfer.getExpeditorId() != null)) {
                orderRepository.updateStatusAndStockTransferByStockTransferId(stockTransfer.getId(), OrderStatus.APPROVED);
            }
        }
        stockTransfer.setStatus(status);
        if (StockTransferStatus.APPROVED == stockTransfer.getStatus() && stockTransfer.getTrip() != null) {
            tripService.updateStatus(stockTransfer.getTrip(), TripStatus.COMPLETED);
        }
        stockTransferRepository.save(stockTransfer);
        return new CheckStatusResponseDTO(true, 0);
    }

    public List<CisInfoResponse> check(BatchValidationDTO validationDTO) {

        Map<String, CisInfoResponse> resultMap = new LinkedHashMap<>();
        CompanySettings xtraceSettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.XTRACE_ENABLED).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanySettings.class.getSimpleName(), "companyId", getCurrentCompanyId()));


        if (!CollectionUtils.isEmpty(validationDTO.getAggregationCodes())) {
            List<String> clearAggregationCodes = validationDTO.getAggregationCodes().stream().map(code -> {
                code = MarkUtils.clearAggregationCode(code);
                CisInfo cisInfo = new CisInfo();
                cisInfo.setRequestedCis(code);
                cisInfo.setOriginalCode(code);
                resultMap.put(code, new CisInfoResponse(cisInfo));
                return code;
            }).collect(Collectors.toList());
            if (xtraceSettings.isEnabled()) {
                cisInfoService.batchCheckAggregation(validationDTO, resultMap, clearAggregationCodes);
            }
            List<String> aggregationCodesForUsedCheck = new ArrayList<>(resultMap.values().stream().filter(cisInfoResponse -> TuronMarkStatus.isSuccess(cisInfoResponse.getCisInfo().getStatus()))
                    .map(cisInfoResponse -> Optional.ofNullable(cisInfoResponse.getCisInfo().getRequestedCis()).orElseGet(() -> cisInfoResponse.getCisInfo().getOriginalCode())).toList());
            if (!CollectionUtils.isEmpty(aggregationCodesForUsedCheck)) {
                List<StockTransferProductAggregation> stockTransferProductAggregations = stockTransferProductAggregationRepository.getByCodesAndCompanyIdAndStatusNot(
                        aggregationCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockTransferStatus.REJECTED
                );
                if (!CollectionUtils.isEmpty(stockTransferProductAggregations)) {
                    stockTransferProductAggregations.forEach(stockTransferProductAggregation -> {
                        CisInfoResponse cisInfoResponse = resultMap.get(stockTransferProductAggregation.getOriginalCode());
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.get(stockTransferProductAggregation.getCode());
                        }
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.values().stream().filter(tempCisInfo -> tempCisInfo.getCisInfo().getCode().equals(stockTransferProductAggregation.getCode())).findFirst().orElse(null);
                        }
                        if (cisInfoResponse != null) {
                            CisInfo cisInfo = cisInfoResponse.getCisInfo();
                            cisInfo.setErrorMessage(
                                    "Код уже использован в передача " + stockTransferProductAggregation.getStockTransferProduct().getStockTransfer().getTransferNumber()
                            );
                            cisInfo.setStatus(TuronMarkStatus.ERROR);
                        }
                    });
                }
            }
        }

        if (!CollectionUtils.isEmpty(validationDTO.getMarkCodes())) {
            List<String> markCodes = new ArrayList<>(validationDTO.getMarkCodes());
            validationDTO.getMarkCodes().forEach(markCode -> {
                CisInfo cisInfo = new CisInfo();
                cisInfo.setRequestedCis(MarkUtils.clearOriginalCode(markCode, validationDTO.getProductType()));
                cisInfo.setOriginalCode(markCode);
                if (StringUtils.isNotEmpty(validationDTO.getBoxBarcode())) {
                    if (!markCode.contains(validationDTO.getBoxBarcode())) {
                        if (StringUtils.isNotEmpty(validationDTO.getBarcode())) {
                            if (!markCode.contains(validationDTO.getBarcode())) {
                                String errorMessage = localizationService.localize(MessageConstants.SELECTED_BARCODE_NOT_MATCH, new Object[]{validationDTO.getBarcode(), MarkUtils.getBarcodeFromMarkCode(markCode)});
                                cisInfo.setStatus(TuronMarkStatus.ERROR);
                                cisInfo.setErrorMessage(errorMessage);
                                cisInfo.setGtin(validationDTO.getBarcode());
                                markCodes.remove(markCode);
                            }
                        }
                    }
                } else if (StringUtils.isNotEmpty(validationDTO.getBlockBarcode())) {
                    if (!markCode.contains(validationDTO.getBlockBarcode())) {
                        if (StringUtils.isNotEmpty(validationDTO.getBarcode())) {
                            if (!markCode.contains(validationDTO.getBarcode())) {
                                String errorMessage = localizationService.localize(MessageConstants.SELECTED_BARCODE_NOT_MATCH, new Object[]{validationDTO.getBarcode(), MarkUtils.getBarcodeFromMarkCode(markCode)});
                                cisInfo.setStatus(TuronMarkStatus.ERROR);
                                cisInfo.setErrorMessage(errorMessage);
                                cisInfo.setGtin(validationDTO.getBarcode());
                                markCodes.remove(markCode);
                            }
                        }
                    }
                } else {
                    if (StringUtils.isNotEmpty(validationDTO.getBarcode())) {
                        if (!markCode.contains(validationDTO.getBarcode())) {
                            String errorMessage = localizationService.localize(MessageConstants.SELECTED_BARCODE_NOT_MATCH, new Object[]{validationDTO.getBarcode(), MarkUtils.getBarcodeFromMarkCode(markCode)});
                            cisInfo.setStatus(TuronMarkStatus.ERROR);
                            cisInfo.setErrorMessage(errorMessage);
                            cisInfo.setGtin(validationDTO.getBarcode());
                            markCodes.remove(markCode);
                        }
                    }
                }
                resultMap.put(cisInfo.getOriginalCode(), new CisInfoResponse(cisInfo));
            });
            if (xtraceSettings.isEnabled()) {
                cisInfoService.batchCheckMark(validationDTO, resultMap, markCodes);
            }
            List<String> markCodesForUsedCheck = new ArrayList<>(resultMap.values().stream().filter(cisInfoResponse -> TuronMarkStatus.isSuccess(cisInfoResponse.getCisInfo().getStatus()))
                    .map(cisInfoResponse -> Optional.ofNullable(cisInfoResponse.getCisInfo().getRequestedCis()).orElseGet(() -> cisInfoResponse.getCisInfo().getOriginalCode())).toList());
            if (!CollectionUtils.isEmpty(markCodesForUsedCheck)) {
                List<StockTransferProductMark> stockTransferProductMarks = stockTransferProductMarkRepository.getByCodesAndCompanyIdAndStatusNot(
                        markCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockTransferStatus.REJECTED
                );
                if (!CollectionUtils.isEmpty(stockTransferProductMarks)) {
                    stockTransferProductMarks.forEach(stockTransferProductAggregation -> {
                        CisInfoResponse cisInfoResponse = resultMap.get(stockTransferProductAggregation.getOriginalCode());
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.get(stockTransferProductAggregation.getCode());
                        }
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.values().stream().filter(tempCisInfo -> tempCisInfo.getCisInfo().getCode().equals(stockTransferProductAggregation.getCode())).findFirst().orElse(null);
                        }
                        if (cisInfoResponse != null) {
                            CisInfo cisInfo = cisInfoResponse.getCisInfo();
                            cisInfo.setErrorMessage(
                                    "Код уже использован в передача " + stockTransferProductAggregation.getStockTransferProduct().getStockTransfer().getTransferNumber()
                            );
                            cisInfo.setStatus(TuronMarkStatus.ERROR);
                        }
                        markCodes.remove(stockTransferProductAggregation.getOriginalCode());
                    });
                }

            }
        }

        //Aggregation block
        if (!CollectionUtils.isEmpty(validationDTO.getAggregationBlockCodes())) {
            List<String> aggregationBlockCodes = new ArrayList<>(validationDTO.getAggregationBlockCodes());
            validationDTO.getAggregationBlockCodes().forEach(aggregationBlockCode -> {
                CisInfo cisInfo = new CisInfo();
                cisInfo.setRequestedCis(MarkUtils.clearOriginalCode(aggregationBlockCode, validationDTO.getProductType()));
                cisInfo.setOriginalCode(aggregationBlockCode);
                resultMap.put(aggregationBlockCode, new CisInfoResponse(cisInfo));
            });
            if (xtraceSettings.isEnabled()) {
                cisInfoService.batchCheckAggregationBlock(validationDTO, resultMap, aggregationBlockCodes);
            }
            List<String> aggregationBlockCodesForUsedCheck = new ArrayList<>(resultMap.values().stream().filter(cisInfoResponse -> TuronMarkStatus.isSuccess(cisInfoResponse.getCisInfo().getStatus()))
                    .map(cisInfoResponse -> Optional.ofNullable(cisInfoResponse.getCisInfo().getRequestedCis()).orElseGet(() -> cisInfoResponse.getCisInfo().getOriginalCode())).toList());
            if (!aggregationBlockCodesForUsedCheck.isEmpty()) {
                List<StockTransferProductAggregationBlock> stockTransferProductAggregationBlocks = stockTransferProductAggregationBlockRepository.getByCodesAndCompanyIdAndStatusNot(
                        aggregationBlockCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockTransferStatus.REJECTED
                );
                if (!CollectionUtils.isEmpty(stockTransferProductAggregationBlocks)) {
                    stockTransferProductAggregationBlocks.forEach(stockTransferProductAggregation -> {
                        CisInfoResponse cisInfoResponse = resultMap.get(stockTransferProductAggregation.getOriginalCode());
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.get(stockTransferProductAggregation.getCode());
                        }
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.values().stream().filter(tempCisInfo -> tempCisInfo.getCisInfo().getCode().equals(stockTransferProductAggregation.getCode())).findFirst().orElse(null);
                        }
                        if (cisInfoResponse != null) {
                            CisInfo cisInfo = cisInfoResponse.getCisInfo();
                            cisInfo.setErrorMessage(
                                    "Код уже использован в передача " + stockTransferProductAggregation.getStockTransferProduct().getStockTransfer().getTransferNumber()
                            );
                            cisInfo.setStatus(TuronMarkStatus.ERROR);
                        }
                        aggregationBlockCodes.remove(stockTransferProductAggregation.getOriginalCode());
                    });
                }
            }
        }
        return Arrays.asList(resultMap.values().toArray(new CisInfoResponse[0]));
    }

    @Transactional
    public Long createFromOrder(StockTransferCreateFromOrderDTO createFromOrderDTO) {
        if (createFromOrderDTO.getBranchId() == null) {
            throw validationService.badRequestExceptionThrow("Branch is required").get();
        }
        if (createFromOrderDTO.getWarehouseId() == null) {
            throw validationService.badRequestExceptionThrow("Warehouse is required").get();
        }
        if (createFromOrderDTO.getExpeditorId() == null) {
            throw validationService.badRequestExceptionThrow("Expeditor is required").get();
        }
        if (orderRepository.countExpeditors(createFromOrderDTO.getOrderIds()) > 1) {
            throw validationService.badRequestExceptionThrow("More than one expeditor selected").get();
        }
        if (CollectionUtils.isEmpty(createFromOrderDTO.getOrderIds())) {
            throw validationService.badRequestExceptionThrow("Order list is empty").get();
        }
        StockTransferDTO stockTransferDTO = new StockTransferDTO();

        BigDecimal totalBrutto = BigDecimal.ZERO;

        BigDecimal totalNetto = BigDecimal.ZERO;

        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        stockTransferDTO.setFromBranchId(createFromOrderDTO.getBranchId());
        stockTransferDTO.setFromWarehouseId(createFromOrderDTO.getWarehouseId());
        stockTransferDTO.setToBranchId(createFromOrderDTO.getBranchId());
        stockTransferDTO.setTransferDate(LocalDateTime.now());
        stockTransferDTO.setExpeditorId(createFromOrderDTO.getExpeditorId());
        stockTransferDTO.setTripId(createFromOrderDTO.getTripId());

        Map<Long, StockTransferProductDTO> map = new HashMap<>();
        for (Order order : orderRepository.findAllById(createFromOrderDTO.getOrderIds())) {
            for (OrderProduct orderProduct : order.getProducts()) {
                StockTransferProductDTO stockTransferProductDTO;
                if (map.containsKey(orderProduct.getProductId())) {
                    stockTransferProductDTO = map.get(orderProduct.getProductId());
                } else {
                    stockTransferProductDTO = new StockTransferProductDTO();
                    stockTransferProductDTO.setId(orderProduct.getProductId());
                    if (orderProduct.getOrder() != null) {
                        stockTransferProductDTO.setOrderId(orderProduct.getOrder().getId());
                    }
                    stockTransferProductDTO.setPrice(orderProduct.getPrice());
                    map.put(orderProduct.getProductId(), stockTransferProductDTO);
                    stockTransferDTO.addProduct(stockTransferProductDTO);
                }
                stockTransferProductDTO.setQty(Optional.ofNullable(stockTransferProductDTO.getQty()).orElse(BigDecimal.ZERO).add(orderProduct.getQty()));
                stockTransferProductDTO.setTotalPrice(Optional.ofNullable(stockTransferProductDTO.getTotalPrice()).orElse(BigDecimal.ZERO).add(orderProduct.getTotalPrice()));
                stockTransferProductDTO.setTotalBrutto(Optional.ofNullable(stockTransferProductDTO.getTotalBrutto()).orElse(BigDecimal.ZERO).add(Optional.ofNullable(orderProduct.getTotalBrutto()).orElse(BigDecimal.ZERO)));
                stockTransferProductDTO.setTotalNetto(Optional.ofNullable(stockTransferProductDTO.getTotalNetto()).orElse(BigDecimal.ZERO).add(Optional.ofNullable(orderProduct.getTotalNetto()).orElse(BigDecimal.ZERO)));
                totalNetto = totalNetto.add(Optional.ofNullable(orderProduct.getTotalBrutto()).orElse(BigDecimal.ZERO));
                totalBrutto = totalBrutto.add(Optional.ofNullable(orderProduct.getTotalNetto()).orElse(BigDecimal.ZERO));
                totalQty = totalQty.add(Optional.ofNullable(orderProduct.getQty()).orElse(BigDecimal.ZERO));
                totalPrice = totalPrice.add(Optional.ofNullable(orderProduct.getTotalPrice()).orElse(BigDecimal.ZERO));
            }
        }

        stockTransferDTO.setStockTransferType(StockTransferType.W2C.getCode());
        stockTransferDTO.setToWarehouseId(createFromOrderDTO.getCarId());
        stockTransferDTO.setTotalBrutto(totalBrutto);
        stockTransferDTO.setTotalNetto(totalNetto);
        stockTransferDTO.setTotalQty(totalQty);
        stockTransferDTO.setTotalPrice(totalPrice);
        stockTransferDTO.setOrderCount(createFromOrderDTO.getOrderIds().size());
        Long stockTransferId = this.create(stockTransferDTO);
        orderRepository.setStockTransfer(createFromOrderDTO.getOrderIds(), stockTransferId);
        orderRepository.updateStatus(createFromOrderDTO.getOrderIds(), OrderStatus.LOADING);
        return stockTransferId;
    }

    @Transactional
    public Long createFromWarehouseProduct(StockTransferCreateFromWarehouseProductDTO transferCreateFromWarehouseProductDTO) {
        if (transferCreateFromWarehouseProductDTO.getFromBranchId() == null) {
            throw validationService.badRequestExceptionThrow("From branch is required").get();
        }
        if (transferCreateFromWarehouseProductDTO.getToBranchId() == null) {
            throw validationService.badRequestExceptionThrow("To branch is required").get();
        }
        if (transferCreateFromWarehouseProductDTO.getFromWarehouseId() == null) {
            throw validationService.badRequestExceptionThrow("From warehouse is required").get();
        }
        if (transferCreateFromWarehouseProductDTO.getToWarehouseId() == null) {
            throw validationService.badRequestExceptionThrow("To warehouse is required").get();
        }
        BalanceReportFilter filter = new BalanceReportFilter();
        filter.setWarehouseId(transferCreateFromWarehouseProductDTO.getFromWarehouseId());
        filter.setBranchId(transferCreateFromWarehouseProductDTO.getFromBranchId());
        filter.setCompanyId(getCurrentCompanyId());
        filter.setSize(1000);
        filter.setPage(0);
        List<WarehouseProductReportDTO> warehouseProducts = warehouseProductService.getReport(filter).getContent();
        if (CollectionUtils.isEmpty(warehouseProducts)) {
            throw validationService.badRequestExceptionThrow("Empty list").get();
        }
        StockTransferDTO stockTransferDTO = new StockTransferDTO();
        stockTransferDTO.setFromBranchId(transferCreateFromWarehouseProductDTO.getFromBranchId());
        stockTransferDTO.setFromWarehouseId(transferCreateFromWarehouseProductDTO.getFromWarehouseId());
        stockTransferDTO.setToBranchId(transferCreateFromWarehouseProductDTO.getToBranchId());
        stockTransferDTO.setToWarehouseId(transferCreateFromWarehouseProductDTO.getToWarehouseId());
        stockTransferDTO.setExpeditorId(transferCreateFromWarehouseProductDTO.getExpeditorId());
        stockTransferDTO.setStockTransferType(StockTransferType.C2W.getCode());
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalBrutto = BigDecimal.ZERO;
        BigDecimal totalNetto = BigDecimal.ZERO;
        for (WarehouseProductReportDTO warehouseProduct : warehouseProducts) {
            StockTransferProductDTO productDTO = new StockTransferProductDTO();
            productDTO.setId(warehouseProduct.getProduct().getId());
            productDTO.setQty(warehouseProduct.getQty());
            productDTO.setPrice(warehouseProduct.getPrice());
            productDTO.setTotalPrice(warehouseProduct.getTotalPrice());
            productDTO.setTotalNetto(Optional.ofNullable(warehouseProduct.getNetto()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(warehouseProduct.getQty()).orElse(BigDecimal.ZERO)));
            productDTO.setTotalBrutto(Optional.ofNullable(warehouseProduct.getBrutto()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(warehouseProduct.getQty()).orElse(BigDecimal.ZERO)));
            totalPrice = totalPrice.add(Optional.ofNullable(productDTO.getTotalPrice()).orElse(BigDecimal.ZERO));
            totalQty = totalQty.add(Optional.ofNullable(productDTO.getQty()).orElse(BigDecimal.ZERO));
            totalBrutto = totalBrutto.add(Optional.ofNullable(productDTO.getTotalBrutto()).orElse(BigDecimal.ZERO));
            totalNetto = totalNetto.add(Optional.ofNullable(productDTO.getTotalNetto()).orElse(BigDecimal.ZERO));
            stockTransferDTO.addProduct(productDTO);
        }
        stockTransferDTO.setTotalPrice(totalPrice);
        stockTransferDTO.setTotalQty(totalQty);
        stockTransferDTO.setTotalBrutto(totalBrutto);
        stockTransferDTO.setTotalNetto(totalNetto);
        return this.create(stockTransferDTO);
    }

    public List<WarehouseProductReportDTO> getCurrentStock(BalanceReportFilter filter) {
        filter.setPage(0);
        filter.setSize(1000);
        filter.setWarehouseId(Optional.ofNullable(filter.getWarehouseId()).orElse(SecurityUtils.getCurrentWarehouseId()));
        filter.setBranchId(Optional.ofNullable(filter.getBranchId()).orElse(SecurityUtils.getCurrentBranchId()));
        if (filter.getWarehouseId() == null || filter.getBranchId() == null) {
            return Collections.emptyList();
        }
        return warehouseProductService.getReportList(filter);
    }

    public Map<CodeType, List<CheckedCodeDTO>> getAggregationCodes(Long stockTransferProductId) {
        Map<CodeType, List<CheckedCodeDTO>> result = new HashMap<>();

        List<CheckedCodeDTO> aggregationCodes = stockTransferProductAggregationRepository.findAllByStockTransferProductIdAndDeletedIsFalse(stockTransferProductId).stream().map(StockTransferProductAggregation::toCheckDTO).toList();

        result.put(CodeType.AGGREGATION, aggregationCodes);

        List<CheckedCodeDTO> markCodes = stockTransferProductMarkRepository.findAllByStockTransferProductIdAndDeletedIsFalse(stockTransferProductId).stream().map(StockTransferProductMark::toCheckDTO).toList();

        result.put(CodeType.MARK, markCodes);

        List<CheckedCodeDTO> aggregationBlockCodes = stockTransferProductAggregationBlockRepository.findAllByStockTransferProductIdAndDeletedIsFalse(stockTransferProductId).stream().map(StockTransferProductAggregationBlock::toCheckDTO).toList();

        result.put(CodeType.BLOCK, aggregationBlockCodes);

        return result;
    }

    public List<CommonDTO> lookUp(StockTransferFilter filter) {
        return stockTransferRepository.lookUp(filter).stream().map(StockTransfer::toCommonDTO).toList();
    }

    public ExpeditorStockTransferDetailDTO getDetailForExpeditor(Long id) {
        return stockTransferRepository.getDetail(id).map(stockTransfer -> {
            ExpeditorStockTransferDetailDTO expeditorStockTransferDetailDTO = new ExpeditorStockTransferDetailDTO();
            expeditorStockTransferDetailDTO.setId(stockTransfer.getId());
            expeditorStockTransferDetailDTO.setStatus(stockTransfer.getStatus());
            expeditorStockTransferDetailDTO.setTransferDate(stockTransfer.getTransferDate());
            expeditorStockTransferDetailDTO.setTruckRegNo(stockTransfer.getToWarehouse().getTruckRegNo());
            expeditorStockTransferDetailDTO.setTruckModel(stockTransfer.getToWarehouse().getTruckModel());
            expeditorStockTransferDetailDTO.setParentTruck(Optional.of(stockTransfer.getToWarehouse().getParent()).map(Warehouse::toCommonDTO).orElse(null));
            expeditorStockTransferDetailDTO.setTrailerRegNo(stockTransfer.getToWarehouse().getTrailerRegNo());
            expeditorStockTransferDetailDTO.setTrailerModel(stockTransfer.getToWarehouse().getTrailerModel());
            expeditorStockTransferDetailDTO.setToWarehouse(stockTransfer.getToWarehouse().toCommonDTO());
            expeditorStockTransferDetailDTO.setTransferNumber(stockTransfer.getTransferNumber());
            if (stockTransfer.getTrip() != null) {
                expeditorStockTransferDetailDTO.setTrip(stockTransfer.getTrip().toCommonDTO());
            }
            for (StockTransferProduct stockTransferProduct : stockTransfer.getProducts()) {
                ExpeditorStockTransferProductDetailDTO stockTransferProductDetailDTO = new ExpeditorStockTransferProductDetailDTO();
                stockTransferProductDetailDTO.setStockTransferProductId(stockTransferProduct.getId());
                stockTransferProductDetailDTO.setId(stockTransferProduct.getFromProductId());
                stockTransferProductDetailDTO.setName(stockTransferProduct.getFromProduct().getName());
                stockTransferProductDetailDTO.setBarcode(stockTransferProduct.getFromProduct().getBarcode());
                stockTransferProductDetailDTO.setVatBarCode(stockTransferProduct.getFromProduct().getVatBarcode());
                stockTransferProductDetailDTO.setQtyInPackage(stockTransferProduct.getFromProduct().getQtyInPackage());
                stockTransferProductDetailDTO.setQtyInBox(stockTransferProduct.getFromProduct().getQtyInBox());
                stockTransferProductDetailDTO.setQtyInPallet(stockTransferProduct.getFromProduct().getQtyInPallet());
                stockTransferProductDetailDTO.setCapacity(stockTransferProduct.getFromProduct().getCapacity());
                stockTransferProductDetailDTO.setHasMark(stockTransferProduct.getFromProduct().isHasMark());
                stockTransferProductDetailDTO.setHasMarking(stockTransferProduct.isHasMarking());
                stockTransferProductDetailDTO.setExciseRate(stockTransferProduct.getFromProduct().getExciseRate());
                stockTransferProductDetailDTO.setExciseSum(stockTransferProduct.getFromProduct().getExciseSum());
                stockTransferProductDetailDTO.setPackageName(stockTransferProduct.getFromProduct().getPackageName());
                stockTransferProductDetailDTO.setPackageCode(stockTransferProduct.getFromProduct().getPackageCode());
                stockTransferProductDetailDTO.setVat(stockTransferProduct.getFromProduct().getVat());
                stockTransferProductDetailDTO.setCompanyCommittent(Optional.ofNullable(stockTransferProduct.getFromProduct().getCompanyCommittent()).map(CompanyCommittent::toLookUpDTO).orElse(null));
                stockTransferProductDetailDTO.setProductType(stockTransferProduct.getFromProduct().getProductType());
                stockTransferProductDetailDTO.setQty(stockTransferProduct.getQty());
                stockTransferProductDetailDTO.setBlockQty(stockTransferProduct.getBlockQty());
                stockTransferProductDetailDTO.setUnit(stockTransferProduct.getFromProduct().getUnit().toDTO());
                if (stockTransferProduct.getOrder() != null) {
                    stockTransferProductDetailDTO.setOrder(stockTransferProduct.getOrder().toCommonDTO());
                    stockTransferProduct.getOrder().getProducts().stream().filter(orderProduct -> Objects.equals(orderProduct.getProductId(), stockTransferProduct.getFromProductId())).forEach(orderProduct -> {
                        stockTransferProductDetailDTO.setSoldQty(orderProductRepository.getSoldQty(stockTransferProduct.getOrder().getId(), stockTransferProduct.getFromProductId()));
                        stockTransferProductDetailDTO.setPackageQuantities(packageQuantityService.getByCompositeId(orderProduct));
                    });
                }
                BigDecimal soldQty = stockTransferProductDetailDTO.getSoldQty();
                BigDecimal blockQty = stockTransferProductDetailDTO.getQtyInPackage();
                if (soldQty != null && blockQty != null && blockQty.compareTo(BigDecimal.ZERO) != 0) {
                    stockTransferProductDetailDTO.setSoldQtyInBlock(soldQty.divide(blockQty, 2, RoundingMode.HALF_UP));
                } else {
                    stockTransferProductDetailDTO.setSoldQtyInBlock(BigDecimal.ZERO);
                }
                stockTransferProductDetailDTO.setAggregationQty(stockTransferProduct.getAggregationQty());
                stockTransferProductDetailDTO.setMarkQty(stockTransferProduct.getMarkQty());
                stockTransferProductDetailDTO.setAggregationBlockQty(stockTransferProduct.getAggregationBlockQty());
                stockTransferProductDetailDTO.setTotalBrutto(stockTransferProduct.getTotalBrutto());
                stockTransferProductDetailDTO.setNetto(stockTransferProduct.getFromProduct().getWeightBrutto());
                stockTransferProductDetailDTO.setBrutto(stockTransferProduct.getFromProduct().getWeightNetto());
                stockTransferProductDetailDTO.setTotalNetto(stockTransferProduct.getTotalNetto());
                stockTransferProductDetailDTO.setPrice(stockTransferProduct.getPrice());
                stockTransferProductDetailDTO.setCatalogPrice(stockTransferProduct.getFromProduct().getPrice());
                stockTransferProductDetailDTO.setTotalPrice(stockTransferProduct.getTotalPrice());
                stockTransferProductDetailDTO.setQty(stockTransferProduct.getQty());
                if (stockTransferProduct.getFromProduct().getQtyInPackage() != null) {
                    int qtyInPackage = Optional.ofNullable(stockTransferProduct.getQty()).orElse(BigDecimal.ZERO).divide(stockTransferProduct.getFromProduct().getQtyInPackage(), RoundingMode.FLOOR).intValue();
                    stockTransferProductDetailDTO.setTotalQtyInPackage(qtyInPackage);
                }
                stockTransferProductDetailDTO.setQtyInWarehouse(
                        warehouseProductRepository.getQty(
                                stockTransfer.getToWarehouseId(),
                                stockTransferProduct.getToProductId(),
                                stockTransferProduct.getUnitId()
                        )
                );
                stockTransferProductDetailDTO.setReservationQty(
                        warehouseProductRepository.getReservationQty(
                                stockTransfer.getToWarehouseId(),
                                stockTransferProduct.getToProductId(),
                                stockTransferProduct.getUnitId()
                        )
                );
                if (stockTransferProduct.getProductSerialId() != null) {
                    stockTransferProductDetailDTO.setProductSerial(stockTransferProduct.getProductSerial().toLookUpDTO());
                    stockTransferProductDetailDTO.setQtyInWarehouseBySerial(productSerialWarehouseRepository.getQty(stockTransfer.getFromWarehouseId(), stockTransferProduct.getProductSerialId()));
                }

                stockTransferProductDetailDTO.setBoxBarcode(stockTransferProduct.getFromProduct().getBoxBarcode());
                stockTransferProductDetailDTO.setBlockBarcode(stockTransferProduct.getFromProduct().getBlockBarcode());
                if (stockTransferProduct.getFromProduct().getParent() != null) {
                    stockTransferProductDetailDTO.setParent(stockTransferProduct.getFromProduct().getParent().toProductCommonDTO());
                    if (StringUtils.isEmpty(stockTransferProductDetailDTO.getBlockBarcode())) {
                        stockTransferProductDetailDTO.setBlockBarcode(stockTransferProduct.getFromProduct().getParent().getBarcode());
                    }
                }
                stockTransferProductDetailDTO.setScannedQty(stockTransferProduct.getScannedQty());
                stockTransferProductDetailDTO.setBlockProducts(stockTransferProduct.getFromProduct().getBlockProducts());
                warehouseProductRepository.findFirstByProductIdAndDeletedIsFalse(stockTransferProduct.getFromProductId()).ifPresent(warehouseProduct -> {
                    stockTransferProductDetailDTO.setBranch(warehouseProduct.getWarehouse().getBranch().toCommonDTO());
                    stockTransferProductDetailDTO.setWarehouse(warehouseProduct.getWarehouse().toCommonDTO());
                    stockTransferProductDetailDTO.setParentWarehouse(Optional.ofNullable(warehouseProduct.getWarehouse().getParent()).map(Warehouse::toCommonDTO).orElse(null));
                });
                expeditorStockTransferDetailDTO.addProduct(stockTransferProductDetailDTO);
            }
            return expeditorStockTransferDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", id));
    }

    public ExpeditorStockTransferStatisticsDTO getStatisticsForExpeditor(Long id) {
        ExpeditorStockTransferStatisticsDTO expeditorStockTransferStatisticsDTO = new ExpeditorStockTransferStatisticsDTO();
        List<Long> orderIds = orderRepository.getIdsByStockTransferIdAndStatusIn(id, List.of(OrderStatus.COMPLETED, OrderStatus.IN_DELIVERY, OrderStatus.LOADING));
        expeditorStockTransferStatisticsDTO.setOrderTotal((long) orderIds.size());
        List<Long> stockOutIds = stockOutRepository.getIdsByOrderIdIn(orderIds);
        expeditorStockTransferStatisticsDTO.setStockOutTotal((long) stockOutIds.size());
        expeditorStockTransferStatisticsDTO.setInvoiceTotal(stockOutRepository.getInvoiceCountByStockIds(stockOutIds));
        expeditorStockTransferStatisticsDTO.setReceiptTotal(stockOutRepository.getReceiptCountByStockIds(stockOutIds));
        return expeditorStockTransferStatisticsDTO;
    }

    private void assignTripToStock(StockTransfer stockTransfer, Set<Long> orderIds, Long tripId) {
        Set<Long> orderTripIds = orderRepository.getTripIdsByIds(new ArrayList<>(orderIds)).stream().filter(Objects::nonNull).collect(Collectors.toSet());

        Long finalTripId;
        if (orderTripIds.size() > 1) {
            throw validationService.badRequestExceptionThrow("Выбранные заказы принадлежат разным рейсам. Невозможно присвоить к передаче.").get();
        }

        if (orderTripIds.size() == 1 && tripId != null) {
            Long orderTripId = orderTripIds.iterator().next();
            if (orderTripId != null && !orderTripId.equals(tripId)) {
                throw validationService.badRequestExceptionThrow("Рейс в заказах и рейс в передаче не совпадают.").get();
            }
        }

        if (orderTripIds.size() == 1) {
            finalTripId = orderTripIds.iterator().next();
        } else if (tripId != null) {
            finalTripId = tripId;
        } else {
            return;
        }

        Trip trip = tripRepository.findById(finalTripId).orElseThrow(validationService.entityNotFoundExceptionThrow(Trip.class.getSimpleName(), "id", finalTripId));
        stockTransfer.setTripId(trip.getId());
        stockTransfer.setTrip(trip);
        stockTransferRepository.saveAndFlush(stockTransfer);
        orderRepository.updateTrip(orderIds, finalTripId);

        if (stockTransfer.getTrip() != null) {
            tripService.updateStatus(stockTransfer.getTrip(), TripStatus.IN_PROGRESS);
        }
    }



    public StockTransferProductAggregationsDTO getProductAggregations(Long id, Long productId) {
        StockTransferProductAggregationsDTO stockTransferProductAggregationsDTO = new StockTransferProductAggregationsDTO();
        stockTransferProductRepository.findByFromProductIdAndStockTransferId(productId, id).ifPresentOrElse(stockTransferProduct -> {
            stockTransferProductAggregationsDTO.setTransferCount(stockTransferProduct.getQty());
            stockTransferProductAggregationsDTO.getTransferAggregations().addAll(
                    Stream.of(
                                    stockTransferProduct.getStockTransferProductAggregations().stream().map(StockTransferProductAggregation::getCode),
                                    stockTransferProduct.getStockTransferProductAggregationBlocks().stream().map(StockTransferProductAggregationBlock::getCode),
                                    stockTransferProduct.getStockTransferProductMarks().stream().map(StockTransferProductMark::getCode)
                            )
                            .flatMap(Function.identity())
                            .toList());
        }, () -> {
            throw validationService.entityNotFoundExceptionThrow(StockTransferProduct.class.getSimpleName(), "id", productId).get();
        });
        stockProductRepository.findByProductIdAndStockTransferId(productId, id).ifPresent(stockProduct -> {
            stockTransferProductAggregationsDTO.setSaleCount(stockProduct.getQty());
            stockTransferProductAggregationsDTO.getSaleAggregations().addAll(
                    Stream.of(
                                    stockProduct.getStockProductAggregations().stream().map(StockProductAggregation::getCode),
                                    stockProduct.getStockProductAggregationBlocks().stream().map(StockProductAggregationBlock::getCode),
                                    stockProduct.getStockProductMarks().stream().map(StockProductMark::getCode)
                            )
                            .flatMap(Function.identity())
                            .toList());
        });
        stockTransferProductAggregationsDTO.setResidueCount(stockTransferProductAggregationsDTO.getTransferCount().subtract(stockTransferProductAggregationsDTO.getSaleCount()));
        stockTransferProductAggregationsDTO.setResidueAggregations(stockTransferProductAggregationsDTO.getTransferAggregations().stream().filter(transferAggregation -> !stockTransferProductAggregationsDTO.getSaleAggregations().contains(transferAggregation)).toList());
        return stockTransferProductAggregationsDTO;
    }
}
