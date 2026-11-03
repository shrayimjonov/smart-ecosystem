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
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustment;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustmentProduct;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustmentProductAggregation;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustmentProductAggregationBlock;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustmentProductMark;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.dto.stockadjustment.AdjustmentDocumentDTO;
import uz.uzkassa.smartpossupply.dto.stockadjustment.AdjustmentStatusUpdateDTO;
import uz.uzkassa.smartpossupply.dto.stockadjustment.StockAdjustmentAggregationCodesDTO;
import uz.uzkassa.smartpossupply.dto.stockadjustment.StockAdjustmentDTO;
import uz.uzkassa.smartpossupply.dto.stockadjustment.StockAdjustmentDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockadjustment.StockAdjustmentListDTO;
import uz.uzkassa.smartpossupply.dto.stockadjustment.StockAdjustmentProductDTO;
import uz.uzkassa.smartpossupply.dto.stockadjustment.StockAdjustmentProductDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockadjustment.WithdrawRequestDTO;
import uz.uzkassa.smartpossupply.dto.stockadjustment.WithdrawResponseDTO;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.enums.StockAdjustmentStatus;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.filters.StockAdjustmentFilter;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.repository.ProductSerialWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.StockAdjustmentProductAggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.StockAdjustmentProductAggregationRepository;
import uz.uzkassa.smartpossupply.repository.StockAdjustmentProductMarkRepository;
import uz.uzkassa.smartpossupply.repository.StockAdjustmentProductRepository;
import uz.uzkassa.smartpossupply.repository.StockAdjustmentRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseProductRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class StockAdjustmentService extends BaseService {

    StockAdjustmentRepository stockAdjustmentRepository;
    WarehouseProductRepository warehouseProductRepository;
    StockAdjustmentProductRepository stockAdjustmentProductRepository;
    StockAdjustmentProductAggregationRepository stockAdjustmentProductAggregationRepository;
    StockAdjustmentProductMarkRepository stockAdjustmentProductMarkRepository;
    StockAdjustmentProductAggregationBlockRepository stockAdjustmentProductAggregationBlockRepository;
    WarehouseRepository warehouseRepository;
    ProductRepository productRepository;
    AslBelgiClient aslBelgiClient;
    WarehouseProductService warehouseProductService;
    ProductSerialWarehouseRepository productSerialWarehouseRepository;
    CisInfoService cisInfoService;
    NumberingContext numberingContext;
    ValidationContext validationContext;

    @Transactional
    public Long create(StockAdjustmentDTO stockAdjustmentDTO) {
        validationContext.validate(stockAdjustmentDTO);
        Company company = getCurrentCompany();
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_ADJUSTMENT, company.getId());
        Unit unit = getUnit();
        StockAdjustment stockAdjustment = new StockAdjustment();
        stockAdjustment.setAdjustmentNumber(numberingMap.get(NUMBER));
        stockAdjustment.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stockAdjustment.setCompanyId(company.getId());
        stockAdjustment.setAdjustmentDate(Optional.ofNullable(stockAdjustmentDTO.getAdjustmentDate()).orElse(LocalDateTime.now()));
        stockAdjustment.setDescription(stockAdjustmentDTO.getDescription());
        stockAdjustment.setBranchId(stockAdjustmentDTO.getBranchId());
        stockAdjustment.setWarehouseId(stockAdjustmentDTO.getWarehouseId());
        stockAdjustment.setRejectReason(stockAdjustmentDTO.getRejectReason());
        stockAdjustment.setRejectOtherReason(stockAdjustmentDTO.getRejectOtherReason());
        stockAdjustment.setAdjustmentReason(stockAdjustmentDTO.getAdjustmentReason());
        stockAdjustment.setCurrencyType(stockAdjustmentDTO.getCurrencyType());
        stockAdjustment.setExchangeRate(stockAdjustmentDTO.getExchangeRate());
        if (stockAdjustmentDTO.getDocument() != null) {
            stockAdjustment.setDocumentNumber(stockAdjustmentDTO.getDocument().getNumber());
            stockAdjustment.setDocumentDate(stockAdjustmentDTO.getDocument().getDate());
            stockAdjustment.setDocumentType(stockAdjustmentDTO.getDocument().getType());
        }
        stockAdjustment = stockAdjustmentRepository.save(stockAdjustment);

        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalForeignPrice = BigDecimal.ZERO;
        Set<String> codes = new HashSet<>();

        for (StockAdjustmentProductDTO stockAdjustmentProductDTO : stockAdjustmentDTO.getProducts()) {
            Product product = productRepository.findById(stockAdjustmentProductDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockAdjustmentProductDTO.getId()));
            StockAdjustmentProduct stockAdjustmentProduct = new StockAdjustmentProduct();
            stockAdjustmentProduct.setProductId(product.getId());
            stockAdjustmentProduct.setProductSerialId(stockAdjustmentProductDTO.getProductSerialId());
            stockAdjustmentProduct.setQty(stockAdjustmentProductDTO.getQty());
            stockAdjustmentProduct.setUnitId(unit.getId());
            stockAdjustmentProduct.setPrice(stockAdjustmentProductDTO.getPrice());
            stockAdjustmentProduct.setForeignPrice(stockAdjustmentProductDTO.getForeignPrice());
            stockAdjustmentProduct.setStockAdjustmentId(stockAdjustment.getId());
            stockAdjustmentProduct = stockAdjustmentProductRepository.save(stockAdjustmentProduct);

            if (!CollectionUtils.isEmpty(stockAdjustmentProductDTO.getAggregationCodes())) {
                List<StockAdjustmentProductAggregation> stockAdjustmentProductAggregations = new ArrayList<>();
                for (String aggregationCode : stockAdjustmentProductDTO.getAggregationCodes()) {
                    String code = MarkUtils.clearAggregationCode(aggregationCode);
                    if (codes.add(code)) {
                        StockAdjustmentProductAggregation stockAdjustmentProductAggregation = new StockAdjustmentProductAggregation();
                        stockAdjustmentProductAggregation.setStockAdjustmentProductId(stockAdjustmentProduct.getId());
                        stockAdjustmentProductAggregation.setCode(code);
                        stockAdjustmentProductAggregation.setOriginalCode(aggregationCode);
                        stockAdjustmentProductAggregation.setQty(stockAdjustmentProductDTO.getQty());
                        stockAdjustmentProductAggregation.setProductId(stockAdjustmentProductDTO.getId());
                        stockAdjustmentProductAggregations.add(stockAdjustmentProductAggregation);
                    }
                }
                stockAdjustmentProductAggregationRepository.saveAll(stockAdjustmentProductAggregations);
            }

            if (!CollectionUtils.isEmpty(stockAdjustmentProductDTO.getMarkCodes())) {
                List<StockAdjustmentProductMark> stockAdjustmentProductMarks = new ArrayList<>();
                for (String markCode : stockAdjustmentProductDTO.getMarkCodes()) {
                    String code = MarkUtils.clearOriginalCode(markCode, product.getProductType());
                    if (codes.add(code)) {
                        StockAdjustmentProductMark stockAdjustmentProductMark = new StockAdjustmentProductMark();
                        stockAdjustmentProductMark.setStockAdjustmentProductId(stockAdjustmentProduct.getId());
                        stockAdjustmentProductMark.setCode(code);
                        stockAdjustmentProductMark.setOriginalCode(markCode);
                        stockAdjustmentProductMark.setQty(stockAdjustmentProductDTO.getQty());
                        stockAdjustmentProductMark.setProductId(stockAdjustmentProductDTO.getId());
                        stockAdjustmentProductMarks.add(stockAdjustmentProductMark);
                    }
                }
                stockAdjustmentProductMarkRepository.saveAll(stockAdjustmentProductMarks);
            }

            if (!CollectionUtils.isEmpty(stockAdjustmentProductDTO.getAggregationBlockCodes())) {
                List<StockAdjustmentProductAggregationBlock> stockAdjustmentProductAggregationBlocks = new ArrayList<>();
                for (String aggregationBlockCode : stockAdjustmentProductDTO.getAggregationBlockCodes()) {
                    String code = MarkUtils.clearOriginalCode(aggregationBlockCode, product.getProductType());
                    if (codes.add(code)) {
                        StockAdjustmentProductAggregationBlock stockAdjustmentProductAggregationBlock = new StockAdjustmentProductAggregationBlock();
                        stockAdjustmentProductAggregationBlock.setStockAdjustmentProductId(stockAdjustmentProduct.getId());
                        stockAdjustmentProductAggregationBlock.setCode(code);
                        stockAdjustmentProductAggregationBlock.setOriginalCode(aggregationBlockCode);
                        stockAdjustmentProductAggregationBlock.setQty(stockAdjustmentProductDTO.getQty());
                        stockAdjustmentProductAggregationBlock.setProductId(stockAdjustmentProductDTO.getId());
                        stockAdjustmentProductAggregationBlocks.add(stockAdjustmentProductAggregationBlock);
                    }
                }
                stockAdjustmentProductAggregationBlockRepository.saveAll(stockAdjustmentProductAggregationBlocks);
            }

            totalQty = totalQty.add(stockAdjustmentProductDTO.getQty());
            if (stockAdjustmentProductDTO.getPrice() != null) {
                totalPrice = totalPrice.add(stockAdjustmentProductDTO.getPrice());
            }
            if (stockAdjustmentProductDTO.getForeignPrice() != null) {
                totalForeignPrice = totalForeignPrice.add(stockAdjustmentProductDTO.getForeignPrice());
            }
            stockAdjustmentProduct.setHasMarking(!CollectionUtils.isEmpty(stockAdjustmentProductDTO.getAggregationCodes()) || !CollectionUtils.isEmpty(stockAdjustmentProductDTO.getAggregationBlockCodes()) || !CollectionUtils.isEmpty(stockAdjustmentProductDTO.getMarkCodes()));
            stockAdjustmentProductRepository.save(stockAdjustmentProduct);

        }
        stockAdjustment.setTotalQty(totalQty);
        stockAdjustment.setTotalPrice(totalPrice);
        stockAdjustment.setTotalForeignPrice(totalForeignPrice);
        stockAdjustmentRepository.save(stockAdjustment);
        return stockAdjustment.getId();
    }

    @Transactional
    public Long update(Long id, StockAdjustmentDTO stockAdjustmentDTO) {
        StockAdjustment stockAdjustment = stockAdjustmentRepository
                .findById(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(StockAdjustment.class.getSimpleName(), "id", id));

        this.validateCompany(stockAdjustment.getCompanyId());
        if (!StockAdjustmentStatus.DRAFT.equals(stockAdjustment.getStatus())) {
            throw validationService.badRequestExceptionThrow(
                    localizationService.localize(MessageConstants.YOU_CAN_UPDATE_ONLY, new Object[]{StockAdjustmentStatus.DRAFT.getName()})).get();
        }

        stockAdjustment.setAdjustmentDate(stockAdjustmentDTO.getAdjustmentDate());
        stockAdjustment.setDescription(stockAdjustmentDTO.getDescription());
        stockAdjustment.setCurrencyType(stockAdjustmentDTO.getCurrencyType());
        stockAdjustment.setExchangeRate(stockAdjustmentDTO.getExchangeRate());

        if (stockAdjustmentDTO.getDocument() != null) {
            stockAdjustment.setDocumentNumber(stockAdjustmentDTO.getDocument().getNumber());
            stockAdjustment.setDocumentDate(stockAdjustmentDTO.getDocument().getDate());
            stockAdjustment.setDocumentType(stockAdjustmentDTO.getDocument().getType());
        }
        stockAdjustment.setAdjustmentReason(stockAdjustmentDTO.getAdjustmentReason());

        Branch branch = branchRepository.findById(stockAdjustmentDTO.getBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", stockAdjustmentDTO.getBranchId()));
        stockAdjustment.setBranch(branch);
        stockAdjustment.setBranchId(branch.getId());

        Warehouse warehouse = warehouseRepository.findById(stockAdjustmentDTO.getWarehouseId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Warehouse.class.getSimpleName(), "id", stockAdjustmentDTO.getWarehouseId()));
        stockAdjustment.setWarehouse(warehouse);
        stockAdjustment.setWarehouseId(warehouse.getId());

        stockAdjustment.setRejectReason(stockAdjustmentDTO.getRejectReason());
        stockAdjustment.setRejectOtherReason(stockAdjustmentDTO.getRejectOtherReason());

        if (
                stockAdjustmentDTO.getAdjustmentNumber() != null &&
                        !stockAdjustmentDTO.getAdjustmentNumber().equals(stockAdjustment.getAdjustmentNumber())
        ) {
            if (
                    stockAdjustmentRepository.existsStockAdjustmentByAdjustmentNumberAndCompanyIdAndIdNot(
                            stockAdjustmentDTO.getAdjustmentNumber(),
                            stockAdjustment.getCompanyId(),
                            stockAdjustment.getId()
                    )
            ) {
                throw validationService.conflictExceptionThrow(
                        localizationService.localize(
                                MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                                new Object[]{
                                        localizationService.localize(StockAdjustment.class.getSimpleName()),
                                        localizationService.localize(MessageConstants.ADJUSTMENT) + " " + localizationService.localize(MessageConstants.NUMBER),
                                        stockAdjustmentDTO.getAdjustmentNumber(),
                                }
                        )
                ).get();
            }
            stockAdjustment.setAdjustmentNumber(stockAdjustmentDTO.getAdjustmentNumber());
        }

        Map<Long, StockAdjustmentProduct> stockAdjustmentProductsMap = stockAdjustment
                .getProducts()
                .stream()
                .collect(Collectors.toMap(StockAdjustmentProduct::getId, Function.identity()));

        Unit unit = getUnit();
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalForeignPrice = BigDecimal.ZERO;
        Set<String> codes = new HashSet<>();

        for (StockAdjustmentProductDTO stockAdjustmentProductDTO : stockAdjustmentDTO.getProducts()) {
            StockAdjustmentProduct stockAdjustmentProduct;
            if (
                    stockAdjustmentProductDTO.getStockAdjustmentProductId() != null &&
                            stockAdjustmentProductsMap.get(stockAdjustmentProductDTO.getStockAdjustmentProductId()) != null
            ) {
                stockAdjustmentProduct = stockAdjustmentProductsMap.get(stockAdjustmentProductDTO.getStockAdjustmentProductId());
            } else {
                stockAdjustmentProduct = new StockAdjustmentProduct();
            }

            Product product = productRepository.findById(stockAdjustmentProductDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockAdjustmentProductDTO.getId()));

            stockAdjustmentProduct.setStockAdjustmentId(stockAdjustment.getId());
            stockAdjustmentProduct.setProductId(product.getId());
            stockAdjustmentProduct.setProductSerialId(stockAdjustmentProductDTO.getProductSerialId());
            stockAdjustmentProduct.setUnitId(unit.getId());
            stockAdjustmentProduct.setQty(stockAdjustmentProductDTO.getQty());
            stockAdjustmentProduct.setPrice(stockAdjustmentProductDTO.getPrice());
            stockAdjustmentProduct.setForeignPrice(stockAdjustmentProductDTO.getForeignPrice());
            stockAdjustmentProduct = stockAdjustmentProductRepository.save(stockAdjustmentProduct);


            if (stockAdjustmentProductDTO.getAggregationCodes() != null) {
                stockAdjustmentProductAggregationRepository.deleteAllByStockAdjustmentProductId(stockAdjustmentProduct.getId());
                List<StockAdjustmentProductAggregation> stockAdjustmentProductAggregations = new ArrayList<>();
                for (String aggregationCode : stockAdjustmentProductDTO.getAggregationCodes()) {
                    String code = MarkUtils.clearAggregationCode(aggregationCode);
                    if (codes.add(code)) {
                        StockAdjustmentProductAggregation stockAdjustmentProductAggregation = new StockAdjustmentProductAggregation();
                        stockAdjustmentProductAggregation.setStockAdjustmentProductId(stockAdjustmentProduct.getId());
                        stockAdjustmentProductAggregation.setCode(code);
                        stockAdjustmentProductAggregation.setOriginalCode(aggregationCode);
                        stockAdjustmentProductAggregation.setQty(stockAdjustmentProductDTO.getQty());
                        stockAdjustmentProductAggregation.setProductId(stockAdjustmentProductDTO.getId());
                        stockAdjustmentProductAggregations.add(stockAdjustmentProductAggregation);
                    }
                }
                stockAdjustmentProductAggregationRepository.saveAll(stockAdjustmentProductAggregations);
            }

            if (stockAdjustmentProductDTO.getMarkCodes() != null) {
                stockAdjustmentProductMarkRepository.deleteAllByStockAdjustmentProductId(stockAdjustmentProduct.getId());
                List<StockAdjustmentProductMark> stockAdjustmentProductMarks = new ArrayList<>();
                for (String markCode : stockAdjustmentProductDTO.getMarkCodes()) {
                    String code = MarkUtils.clearOriginalCode(markCode, product.getProductType());
                    if (codes.add(code)) {
                        StockAdjustmentProductMark stockAdjustmentProductMark = new StockAdjustmentProductMark();
                        stockAdjustmentProductMark.setStockAdjustmentProductId(stockAdjustmentProduct.getId());
                        stockAdjustmentProductMark.setCode(code);
                        stockAdjustmentProductMark.setOriginalCode(markCode);
                        stockAdjustmentProductMark.setQty(stockAdjustmentProductDTO.getQty());
                        stockAdjustmentProductMark.setProductId(stockAdjustmentProductDTO.getId());
                        stockAdjustmentProductMarks.add(stockAdjustmentProductMark);
                    }
                }
                stockAdjustmentProductMarkRepository.saveAll(stockAdjustmentProductMarks);
            }

            if (stockAdjustmentProductDTO.getAggregationBlockCodes() != null) {
                stockAdjustmentProductAggregationBlockRepository.deleteAllByStockAdjustmentProductId(stockAdjustmentProduct.getId());
                List<StockAdjustmentProductAggregationBlock> stockAdjustmentProductAggregationBlocks = new ArrayList<>();
                for (String aggregationBlockCode : stockAdjustmentProductDTO.getAggregationBlockCodes()) {
                    String code = MarkUtils.clearOriginalCode(aggregationBlockCode, product.getProductType());
                    if (codes.add(code)) {
                        StockAdjustmentProductAggregationBlock stockAdjustmentProductAggregationBlock = new StockAdjustmentProductAggregationBlock();
                        stockAdjustmentProductAggregationBlock.setStockAdjustmentProductId(stockAdjustmentProduct.getId());
                        stockAdjustmentProductAggregationBlock.setCode(code);
                        stockAdjustmentProductAggregationBlock.setOriginalCode(aggregationBlockCode);
                        stockAdjustmentProductAggregationBlock.setQty(stockAdjustmentProductDTO.getQty());
                        stockAdjustmentProductAggregationBlock.setProductId(stockAdjustmentProductDTO.getId());
                        stockAdjustmentProductAggregationBlocks.add(stockAdjustmentProductAggregationBlock);
                    }
                }
                stockAdjustmentProductAggregationBlockRepository.saveAll(stockAdjustmentProductAggregationBlocks);
            }

            stockAdjustmentProductsMap.remove(stockAdjustmentProduct.getId());
            totalQty = totalQty.add(stockAdjustmentProductDTO.getQty());
            if (stockAdjustmentProductDTO.getPrice() != null) {
                totalPrice = totalPrice.add(stockAdjustmentProductDTO.getPrice());
            }
            if (stockAdjustmentProductDTO.getForeignPrice() != null) {
                totalForeignPrice = totalForeignPrice.add(stockAdjustmentProductDTO.getForeignPrice());
            }
            if (stockAdjustmentProductDTO.getAggregationCodes() != null || stockAdjustmentProductDTO.getMarkCodes() != null || stockAdjustmentProductDTO.getAggregationBlockCodes() != null) {
                stockAdjustmentProduct.setHasMarking(!CollectionUtils.isEmpty(stockAdjustmentProductDTO.getAggregationCodes()) || !CollectionUtils.isEmpty(stockAdjustmentProductDTO.getAggregationBlockCodes()) || !CollectionUtils.isEmpty(stockAdjustmentProductDTO.getMarkCodes()));
                stockAdjustmentProductRepository.save(stockAdjustmentProduct);
            }
        }
        stockAdjustment.setTotalQty(totalQty);
        stockAdjustment.setTotalPrice(totalPrice);
        stockAdjustment.setTotalForeignPrice(totalForeignPrice);

        if (!stockAdjustmentProductsMap.isEmpty()) {
            List<Long> stockAdjustmentProductIds = new ArrayList<>(stockAdjustmentProductsMap.keySet());
            stockAdjustmentProductAggregationRepository.deleteAllByStockProductIds(stockAdjustmentProductIds);
            stockAdjustmentProductMarkRepository.deleteAllByStockProductIds(stockAdjustmentProductIds);
            stockAdjustmentProductAggregationBlockRepository.deleteAllByStockProductIds(stockAdjustmentProductIds);
            stockAdjustmentProductRepository.deleteByIds(stockAdjustmentProductIds);
        }
        stockAdjustmentRepository.save(stockAdjustment);
        return id;
    }

    public StockAdjustmentDetailDTO get(Long id, boolean withCodes) {
        StockAdjustment stockAdjustment = stockAdjustmentRepository
                .getDetail(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(StockAdjustment.class.getSimpleName(), "id", id));
        this.validateCompany(stockAdjustment.getCompanyId());

        StockAdjustmentDetailDTO stockAdjustmentDetailDTO = new StockAdjustmentDetailDTO();
        stockAdjustmentDetailDTO.setId(stockAdjustment.getId());
        stockAdjustmentDetailDTO.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
        stockAdjustmentDetailDTO.setAdjustmentNumber(stockAdjustment.getAdjustmentNumber());
        stockAdjustmentDetailDTO.setStatus(stockAdjustment.getStatus());
        stockAdjustmentDetailDTO.setBranch(stockAdjustment.getBranch().toCommonDTO());
        stockAdjustmentDetailDTO.setWarehouse(stockAdjustment.getWarehouse().toCommonDTO());
        stockAdjustmentDetailDTO.setDescription(stockAdjustment.getDescription());
        stockAdjustmentDetailDTO.setTotalQty(stockAdjustment.getTotalQty());
        stockAdjustmentDetailDTO.setTotalPrice(stockAdjustment.getTotalPrice());
        stockAdjustmentDetailDTO.setTotalForeignPrice(stockAdjustment.getTotalForeignPrice());
        stockAdjustmentDetailDTO.setCurrencyType(stockAdjustment.getCurrencyType());
        stockAdjustmentDetailDTO.setExchangeRate(stockAdjustment.getExchangeRate());
        stockAdjustmentDetailDTO.setRejectReason(stockAdjustment.getRejectReason());
        stockAdjustmentDetailDTO.setRejectOtherReason(stockAdjustment.getRejectOtherReason());
        stockAdjustmentDetailDTO.setAdjustmentReason(stockAdjustment.getAdjustmentReason());
        stockAdjustmentDetailDTO.setLastModifiedDate(stockAdjustment.getUpdatedDate());
        if (StringUtils.isNotEmpty(stockAdjustment.getDocumentNumber())) {
            stockAdjustmentDetailDTO.setDocument(new AdjustmentDocumentDTO(stockAdjustment.getDocumentNumber(), stockAdjustment.getDocumentDate(), stockAdjustment.getDocumentType()));
        }

        stockAdjustment
                .getProducts()
                .forEach(stockAdjustmentProduct -> {
                    StockAdjustmentProductDetailDTO stockAdjustmentProductDetailDTO = new StockAdjustmentProductDetailDTO();
                    stockAdjustmentProductDetailDTO.setStockAdjustmentProductId(stockAdjustmentProduct.getId());
                    stockAdjustmentProductDetailDTO.setId(stockAdjustmentProduct.getProductId());
                    stockAdjustmentProductDetailDTO.setName(stockAdjustmentProduct.getProduct().getName());
                    stockAdjustmentProductDetailDTO.setBarcode(stockAdjustmentProduct.getProduct().getBarcode());
                    stockAdjustmentProductDetailDTO.setVatBarcode(stockAdjustmentProduct.getProduct().getVatBarcode());
                    stockAdjustmentProductDetailDTO.setHasMark(stockAdjustmentProduct.getProduct().isHasMark());
                    stockAdjustmentProductDetailDTO.setHasMarking(stockAdjustmentProduct.isHasMarking());
                    stockAdjustmentProductDetailDTO.setProductType(stockAdjustmentProduct.getProduct().getProductType());
                    if (withCodes) {
                        if (!CollectionUtils.isEmpty(stockAdjustmentProduct.getStockAdjustmentProductAggregations())) {
                            stockAdjustmentProduct.getStockAdjustmentProductAggregations().forEach(stockAdjustmentProductAggregation -> stockAdjustmentProductDetailDTO.addAggregation(stockAdjustmentProductAggregation.toCheckDTO()));
                        }
                        if (!CollectionUtils.isEmpty(stockAdjustmentProduct.getStockAdjustmentProductAggregationBlocks())) {
                            stockAdjustmentProduct.getStockAdjustmentProductAggregationBlocks().forEach(stockAdjustmentProductAggregationBlock -> stockAdjustmentProductDetailDTO.addAggregationBlock(stockAdjustmentProductAggregationBlock.toCheckDTO()));
                        }
                        if (!CollectionUtils.isEmpty(stockAdjustmentProduct.getStockAdjustmentProductMarks())) {
                            stockAdjustmentProduct.getStockAdjustmentProductMarks().forEach(stockAdjustmentProductMark -> stockAdjustmentProductDetailDTO.addAggregationBlock(stockAdjustmentProductMark.toCheckDTO()));
                        }
                    }
                    stockAdjustmentProductDetailDTO.setBoxBarcode(stockAdjustmentProduct.getProduct().getBoxBarcode());
                    stockAdjustmentProductDetailDTO.setBlockBarcode(stockAdjustmentProduct.getProduct().getBlockBarcode());
                    if (stockAdjustmentProduct.getProduct().getParent() != null) {
                        stockAdjustmentProductDetailDTO.setParent(stockAdjustmentProduct.getProduct().getParent().toProductCommonDTO());
                        if (StringUtils.isEmpty(stockAdjustmentProductDetailDTO.getBlockBarcode())) {
                            stockAdjustmentProductDetailDTO.setBlockBarcode(stockAdjustmentProduct.getProduct().getParent().getBarcode());
                        }
                    }
                    if (stockAdjustmentProduct.getProductSerialId() != null) {
                        stockAdjustmentProductDetailDTO.setProductSerial(stockAdjustmentProduct.getProductSerial().toCommonDTO());
                        stockAdjustmentProductDetailDTO.setQtyInWarehouseBySerial(productSerialWarehouseRepository.getQty(stockAdjustment.getWarehouseId(), stockAdjustmentProduct.getProductSerialId()));
                    }
                    stockAdjustmentProductDetailDTO.setQty(stockAdjustmentProduct.getQty());
                    stockAdjustmentProductDetailDTO.setPrice(stockAdjustmentProduct.getPrice());
                    stockAdjustmentProductDetailDTO.setForeignPrice(stockAdjustmentProduct.getForeignPrice());

                    if (!StockAdjustmentStatus.COMPLETED.equals(stockAdjustment.getStatus())) {
                        stockAdjustmentProductDetailDTO.setQtyInWarehouse(
                                warehouseProductRepository.getQty(
                                        stockAdjustment.getWarehouseId(),
                                        stockAdjustmentProduct.getProductId(),
                                        stockAdjustmentProduct.getUnitId()
                                )
                        );
                        stockAdjustmentProductDetailDTO.setReservationQty(
                                warehouseProductRepository.getReservationQty(
                                        stockAdjustment.getWarehouseId(),
                                        stockAdjustmentProduct.getProductId(),
                                        stockAdjustmentProduct.getUnitId()
                                )
                        );
                    }
                    stockAdjustmentProductDetailDTO.setBlockProducts(stockAdjustmentProduct.getProduct().getBlockProducts());
                    stockAdjustmentDetailDTO.addProduct(stockAdjustmentProductDetailDTO);
                });
        return stockAdjustmentDetailDTO;
    }

    public Page<StockAdjustmentListDTO> getList(StockAdjustmentFilter filter) {
        ResultList<StockAdjustment> resultList = stockAdjustmentRepository.getResultList(filter);
        List<StockAdjustmentListDTO> result = resultList
                .getList()
                .stream()
                .map(stockAdjustment -> {
                    StockAdjustmentListDTO stockAdjustmentListDTO = new StockAdjustmentListDTO();
                    stockAdjustmentListDTO.setId(stockAdjustment.getId());
                    stockAdjustmentListDTO.setAdjustmentNumber(stockAdjustment.getAdjustmentNumber());
                    stockAdjustmentListDTO.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
                    stockAdjustmentListDTO.setBranch(stockAdjustment.getBranch().toCommonDTO());
                    stockAdjustmentListDTO.setWarehouse(stockAdjustment.getWarehouse().toCommonDTO());
                    stockAdjustmentListDTO.setStatus(stockAdjustment.getStatus());
                    stockAdjustmentListDTO.setTotalQty(stockAdjustment.getTotalQty());
                    stockAdjustmentListDTO.setTotalPrice(stockAdjustment.getTotalPrice());
                    stockAdjustmentListDTO.setTotalForeignPrice(stockAdjustment.getTotalForeignPrice());
                    stockAdjustmentListDTO.setCurrencyType(stockAdjustment.getCurrencyType());
                    stockAdjustmentListDTO.setCreator(stockAdjustment.getCreator().toCommonDTO());
                    stockAdjustmentListDTO.setLastModifiedDate(stockAdjustment.getUpdatedDate());
                    return stockAdjustmentListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        StockAdjustment stockAdjustment = stockAdjustmentRepository
                .findById(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(StockAdjustment.class.getSimpleName(), "id", id));
        this.validateCompany(stockAdjustment.getCompanyId());

        if (!StockAdjustmentStatus.DRAFT.equals(stockAdjustment.getStatus())) {
            throw validationService.badRequestExceptionThrow(
                    localizationService.localize(MessageConstants.YOU_CAN_DELETE_ONLY, new Object[]{StockAdjustmentStatus.DRAFT.getName()})
            ).get();
        }

        List<Long> stockAdjustmentProductIds = stockAdjustment
                .getProducts()
                .stream()
                .map(AbstractAuditingEntity::getId)
                .toList();
        stockAdjustmentProductRepository.deleteAllByStockAdjustmentIds(stockAdjustmentProductIds);
        stockAdjustmentProductAggregationRepository.deleteAllByStockAdjustmentProductIds(stockAdjustmentProductIds);
        stockAdjustmentProductAggregationBlockRepository.deleteAllByStockAdjustmentProductIds(stockAdjustmentProductIds);
        stockAdjustmentProductMarkRepository.deleteAllByStockAdjustmentProductIds(stockAdjustmentProductIds);

        stockAdjustment.setDeleted(true);
        stockAdjustmentRepository.save(stockAdjustment);
    }

    @Transactional
    public CheckStatusResponseDTO updateStatus(Long id, AdjustmentStatusUpdateDTO statusUpdateDTO, LocalDateTime lastModifiedDate) {
        StockAdjustment stockAdjustment = stockAdjustmentRepository
                .findById(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(StockAdjustment.class.getSimpleName(), "id", id));
        if (!stockAdjustment.getUpdatedDate().equals(lastModifiedDate)) {
            return new CheckStatusResponseDTO(false, 1);
        }
        this.validateCompany(stockAdjustment.getCompanyId());

        StockAdjustmentStatus status = statusUpdateDTO.getStatus();
        if (StockAdjustmentStatus.COMPLETED.equals(status)) {
            if (!StockAdjustmentStatus.DRAFT.equals(stockAdjustment.getStatus())) {
                throw validationService.badRequestExceptionThrow("Только черновой статус корректировки может быть завершен").get();
            }
            warehouseProductService.out(stockAdjustment);
            stockAdjustment.setCompletedDate(LocalDateTime.now());
        } else if (StockAdjustmentStatus.REJECTED.equals(status)) {
            if (!StockAdjustmentStatus.COMPLETED.equals(stockAdjustment.getStatus())) {
                throw validationService.badRequestExceptionThrow("Только завершенный статус корректировки может быть отклонен").get();
            }
            warehouseProductService.in(stockAdjustment);
        }
        stockAdjustment.setStatus(status);
        if (statusUpdateDTO.getReason() != null) {
            stockAdjustment.setRejectReason(statusUpdateDTO.getReason());
            stockAdjustment.setRejectOtherReason(statusUpdateDTO.getOtherReason());
        }
        stockAdjustmentRepository.save(stockAdjustment);
        return new CheckStatusResponseDTO(true, 0);
    }

    @Transactional
    public Long withdraw(Long id, WithdrawRequestDTO withdrawRequestDTO) {
        StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(StockAdjustment.class.getSimpleName(), "id", id));
        if (StockAdjustmentStatus.SENT.equals(stockAdjustment.getStatus())) {
            throw validationService.badRequestExceptionThrow("Товары уже выведен из оборота").get();
        }

        WithdrawResponseDTO withdrawResponseDTO = aslBelgiClient.withdraw(getCompanyAggregationInfo().getAslBelgiToken(), getCurrentCompanyTin(), withdrawRequestDTO);
        if (withdrawRequestDTO != null && withdrawResponseDTO.getDocumentId() != null) {
            stockAdjustment.setStatus(StockAdjustmentStatus.SENT);
            return id;
        } else {
            throw validationService.badRequestExceptionThrow("Товары не выведен из оборота").get();
        }

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
                    .map(cisInfoResponse -> Optional.ofNullable(cisInfoResponse.getCisInfo().getRequestedCis()).orElseGet(() -> cisInfoResponse.getCisInfo().getCode())).toList());
            if (!CollectionUtils.isEmpty(aggregationCodesForUsedCheck)) {
                List<StockAdjustmentProductAggregation> stockAdjustmentProductAggregations = stockAdjustmentProductAggregationRepository.getByCodesAndStatusNot(
                        aggregationCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockAdjustmentStatus.REJECTED
                );
                if (!CollectionUtils.isEmpty(stockAdjustmentProductAggregations)) {
                    stockAdjustmentProductAggregations.forEach(stockAdjustmentProductAggregation -> {
                        CisInfoResponse cisInfoResponse = resultMap.get(stockAdjustmentProductAggregation.getOriginalCode());
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.get(stockAdjustmentProductAggregation.getCode());
                        }
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.values().stream().filter(tempCisInfo -> tempCisInfo.getCisInfo().getCode().equals(stockAdjustmentProductAggregation.getCode())).findFirst().orElse(null);
                        }
                        if (cisInfoResponse != null) {
                            CisInfo cisInfo = cisInfoResponse.getCisInfo();
                            cisInfo.setErrorMessage(
                                    "Код уже использован в корректировка " + stockAdjustmentProductAggregation.getStockAdjustmentProduct().getStockAdjustment().getAdjustmentNumber()
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
            if (xtraceSettings.isEnabled()){
                cisInfoService.batchCheckMark(validationDTO, resultMap, markCodes);
            }
            List<String> markCodesForUsedCheck = new ArrayList<>(resultMap.values().stream().filter(cisInfoResponse -> TuronMarkStatus.isSuccess(cisInfoResponse.getCisInfo().getStatus()))
                    .map(cisInfoResponse -> Optional.ofNullable(cisInfoResponse.getCisInfo().getRequestedCis()).orElseGet(() -> cisInfoResponse.getCisInfo().getCode())).toList());
            if (!CollectionUtils.isEmpty(markCodesForUsedCheck)) {
                List<StockAdjustmentProductMark> stockAdjustmentProductMarks = stockAdjustmentProductMarkRepository.getByCodesAndCompanyIdAndStatusNot(
                        markCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockAdjustmentStatus.REJECTED
                );
                if (!CollectionUtils.isEmpty(stockAdjustmentProductMarks)) {
                    stockAdjustmentProductMarks.forEach(stockAdjustmentProductMark -> {
                        CisInfoResponse cisInfoResponse = resultMap.get(stockAdjustmentProductMark.getOriginalCode());
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.get(stockAdjustmentProductMark.getCode());
                        }
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.values().stream().filter(tempCisInfo -> tempCisInfo.getCisInfo().getCode().equals(stockAdjustmentProductMark.getCode())).findFirst().orElse(null);
                        }
                        if (cisInfoResponse != null) {
                            CisInfo cisInfo = cisInfoResponse.getCisInfo();
                            cisInfo.setErrorMessage(
                                    "Код уже использован в корректировка " + stockAdjustmentProductMark.getStockAdjustmentProduct().getStockAdjustment().getAdjustmentNumber()
                            );
                            cisInfo.setStatus(TuronMarkStatus.ERROR);
                        }
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
                    .map(cisInfoResponse -> Optional.ofNullable(cisInfoResponse.getCisInfo().getRequestedCis()).orElseGet(() -> cisInfoResponse.getCisInfo().getCode())).toList());
            if (!aggregationBlockCodesForUsedCheck.isEmpty()) {
                List<StockAdjustmentProductAggregationBlock> stockAdjustmentProductAggregationBlocks = stockAdjustmentProductAggregationBlockRepository.getByCodesAndCompanyIdAndStatusNot(
                        aggregationBlockCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockAdjustmentStatus.REJECTED
                );
                if (!CollectionUtils.isEmpty(stockAdjustmentProductAggregationBlocks)) {
                    stockAdjustmentProductAggregationBlocks.forEach(stockAdjustmentProductAggregationBlock -> {
                        CisInfoResponse cisInfoResponse = resultMap.get(stockAdjustmentProductAggregationBlock.getOriginalCode());
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.get(stockAdjustmentProductAggregationBlock.getCode());
                        }
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.values().stream().filter(tempCisInfo -> tempCisInfo.getCisInfo().getCode().equals(stockAdjustmentProductAggregationBlock.getCode())).findFirst().orElse(null);
                        }
                        if (cisInfoResponse != null) {
                            CisInfo cisInfo = cisInfoResponse.getCisInfo();
                            cisInfo.setErrorMessage(
                                    "Код уже использован в корректировка " + stockAdjustmentProductAggregationBlock.getStockAdjustmentProduct().getStockAdjustment().getAdjustmentNumber()
                            );
                            cisInfo.setStatus(TuronMarkStatus.ERROR);
                        }
                    });
                }
            }
        }
        return Arrays.asList(resultMap.values().toArray(new CisInfoResponse[0]));
    }

    public Map<CodeType, List<CheckedCodeDTO>> getAggregationCodes(Long stockProductId) {
        Map<CodeType, List<CheckedCodeDTO>> result = new HashMap<>();

        List<CheckedCodeDTO> aggregationCodes = stockAdjustmentProductAggregationRepository.findAllByStockAdjustmentProductIdAndDeletedIsFalse(stockProductId).stream().map(StockAdjustmentProductAggregation::toCheckDTO).toList();

        result.put(CodeType.AGGREGATION, aggregationCodes);

        List<CheckedCodeDTO> markCodes = stockAdjustmentProductMarkRepository.findAllByStockAdjustmentProductIdAndDeletedIsFalse(stockProductId).stream().map(StockAdjustmentProductMark::toCheckDTO).toList();

        result.put(CodeType.MARK, markCodes);

        List<CheckedCodeDTO> aggregationBlockCodes = stockAdjustmentProductAggregationBlockRepository.findAllByStockAdjustmentProductIdAndDeletedIsFalse(stockProductId).stream().map(StockAdjustmentProductAggregationBlock::toCheckDTO).toList();

        result.put(CodeType.BLOCK, aggregationBlockCodes);

        return result;
    }

    public List<StockAdjustmentAggregationCodesDTO> getCodes(Long stockAdjustmentId) {
        return stockAdjustmentRepository.findById(stockAdjustmentId).map(stockAdjustment -> {
            List<StockAdjustmentAggregationCodesDTO> stockAdjustmentAggregationCodes = new ArrayList<>();
            stockAdjustment.getProducts().forEach(stockAdjustmentProduct -> {
                List<String> codes = new ArrayList<>();
                codes.addAll(stockAdjustmentProduct.getStockAdjustmentProductMarks().stream().map(StockAdjustmentProductMark::getOriginalCode).toList());
                codes.addAll(stockAdjustmentProduct.getStockAdjustmentProductAggregations().stream().map(StockAdjustmentProductAggregation::getOriginalCode).toList());
                codes.addAll(stockAdjustmentProduct.getStockAdjustmentProductAggregationBlocks().stream().map(StockAdjustmentProductAggregationBlock::getOriginalCode).toList());
                StockAdjustmentAggregationCodesDTO stockAdjustmentAggregationCodesDTO = new StockAdjustmentAggregationCodesDTO();
                stockAdjustmentAggregationCodesDTO.setVatBarcode(Optional.ofNullable(stockAdjustmentProduct.getProduct()).map(Product::getVatBarcode).orElse(null));
                stockAdjustmentAggregationCodesDTO.setCodes(codes);
                stockAdjustmentAggregationCodes.add(stockAdjustmentAggregationCodesDTO);
            });
            return stockAdjustmentAggregationCodes;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(StockAdjustment.class.getSimpleName(), "id", stockAdjustmentId));
    }
}
