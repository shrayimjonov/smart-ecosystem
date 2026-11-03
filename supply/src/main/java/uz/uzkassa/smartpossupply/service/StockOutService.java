package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import kotlin.collections.ArrayDeque;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.category.Category;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.factura.Contract;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.domain.order.OrderProduct;
import uz.uzkassa.smartpossupply.domain.pricelist.PriceList;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.product.ProductContract;
import uz.uzkassa.smartpossupply.domain.receipt.Receipt;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregation;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregationBlock;
import uz.uzkassa.smartpossupply.domain.stock.StockProductMark;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseExpeditor;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.category.EDocsGenerateDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerWithLimitDTO;
import uz.uzkassa.smartpossupply.dto.customer.TerminalCustomerListDTO;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.dto.order.OrderStatusUpdateDTO;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalStockOutStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalTopProductsStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductWaybillDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.dto.stock.CommonStockStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockWaybillCreateDTO;
import uz.uzkassa.smartpossupply.dto.stockout.MobileStockOutProductDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutListDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutLookUpDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutProductDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutProductDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockout.TerminalStockOutDTO;
import uz.uzkassa.smartpossupply.dto.stockout.TerminalStockOutProductDTO;
import uz.uzkassa.smartpossupply.dto.stockout.TerminalStockOutProductDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockout.TerminalStockOutProductUpdateDTO;
import uz.uzkassa.smartpossupply.dto.stockout.terminal.TerminalStockOutDetailDTO;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FinancialOperationType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TemplateType;
import uz.uzkassa.smartpossupply.enums.TemplateTypeAlias;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.enums.Vat;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.filters.StockProductFilter;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqNotificationProducer;
import uz.uzkassa.smartpossupply.repository.CategoryRepository;
import uz.uzkassa.smartpossupply.repository.CompanyCommittentWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.ContractRepository;
import uz.uzkassa.smartpossupply.repository.CustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.FacturaRepository;
import uz.uzkassa.smartpossupply.repository.FinancialOperationRepository;
import uz.uzkassa.smartpossupply.repository.OrderProductRepository;
import uz.uzkassa.smartpossupply.repository.OrderRepository;
import uz.uzkassa.smartpossupply.repository.PartnerRepository;
import uz.uzkassa.smartpossupply.repository.PriceListRepository;
import uz.uzkassa.smartpossupply.repository.ProductContractRepository;
import uz.uzkassa.smartpossupply.repository.ProductContractWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.repository.ProductSerialWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.ReceiptRepository;
import uz.uzkassa.smartpossupply.repository.StockOutRepository;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationRepository;
import uz.uzkassa.smartpossupply.repository.StockProductMarkRepository;
import uz.uzkassa.smartpossupply.repository.StockProductRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseExpeditorRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseProductRepository;
import uz.uzkassa.smartpossupply.service.handlers.csv.StockOutDetailCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.StockOutDetailExcelHandler;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.validator.ValidationContext;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class StockOutService extends BaseService {

    StockOutRepository stockOutRepository;
    StockProductRepository stockProductRepository;
    OrderProductRepository orderProductRepository;
    WarehouseProductRepository warehouseProductRepository;
    ProductContractWarehouseRepository productContractWarehouseRepository;
    ProductSerialWarehouseRepository productSerialWarehouseRepository;
    CompanyCommittentWarehouseRepository companyCommittentWarehouseRepository;
    ContractRepository contractRepository;
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    StockProductAggregationRepository stockProductAggregationRepository;
    StockProductMarkRepository stockProductMarkRepository;
    StockProductAggregationBlockRepository stockProductAggregationBlockRepository;
    FacturaRepository facturaRepository;
    OrderRepository orderRepository;
    OrderService orderService;
    PriceListRepository priceListRepository;
    CustomerBranchRepository customerBranchRepository;
    ProductContractRepository productContractRepository;
    ProductService productService;
    StockOutDetailExcelHandler stockOutDetailExcelHandler;
    StockOutDetailCsvHandler stockOutDetailCsvHandler;
    CisInfoService cisInfoService;
    WarehouseProductService warehouseProductService;
    PartnerRepository partnerRepository;
    FinancialOperationRepository financialOperationRepository;
    FinancialOperationService financialOperationService;
    ReceiptRepository receiptRepository;
    WarehouseExpeditorRepository warehouseExpeditorRepository;
    EDocService eDocService;
    PackageQuantityService packageQuantityService;
    NumberingContext numberingContext;
    RabbitMqNotificationProducer notificationProducer;
    ValidationContext validationContext;

    @Transactional
    public Long create(StockOutDTO stockOutDTO) {
        validationContext.validate(stockOutDTO);
        try {
            log.info("STOCK OUT CREATE: {}", objectMapper.writeValueAsString(stockOutDTO));
        } catch (JsonProcessingException e) {
            log.info("Writing object error: {}", e.getMessage());
        }
        if (stockOutDTO.getOrderId() != null) {
            stockOutRepository.findFirstByOrderIdAndTransferTypeAndStatusIsNotAndDeletedIsFalse(stockOutDTO.getOrderId(), TransferType.OUT, StockStatus.CANCELLED).ifPresent(stock -> {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ENTITY_ALREADY_CREATED_BY, new Object[]{
                        localizationService.localize(MessageConstants.STOCK_OUTS), localizationService.localize(MessageConstants.ORDERS),
                        stock.getStockNumber()
                })).get();
            });
        }

        if (stockOutDTO.getStockInId() != null) {
            stockOutRepository.findFirstByStockInIdAndDeletedIsFalse(stockOutDTO.getStockInId()).ifPresent(stockIn -> {
                throw validationService.badRequestExceptionThrow("Отгрузка по данному приходу " + stockIn.getStockNumber() + " уже создана").get();
            });
        }
        Company company = getCurrentCompany();
        Unit unit = getUnit();
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_OUT, company.getId());

        Stock stock = new Stock();
        stock.setTransferType(TransferType.OUT);
        stock.setTransferDate(Optional.ofNullable(stockOutDTO.getTransferDate()).orElse(LocalDateTime.now()));
        stock.setStockNumber(numberingMap.get(NUMBER));
        stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stock.setStockInId(stockOutDTO.getStockInId());
        stock.setContractId(stockOutDTO.getContractId());
        stock.setDescription(stockOutDTO.getDescription());
        stock.setWarehouseId(stockOutDTO.getWarehouseId());
        stock.setBranchId(stockOutDTO.getBranchId());
        stock.setVehicleId(stockOutDTO.getVehicleId());
        stock.setCustomerId(stockOutDTO.getCustomerId());
        stock.setSupplierId(company.getId());
        stock.setCompanyId(company.getId());
        stock.setPriceListId(stockOutDTO.getPriceListId());
        stock.setDeliveryBranchId(stockOutDTO.getDeliveryBranchId());
        stock.setPaymentType(stockOutDTO.getPaymentType());

        stock.setTotalQty(stockOutDTO.getTotalQty());
        stock.setTotalPrice(stockOutDTO.getTotalPrice());
        stock.setTotalForeignPrice(stockOutDTO.getTotalForeignPrice());
        stock.setExchangeRate(stockOutDTO.getExchangeRate());
        stock.setCurrencyType(stockOutDTO.getCurrencyType());
        stock.setTotalPriceWithoutVat(stockOutDTO.getTotalPriceWithoutVat());
        stock.setTotalVatPrice(stockOutDTO.getTotalVatPrice());
        stock.setWithLimit(stockOutDTO.isAllowWithLimit());
        stock.setTotalDiscountPrice(stockOutDTO.getTotalDiscountPrice());
        stock.setTotalBrutto(stockOutDTO.getTotalBrutto());
        stock.setTotalNetto(stockOutDTO.getTotalNetto());

        stockOutRepository.save(stock);

        if (stockOutDTO.getOrderId() != null) {
            Order order = orderRepository.findById(stockOutDTO.getOrderId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", stockOutDTO.getOrderId()));
            order.setStockOutId(stock.getId());
            order.setStockOut(stock);
            order.setStockOutNumber(stock.getStockNumber());
            order.getStockOuts().add(stock);
            orderService.updateStatus(order, OrderStatus.UN_LOADING);

            stock.setOrderId(order.getId());
            stock.setDeliveryBranchId(order.getDeliveryBranchId());
            stock.setAdvancePaymentAmount(order.getAdvancePaymentAmount());
            stock.setAdvancePaymentPercent(order.getAdvancePaymentPercent());
            stock.setSpecificationDate(order.getSpecificationDate());
            stock.setSpecificationNumber(order.getSpecificationNumber());
            stock.setAppType(order.getAppType());
            stockOutRepository.save(stock);
        }

        List<StockOutProductDTO> products = stockOutDTO.getProducts();

        Map<Long, Product> productMap = productRepository.findAllById(products.stream().map(StockOutProductDTO::getId).filter(Objects::nonNull).collect(Collectors.toSet())).stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        Set<Long> productContractIds = products.stream().map(StockOutProductDTO::getProductContractId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, ProductContract> productContractMap = productContractIds.isEmpty() ? new HashMap<>() : productContractRepository.findAllById(productContractIds).stream().collect(Collectors.toMap(ProductContract::getId, Function.identity()));

        Set<Long> stockInProductIds = products.stream().map(StockOutProductDTO::getStockInProductId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, StockProduct> stockInProductMap = stockInProductIds.isEmpty() ? new HashMap<>() : stockProductRepository.findAllById(stockInProductIds).stream().collect(Collectors.toMap(StockProduct::getId, Function.identity()));

        Set<Long> orderProductIds = products.stream().map(StockOutProductDTO::getOrderProductId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, OrderProduct> orderProductMap = orderProductIds.isEmpty() ? new HashMap<>() : orderProductRepository.findAllById(orderProductIds).stream().collect(Collectors.toMap(OrderProduct::getId, Function.identity()));

        List<Long> stockInIdList = new ArrayList<>(stockInProductIds);
        Map<Long, List<StockProductAggregation>> stockInAggregationMap = stockInIdList.isEmpty() ? new HashMap<>() : stockProductAggregationRepository.findAllByStockProductIdInAndDeletedIsFalseAndParentIsTrue(stockInIdList).stream().collect(Collectors.groupingBy(StockProductAggregation::getStockProductId));
        Map<Long, List<StockProductMark>> stockInMarkMap = stockInIdList.isEmpty() ? new HashMap<>() : stockProductMarkRepository.findAllByStockProductIdInAndDeletedIsFalseAndParentIsTrue(stockInIdList).stream().collect(Collectors.groupingBy(StockProductMark::getStockProductId));
        Map<Long, List<StockProductAggregationBlock>> stockInAggregationBlockMap = stockInIdList.isEmpty() ? new HashMap<>() : stockProductAggregationBlockRepository.findAllByStockProductIdInAndDeletedIsFalseAndParentIsTrue(stockInIdList).stream().collect(Collectors.groupingBy(StockProductAggregationBlock::getStockProductId));

        int sOrder = 0;
        boolean hasDiscount = false;
        for (StockOutProductDTO stockOutProductDTO : products) {
            Product product = productMap.get(stockOutProductDTO.getId());
            if (product == null) {
                throw validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockOutProductDTO.getId()).get();
            }

            if (stockOutProductDTO.getDiscountId() == null && (stockOutProductDTO.getSellingPriceWithoutVat() == null || stockOutProductDTO.getTotalSellingPriceWithoutVat() == null)) {
                throw validationService.badRequestExceptionThrow("Цена продажи товара без НДС не может быть нулевой", "SellingPriceWithoutVat or  TotalSellingPriceWithoutVat is null").get();
            }

            if (stockOutProductDTO.getProductType() != null && product.getProductType() == null) {
                product.setProductType(stockOutProductDTO.getProductType());
                product.setHasMark(true);
                productRepository.save(product);
            }

            StockProduct stockProduct = new StockProduct();
            stockProduct.setProductId(product.getId());
            stockProduct.setProduct(product);
            stockProduct.setWarehouseId(stockOutProductDTO.getWarehouseId());
            stockProduct.setQty(stockOutProductDTO.getQty());
            stockProduct.setQtyInPackage(stockOutProductDTO.getQtyInPackage());
            stockProduct.setRequestedQty(stockOutProductDTO.getRequestedQty());
            stockProduct.setUnitId(unit.getId());
            stockProduct.setSOrder(++sOrder);
            stockProduct.setDiscountProductUniqueId(stockOutProductDTO.getDiscountProductUniqueId());
            stockProduct.setParentDiscountProductUniqueId(stockOutProductDTO.getParentDiscountProductUniqueId());

            stockProduct.setSellingPrice(stockOutProductDTO.getSellingPrice());
            stockProduct.setSellingForeignPrice(stockOutProductDTO.getSellingForeignPrice());
            stockProduct.setSellingPriceWithoutVat(stockOutProductDTO.getSellingPriceWithoutVat());
            stockProduct.setTotalSellingPrice(stockOutProductDTO.getTotalSellingPrice());
            stockProduct.setTotalSellingForeignPrice(stockOutProductDTO.getTotalSellingForeignPrice());
            stockProduct.setTotalSellingPriceWithoutVat(stockOutProductDTO.getTotalSellingPriceWithoutVat());

            stockProduct.setVatRate(stockOutProductDTO.getVatRate());
            stockProduct.setVatPrice(stockOutProductDTO.getVatPrice());
            stockProduct.setTotalVatPrice(stockOutProductDTO.getTotalVatPrice());
            stockProduct.setPriceListPrice(stockOutProductDTO.getPriceListPrice());
            stockProduct.setExciseRate(stockOutProductDTO.getExciseRate());
            stockProduct.setExciseSum(stockOutProductDTO.getExciseSum());

            stockProduct.setMarkupPrice(stockOutProductDTO.getMarkupPrice());
            stockProduct.setMarkupPercent(stockOutProductDTO.getMarkupPercent());
            stockProduct.setBasePrice(stockOutProductDTO.getBasePrice());
            stockProduct.setCompanyCommittentId(stockOutProductDTO.getCompanyCommittentId());
            stockProduct.setProductSerialId(stockOutProductDTO.getProductSerialId());
            stockProduct.setScannedQty(stockOutProductDTO.getScannedQty());
            stockProduct.setDiscountId(stockOutProductDTO.getDiscountId());
            stockProduct.setDiscountItemProductId(stockOutProductDTO.getDiscountItemProductId());
            stockProduct.setTotalDiscountPrice(stockOutProductDTO.getTotalDiscountPrice());
            stockProduct.setDiscountPercent(stockOutProductDTO.getDiscountPercent());
            stockProduct.setDiscountPrice(stockOutProductDTO.getDiscountPrice());
            if (StringUtils.isNotEmpty(stockOutProductDTO.getPackageCode())) {
                stockProduct.setPackageCode(stockOutProductDTO.getPackageCode());
                stockProduct.setPackageName(stockOutProductDTO.getPackageName());
                if (!stockOutProductDTO.getPackageCode().equals(stockProduct.getProduct().getPackageCode())) {
                    Product globalProduct = stockProduct.getProduct();
                    globalProduct.setPackageCode(stockOutProductDTO.getPackageCode());
                    globalProduct.setPackageName(stockOutProductDTO.getPackageName());
                    productRepository.save(globalProduct);
                }
            }

            if (stockOutProductDTO.getProductContractId() != null) {
                ProductContract productContract = productContractMap.get(stockOutProductDTO.getProductContractId());
                if (productContract == null) {
                    throw validationService.entityNotFoundExceptionThrow(ProductContract.class.getSimpleName(), "id", stockOutProductDTO.getProductContractId()).get();
                }
                productContractRepository.save(productContract);
                stockProduct.setProductContractId(productContract.getId());
                stockProduct.setProductContract(productContract);
            }

            if (stockOutProductDTO.getStockInProductId() != null) {
                StockProduct stockInProduct = stockInProductMap.get(stockOutProductDTO.getStockInProductId());
                if (stockInProduct == null) {
                    throw validationService.entityNotFoundExceptionThrow(StockProduct.class.getSimpleName(), "id", stockOutProductDTO.getStockInProductId()).get();
                }
                stockProduct.setName(stockInProduct.getName());
                stockProduct.setCompanyCommittentId(stockInProduct.getCompanyCommittentId());
                stockProduct.setLgotaVatSum(stockInProduct.getLgotaVatSum());
                stockProduct.setProductSerialId(stockInProduct.getProductSerialId());
                stockProduct.setMarkupPrice(stockInProduct.getMarkupPrice());
                stockProduct.setMarkupPercent(stockInProduct.getMarkupPercent());
                stockProduct.setBasePrice(stockInProduct.getBasePrice());
                stockProduct.setExpiryDate(stockInProduct.getExpiryDate());
                stockProduct.setCode1C(stockInProduct.getCode1C());
            } else if (stockOutProductDTO.getOrderProductId() != null) {
                OrderProduct orderProduct = orderProductMap.get(stockOutProductDTO.getOrderProductId());
                if (orderProduct == null) {
                    throw validationService.entityNotFoundExceptionThrow(OrderProduct.class.getSimpleName(), "id", stockOutProductDTO.getOrderProductId()).get();
                }
                stockProduct.setCompanyCommittentId(orderProduct.getCompanyCommittentId());
                stockProduct.setLgotaVatSum(orderProduct.getLgotaVatSum());
                stockProduct.setProductSerialId(orderProduct.getProductSerialId());
                stockProduct.setMarkupPrice(orderProduct.getMarkupPrice());
                stockProduct.setMarkupPercent(orderProduct.getMarkupPercent());
                stockProduct.setBasePrice(orderProduct.getBasePrice());
                stockProduct.setExpiryDate(orderProduct.getExpiryDate());
                stockProduct.setCode1C(orderProduct.getCode1C());
            }
            hasDiscount = stockProduct.getDiscountId() != null && BigDecimal.valueOf(100).compareTo(Optional.ofNullable(stockProduct.getDiscountPercent()).orElse(BigDecimal.ZERO)) == 0;
            stockProduct.setStockId(stock.getId());
            stockProduct.setTotalBrutto(stockOutProductDTO.getTotalBrutto());
            stockProduct.setTotalNetto(stockOutProductDTO.getTotalNetto());
            stockProduct.setDiscountProductUniqueId(stockOutProductDTO.getDiscountProductUniqueId());
            stockProduct.setParentDiscountProductUniqueId(stockOutProductDTO.getParentDiscountProductUniqueId());
            stockProductRepository.save(stockProduct);
            if (!CollectionUtils.isEmpty(stockOutProductDTO.getPackageQuantities())) {
                packageQuantityService.create(stockProduct, stockOutProductDTO.getPackageQuantities());
            }

            int aggregationCount = 0;
            int markCount = 0;
            int aggregationBlockCount = 0;

            if (!CollectionUtils.isEmpty(stockOutProductDTO.getAggregationCodes()) || stockOutProductDTO.getStockInProductId() != null) {
                if (!CollectionUtils.isEmpty(stockOutProductDTO.getAggregationCodes())) {
                    aggregationCount = aggregationCount + stockOutProductDTO.getAggregationCodes().size();

                    List<StockProductAggregation> stockProductAggregations = new ArrayList<>();
                    for (CheckedCodeDTO checkedStockOutCodeDTO : stockOutProductDTO.getAggregationCodes()) {
                        StockProductAggregation stockProductAggregation = new StockProductAggregation();
                        stockProductAggregation.setStockProductId(stockProduct.getId());
                        stockProductAggregation.setCode(checkedStockOutCodeDTO.getCode());
                        stockProductAggregation.setOriginalCode(checkedStockOutCodeDTO.getOriginalCode());
                        stockProductAggregation.setChecked(checkedStockOutCodeDTO.isChecked());
                        stockProductAggregation.setQty(checkedStockOutCodeDTO.getQty());
                        stockProductAggregation.setProductId(product.getId());
                        stockProductAggregation.setTransferType(TransferType.OUT);
                        stockProductAggregations.add(stockProductAggregation);
                    }
                    stockProductAggregationRepository.saveAll(stockProductAggregations);
                } else {
                    List<CheckedCodeDTO> aggregationCodes = stockInAggregationMap.getOrDefault(stockOutProductDTO.getStockInProductId(), Collections.emptyList()).stream().map(StockProductAggregation::toCheckDTO).toList();
                    aggregationCount = aggregationCount + aggregationCodes.size();

                    List<StockProductAggregation> stockProductAggregations = new ArrayList<>();
                    for (CheckedCodeDTO checkedCodeDTO : aggregationCodes) {
                        StockProductAggregation stockProductAggregation = new StockProductAggregation();
                        stockProductAggregation.setStockProductId(stockProduct.getId());
                        stockProductAggregation.setCode(checkedCodeDTO.getCode());
                        stockProductAggregation.setOriginalCode(checkedCodeDTO.getOriginalCode());
                        stockProductAggregation.setChecked(checkedCodeDTO.isChecked());
                        stockProductAggregation.setQty(checkedCodeDTO.getQty());
                        stockProductAggregation.setProductId(product.getId());
                        stockProductAggregation.setTransferType(TransferType.OUT);
                        stockProductAggregations.add(stockProductAggregation);
                    }

                    stockProductAggregationRepository.saveAll(stockProductAggregations);
                }
            }
            if (!CollectionUtils.isEmpty(stockOutProductDTO.getMarkCodes()) || stockOutProductDTO.getStockInProductId() != null) {
                if (!CollectionUtils.isEmpty(stockOutProductDTO.getMarkCodes())) {
                    markCount = markCount + stockOutProductDTO.getMarkCodes().size();

                    List<StockProductMark> stockProductMarks = new ArrayList<>();
                    for (CheckedCodeDTO checkedStockOutCodeDTO : stockOutProductDTO.getMarkCodes()) {
                        StockProductMark stockProductMark = new StockProductMark();
                        stockProductMark.setStockProductId(stockProduct.getId());
                        stockProductMark.setCode(checkedStockOutCodeDTO.getCode());
                        stockProductMark.setOriginalCode(checkedStockOutCodeDTO.getOriginalCode());
                        stockProductMark.setChecked(checkedStockOutCodeDTO.isChecked());
                        stockProductMark.setQty(checkedStockOutCodeDTO.getQty());
                        stockProductMark.setProductId(product.getId());
                        stockProductMark.setTransferType(TransferType.OUT);
                        stockProductMarks.add(stockProductMark);
                    }

                    stockProductMarkRepository.saveAll(stockProductMarks);
                } else {
                    List<CheckedCodeDTO> markCodes = stockInMarkMap.getOrDefault(stockOutProductDTO.getStockInProductId(), Collections.emptyList()).stream().map(StockProductMark::toCheckDTO).toList();
                    markCount = markCount + markCodes.size();

                    List<StockProductMark> stockProductMarks = new ArrayList<>();
                    for (CheckedCodeDTO checkedCodeDTO : markCodes) {
                        StockProductMark stockProductMark = new StockProductMark();
                        stockProductMark.setStockProductId(stockProduct.getId());
                        stockProductMark.setCode(checkedCodeDTO.getCode());
                        stockProductMark.setOriginalCode(checkedCodeDTO.getOriginalCode());
                        stockProductMark.setChecked(checkedCodeDTO.isChecked());
                        stockProductMark.setQty(checkedCodeDTO.getQty());
                        stockProductMark.setProductId(product.getId());
                        stockProductMark.setTransferType(TransferType.OUT);
                        stockProductMarks.add(stockProductMark);
                    }
                    stockProductMarkRepository.saveAll(stockProductMarks);
                }
            }

            if (!CollectionUtils.isEmpty(stockOutProductDTO.getAggregationBlockCodes()) || stockOutProductDTO.getStockInProductId() != null) {
                if (!CollectionUtils.isEmpty(stockOutProductDTO.getAggregationBlockCodes())) {
                    aggregationBlockCount = aggregationBlockCount + stockOutProductDTO.getAggregationBlockCodes().size();

                    List<StockProductAggregationBlock> stockProductAggregationBlocks = new ArrayList<>();
                    for (CheckedCodeDTO checkedStockOutCodeDTO : stockOutProductDTO.getAggregationBlockCodes()) {
                        StockProductAggregationBlock stockProductAggregationBlock = new StockProductAggregationBlock();
                        stockProductAggregationBlock.setStockProductId(stockProduct.getId());
                        stockProductAggregationBlock.setCode(checkedStockOutCodeDTO.getCode());
                        stockProductAggregationBlock.setOriginalCode(checkedStockOutCodeDTO.getOriginalCode());
                        stockProductAggregationBlock.setChecked(checkedStockOutCodeDTO.isChecked());
                        stockProductAggregationBlock.setQty(checkedStockOutCodeDTO.getQty());
                        stockProductAggregationBlock.setProductId(product.getId());
                        stockProductAggregationBlock.setTransferType(TransferType.OUT);
                        stockProductAggregationBlocks.add(stockProductAggregationBlock);
                    }

                    stockProductAggregationBlockRepository.saveAll(stockProductAggregationBlocks);
                } else {
                    List<CheckedCodeDTO> aggregationBlockCodes = stockInAggregationBlockMap.getOrDefault(stockOutProductDTO.getStockInProductId(), Collections.emptyList()).stream().map(StockProductAggregationBlock::toCheckDTO).toList();
                    aggregationBlockCount = aggregationBlockCount + aggregationBlockCodes.size();

                    List<StockProductAggregationBlock> stockProductAggregationBlocks = new ArrayList<>();
                    for (CheckedCodeDTO checkedCodeDTO : aggregationBlockCodes) {
                        StockProductAggregationBlock stockProductAggregationBlock = new StockProductAggregationBlock();
                        stockProductAggregationBlock.setStockProductId(stockProduct.getId());
                        stockProductAggregationBlock.setCode(checkedCodeDTO.getCode());
                        stockProductAggregationBlock.setOriginalCode(checkedCodeDTO.getOriginalCode());
                        stockProductAggregationBlock.setChecked(checkedCodeDTO.isChecked());
                        stockProductAggregationBlock.setQty(checkedCodeDTO.getQty());
                        stockProductAggregationBlock.setProductId(product.getId());
                        stockProductAggregationBlock.setTransferType(TransferType.OUT);
                        stockProductAggregationBlocks.add(stockProductAggregationBlock);
                    }

                    stockProductAggregationBlockRepository.saveAll(stockProductAggregationBlocks);
                }
            }

            if (aggregationCount > 0 || markCount > 0 || aggregationBlockCount > 0) {
                stockProduct.setHasMarking(true);
                stockProduct.setAggregationQty(aggregationCount);
                stockProduct.setMarkQty(markCount);
                stockProduct.setAggregationBlockQty(aggregationBlockCount);
                stockProductRepository.save(stockProduct);
            }
        }
        stock.setHasDiscount(hasDiscount);
        return stock.getId();
    }

    @Transactional
    public Long create(TerminalStockOutDTO stockOutDTO) {

        User currentUser = getCurrentUser();

        Long branchId = currentUser.getBranchId();

        Long warehouseId = currentUser.getWarehouseId();

        if (branchId == null) {
            throw validationService.badRequestExceptionThrow("Branch is required").get();
        }
        if (warehouseId == null) {
            throw validationService.badRequestExceptionThrow("Warehouse is required").get();
        }

        WarehouseExpeditor warehouseExpeditor = warehouseExpeditorRepository.findFirstByExpeditorIdAndWarehouseIdOrderByTransferDateDesc(getCurrentUserId(), warehouseId).orElse(null);
        if (warehouseExpeditor != null) {
            warehouseId = warehouseExpeditor.getCarId();
        }
        Company company = getCurrentCompany();
        Unit unit = getUnit();
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_OUT, company.getId());

        Stock stock = new Stock();
        stock.setTransferType(TransferType.OUT);
        stock.setTransferDate(LocalDateTime.now());
        stock.setStockNumber(numberingMap.get(NUMBER));
        stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stock.setContractId(stockOutDTO.getContractId());
        stock.setDescription(stockOutDTO.getDescription());
        stock.setWarehouseId(warehouseId);
        stock.setBranchId(branchId);
        stock.setCustomerId(stockOutDTO.getCustomerId());
        stock.setSupplierId(company.getId());
        stock.setCompanyId(company.getId());
        stock.setDeliveryBranchId(stockOutDTO.getDeliveryBranchId());
        stock.setPaymentType(stockOutDTO.getPaymentType());

        BigDecimal totalSellingPrice = BigDecimal.ZERO;
        BigDecimal totalSellingPriceWithoutVat = BigDecimal.ZERO;
        BigDecimal totalVatPrice = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalDiscountPrice = BigDecimal.ZERO;
        boolean hasDiscount = false;

        stockOutRepository.save(stock);

        for (TerminalStockOutProductDTO stockOutProductDTO : stockOutDTO.getProducts()) {
            Product product = productRepository.findById(stockOutProductDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockOutProductDTO.getId()));

            BigDecimal sellingPrice = Optional.ofNullable(stockOutProductDTO.getSellingPrice()).orElse(BigDecimal.ZERO);
            BigDecimal calculationSellingPrice = sellingPrice.subtract(Optional.ofNullable(stockOutProductDTO.getDiscountPrice()).orElse(BigDecimal.ZERO));
            BigDecimal vatPrice = calculationSellingPrice.multiply(Optional.ofNullable(stockOutProductDTO.getVatRate()).orElse(BigDecimal.ZERO)).divide(BigDecimal.valueOf(100).add(Optional.ofNullable(stockOutProductDTO.getVatRate()).orElse(BigDecimal.ZERO)), RoundingMode.HALF_EVEN);
            BigDecimal sellingPriceWithoutVat = calculationSellingPrice.subtract(vatPrice);
            BigDecimal qty = Optional.ofNullable(stockOutProductDTO.getQty()).orElse(BigDecimal.ZERO);
            BigDecimal discountPrice = Optional.ofNullable(stockOutProductDTO.getDiscountPrice()).orElse(BigDecimal.ZERO);

            StockProduct stockProduct = new StockProduct();
            stockProduct.setProductId(product.getId());
            stockProduct.setProduct(product);
            stockProduct.setQty(stockOutProductDTO.getQty());
            stockProduct.setQtyInPackage(product.getQtyInPackage());
            stockProduct.setQtyInBox(product.getQtyInBox());
            stockProduct.setQtyInPallet(product.getQtyInPallet());
            stockProduct.setUnitId(unit.getId());
            stockProduct.setDiscountItemProductId(stockOutProductDTO.getDiscountItemProductId());
            stockProduct.setDiscountId(stockOutProductDTO.getDiscountId());
            stockProduct.setDiscountProductUniqueId(stockOutProductDTO.getDiscountProductUniqueId());
            stockProduct.setParentDiscountProductUniqueId(stockOutProductDTO.getParentDiscountProductUniqueId());

            stockProduct.setSellingPrice(sellingPrice);
            stockProduct.setSellingPriceWithoutVat(sellingPriceWithoutVat);
            stockProduct.setTotalSellingPrice(sellingPrice.multiply(qty));
            stockProduct.setTotalSellingPriceWithoutVat(sellingPriceWithoutVat.multiply(qty));
            stockProduct.setDiscountPrice(discountPrice);
            stockProduct.setDiscountPercent(stockOutProductDTO.getDiscountPercent());
            stockProduct.setTotalDiscountPrice(discountPrice.multiply(qty));

            stockProduct.setVatRate(stockOutProductDTO.getVatRate());
            stockProduct.setVatPrice(vatPrice);
            stockProduct.setTotalVatPrice(vatPrice.multiply(qty));
            stockProduct.setExciseRate(stockOutProductDTO.getExciseRate());
            stockProduct.setExciseSum(stockOutProductDTO.getExciseSum());

            totalSellingPrice = totalSellingPrice.add(stockProduct.getTotalSellingPrice());
            totalSellingPriceWithoutVat = totalSellingPriceWithoutVat.add(stockProduct.getTotalSellingPriceWithoutVat());
            totalVatPrice = totalVatPrice.add(stockProduct.getTotalVatPrice());
            totalQty = totalQty.add(stockProduct.getQty());
            totalDiscountPrice = totalDiscountPrice.add(stockProduct.getTotalDiscountPrice());

            stockProduct.setStockId(stock.getId());
            hasDiscount = stockProduct.getDiscountId() != null && BigDecimal.valueOf(100).compareTo(Optional.ofNullable(stockProduct.getDiscountPercent()).orElse(BigDecimal.ZERO)) == 0;
            stockProductRepository.save(stockProduct);
        }
        stock.setTotalQty(totalQty);
        stock.setTotalPrice(totalSellingPrice);
        stock.setTotalPriceWithoutVat(totalSellingPriceWithoutVat);
        stock.setTotalVatPrice(totalVatPrice);
        stock.setTotalDiscountPrice(totalDiscountPrice);
        stock.setHasDiscount(hasDiscount);
        stockOutRepository.save(stock);
        return stock.getId();
    }

    @Transactional
    public Long update(Long id, TerminalStockOutDTO stockOutDTO) {
        /*try {
            log.info("TERMINAL STOCK OUT UPDATE: {}", objectMapper.writeValueAsString(stockOutDTO));
        } catch (JsonProcessingException e) {
            log.info("Writing object error: {}", e.getMessage());
        }*/
        Stock stock = stockOutRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        stock.setDescription(stockOutDTO.getDescription());
        stock.setPaymentType(stockOutDTO.getPaymentType());
        BigDecimal totalSellingPrice = BigDecimal.ZERO;
        BigDecimal totalDiscountPrice = BigDecimal.ZERO;
        BigDecimal totalSellingPriceWithoutVat = BigDecimal.ZERO;
        BigDecimal totalVatPrice = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        boolean hasDiscount = false;
        Unit unit = getUnit();
        HashMap<Long, StockProduct> stockProductsMap = new HashMap<>();
        stock.getProducts().forEach(stockProduct -> stockProductsMap.put(stockProduct.getId(), stockProduct));

        for (TerminalStockOutProductDTO stockOutProductDTO : stockOutDTO.getProducts()) {

            BigDecimal sellingPrice = Optional.ofNullable(stockOutProductDTO.getSellingPrice()).orElse(BigDecimal.ZERO);
            BigDecimal calculationSellingPrice = sellingPrice.subtract(Optional.ofNullable(stockOutProductDTO.getDiscountPrice()).orElse(BigDecimal.ZERO));
            BigDecimal vatPrice = calculationSellingPrice.multiply(Optional.ofNullable(stockOutProductDTO.getVatRate()).orElse(BigDecimal.ZERO)).divide(BigDecimal.valueOf(100).add(Optional.ofNullable(stockOutProductDTO.getVatRate()).orElse(BigDecimal.ZERO)), RoundingMode.HALF_EVEN);
            BigDecimal sellingPriceWithoutVat = calculationSellingPrice.subtract(vatPrice);
            BigDecimal qty = Optional.ofNullable(stockOutProductDTO.getQty()).orElse(BigDecimal.ZERO);
            BigDecimal discountPrice = Optional.ofNullable(stockOutProductDTO.getDiscountPrice()).orElse(BigDecimal.ZERO);
            StockProduct stockProduct;
            if (stockOutProductDTO.getStockProductId() != null && stockProductsMap.get(stockOutProductDTO.getStockProductId()) != null) {
                stockProduct = stockProductsMap.get(stockOutProductDTO.getStockProductId());
            } else {
                stockProduct = new StockProduct();
            }

            Product product = productRepository.findById(stockOutProductDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockOutProductDTO.getId()));

            stockProduct.setProductId(product.getId());
            stockProduct.setProduct(product);
            stockProduct.setQty(stockOutProductDTO.getQty());
            stockProduct.setStockId(stock.getId());

            stockProduct.setSellingPrice(sellingPrice);
            stockProduct.setSellingPriceWithoutVat(sellingPriceWithoutVat);
            stockProduct.setTotalSellingPrice(qty.multiply(sellingPrice));
            stockProduct.setTotalSellingPriceWithoutVat(qty.multiply(sellingPriceWithoutVat));

            stockProduct.setVatRate(stockOutProductDTO.getVatRate());
            stockProduct.setDiscountId(stockOutProductDTO.getDiscountId());
            stockProduct.setDiscountItemProductId(stockOutProductDTO.getDiscountItemProductId());
            stockProduct.setDiscountPercent(stockOutProductDTO.getDiscountPercent());
            stockProduct.setDiscountPrice(discountPrice);
            stockProduct.setTotalDiscountPrice(discountPrice.multiply(qty));
            stockProduct.setParentDiscountProductUniqueId(stockOutProductDTO.getParentDiscountProductUniqueId());
            stockProduct.setVatPrice(vatPrice);
            stockProduct.setTotalVatPrice(qty.multiply(vatPrice));
            stockProduct.setExciseRate(stockOutProductDTO.getExciseRate());
            stockProduct.setExciseSum(stockOutProductDTO.getExciseSum());
            stockProduct.setUnitId(unit.getId());
            stockProduct.setDiscountProductUniqueId(stockOutProductDTO.getDiscountProductUniqueId());
            stockProduct.setParentDiscountProductUniqueId(stockOutProductDTO.getParentDiscountProductUniqueId());
            hasDiscount = stockProduct.getDiscountId() != null && BigDecimal.valueOf(100).compareTo(Optional.ofNullable(stockProduct.getDiscountPercent()).orElse(BigDecimal.ZERO)) == 0;

            totalSellingPrice = totalSellingPrice.add(stockProduct.getTotalSellingPrice());
            totalSellingPriceWithoutVat = totalSellingPriceWithoutVat.add(stockProduct.getTotalSellingPriceWithoutVat());
            totalVatPrice = totalVatPrice.add(stockProduct.getTotalVatPrice());
            totalDiscountPrice = totalDiscountPrice.add(stockProduct.getTotalDiscountPrice());
            totalQty = totalQty.add(stockProduct.getQty());

            stockProductRepository.save(stockProduct);
            if (stockProduct.getId() != null) {
                stockProductsMap.remove(stockProduct.getId());
            }
        }
        stock.setTotalQty(totalQty);
        stock.setTotalPrice(totalSellingPrice);
        stock.setTotalPriceWithoutVat(totalSellingPriceWithoutVat);
        stock.setTotalVatPrice(totalVatPrice);
        stock.setHasDiscount(hasDiscount);
        stock.setTotalDiscountPrice(totalDiscountPrice);
        if (!CollectionUtils.isEmpty(stockProductsMap)) {
            List<Long> stockProductIds = new ArrayList<>(stockProductsMap.keySet());
            stockProductRepository.deleteByIds(stockProductIds);
        }
        stockOutRepository.save(stock);
        return id;
    }

    @Transactional
    public Long updateProduct(Long id, List<TerminalStockOutProductUpdateDTO> products) {
        try {
            log.info("TERMINAL STOCK OUT UPDATE: {}", objectMapper.writeValueAsString(products));
        } catch (JsonProcessingException e) {
            log.info("Writing object error: {}", e.getMessage());
        }

        Stock stock = stockOutRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));

        BigDecimal totalSellingPrice = BigDecimal.ZERO;
        BigDecimal totalDiscountPrice = BigDecimal.ZERO;
        BigDecimal totalSellingPriceWithoutVat = BigDecimal.ZERO;
        BigDecimal totalVatPrice = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        boolean hasDiscount = false;
        Unit unit = getUnit();
        HashMap<Long, StockProduct> stockProductsMap = new HashMap<>();
        stock.getProducts().forEach(stockProduct -> stockProductsMap.put(stockProduct.getId(), stockProduct));

        for (TerminalStockOutProductUpdateDTO stockOutProductDTO : products) {

            BigDecimal sellingPrice = Optional.ofNullable(stockOutProductDTO.getPrice()).orElse(BigDecimal.ZERO);
            BigDecimal calculationSellingPrice = sellingPrice.subtract(Optional.ofNullable(stockOutProductDTO.getDiscountPrice()).orElse(BigDecimal.ZERO));
            BigDecimal vatPrice = calculationSellingPrice.multiply(Optional.ofNullable(stockOutProductDTO.getVatRate()).orElse(BigDecimal.ZERO)).divide(BigDecimal.valueOf(100).add(Optional.ofNullable(stockOutProductDTO.getVatRate()).orElse(BigDecimal.ZERO)), RoundingMode.HALF_EVEN);
            BigDecimal sellingPriceWithoutVat = calculationSellingPrice.subtract(vatPrice);
            BigDecimal qty = Optional.ofNullable(stockOutProductDTO.getQty()).orElse(BigDecimal.ZERO);
            BigDecimal discountPrice = BigDecimal.ZERO;
            StockProduct stockProduct;
            if (stockOutProductDTO.getStockOutProductId() != null && stockProductsMap.get(stockOutProductDTO.getStockOutProductId()) != null) {
                stockProduct = stockProductsMap.get(stockOutProductDTO.getStockOutProductId());
            } else {
                stockProduct = new StockProduct();
            }

            Product product = productRepository.findById(stockOutProductDTO.getProductId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockOutProductDTO.getProductId()));

            totalQty = totalQty.add(qty);
            totalSellingPrice = totalSellingPrice.add(sellingPrice.multiply(qty));
            totalSellingPriceWithoutVat = totalSellingPriceWithoutVat.add(sellingPriceWithoutVat.multiply(qty));
            totalVatPrice = totalVatPrice.add(vatPrice.multiply(qty));
            if (stockOutProductDTO.getDiscountId() != null) {
                discountPrice = Optional.ofNullable(stockOutProductDTO.getDiscountPrice()).orElse(BigDecimal.ZERO).multiply(stockOutProductDTO.getQty());
                totalDiscountPrice = totalDiscountPrice.add(discountPrice);
            }

            stockProduct.setProductId(product.getId());
            stockProduct.setProduct(product);
            stockProduct.setQty(stockOutProductDTO.getQty());
            stockProduct.setStockId(stock.getId());

            stockProduct.setSellingPrice(sellingPrice);
            stockProduct.setSellingPriceWithoutVat(sellingPriceWithoutVat);
            stockProduct.setTotalSellingPrice(qty.multiply(sellingPrice));
            stockProduct.setTotalSellingPriceWithoutVat(qty.multiply(sellingPriceWithoutVat));

            stockProduct.setVatRate(stockOutProductDTO.getVatRate());
            stockProduct.setDiscountId(stockOutProductDTO.getDiscountId());
            stockProduct.setDiscountItemProductId(stockOutProductDTO.getDiscountItemProductId());
            stockProduct.setDiscountPercent(stockOutProductDTO.getDiscountPercent());
            stockProduct.setDiscountPrice(stockOutProductDTO.getDiscountPrice());
            stockProduct.setTotalDiscountPrice(discountPrice);
            stockProduct.setDiscountProductUniqueId(stockOutProductDTO.getDiscountProductUniqueId());
            stockProduct.setParentDiscountProductUniqueId(stockOutProductDTO.getParentDiscountProductUniqueId());
            stockProduct.setVatPrice(vatPrice);
            stockProduct.setTotalVatPrice(qty.multiply(vatPrice));
            stockProduct.setExciseRate(stockOutProductDTO.getExciseRate());
            stockProduct.setExciseSum(stockOutProductDTO.getExciseSum());
            stockProduct.setUnitId(unit.getId());
            hasDiscount = stockProduct.getDiscountId() != null && BigDecimal.valueOf(100).compareTo(Optional.ofNullable(stockProduct.getDiscountPercent()).orElse(BigDecimal.ZERO)) == 0;
            stockProductRepository.save(stockProduct);
            if (stockProduct.getId() != null) {
                stockProductsMap.remove(stockProduct.getId());
            }
        }
        stock.setTotalQty(totalQty);
        stock.setTotalPrice(totalSellingPrice);
        stock.setTotalPriceWithoutVat(totalSellingPriceWithoutVat);
        stock.setTotalVatPrice(totalVatPrice);
        stock.setHasDiscount(hasDiscount);
        stock.setTotalDiscountPrice(totalDiscountPrice);

        if (!CollectionUtils.isEmpty(stockProductsMap)) {
            List<Long> stockProductIds = new ArrayList<>(stockProductsMap.keySet());
            stockProductRepository.deleteByIds(stockProductIds);
        }
        stockOutRepository.save(stock);
        return id;
    }

    @Transactional
    public Long update(Long id, StockOutDTO stockOutDTO) {
        validationContext.validate(stockOutDTO);
        Stock stock = stockOutRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        this.validateCompany(stock.getCompanyId());
        stock.setTransferDate(stockOutDTO.getTransferDate());
        stock.setCustomerId(stockOutDTO.getCustomerId());
        stock.setVehicleId(stockOutDTO.getVehicleId());
        stock.setContractId(stockOutDTO.getContractId());
        stock.setDescription(stockOutDTO.getDescription());
        stock.setWarehouseId(stockOutDTO.getWarehouseId());
        stock.setWithLimit(stockOutDTO.isAllowWithLimit());
        stock.setPaymentType(stockOutDTO.getPaymentType());
        stock.setTotalBrutto(stockOutDTO.getTotalBrutto());
        stock.setTotalNetto(stockOutDTO.getTotalNetto());
        stock.setTotalQty(stockOutDTO.getTotalQty());
        stock.setTotalPrice(stockOutDTO.getTotalPrice());
        stock.setTotalForeignPrice(stockOutDTO.getTotalForeignPrice());
        stock.setExchangeRate(stockOutDTO.getExchangeRate());
        stock.setCurrencyType(stockOutDTO.getCurrencyType());
        stock.setTotalPriceWithoutVat(stockOutDTO.getTotalPriceWithoutVat());
        stock.setTotalVatPrice(stockOutDTO.getTotalVatPrice());
        stock.setTotalDiscountPrice(stockOutDTO.getTotalDiscountPrice());

        if (stockOutDTO.getPriceListId() != null) {
            PriceList priceList = priceListRepository.findById(stockOutDTO.getPriceListId()).orElseThrow(validationService.entityNotFoundExceptionThrow(PriceList.class.getSimpleName(), "id", stockOutDTO.getPriceListId()));
            stock.setPriceList(priceList);
            stock.setPriceListId(priceList.getId());
        } else {
            stock.setPriceList(null);
            stock.setPriceListId(null);
        }

        if (stockOutDTO.getDeliveryBranchId() != null) {
            CustomerBranch deliveryBranch = customerBranchRepository.findById(stockOutDTO.getDeliveryBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CustomerBranch.class.getSimpleName(), "id", stockOutDTO.getDeliveryBranchId()));
            stock.setDeliveryBranch(deliveryBranch);
            stock.setDeliveryBranchId(deliveryBranch.getId());
        } else {
            stock.setDeliveryBranch(null);
            stock.setDeliveryBranchId(null);
        }

        if (stockOutDTO.getOrderId() != null && (stock.getOrder() == null || !stockOutDTO.getOrderId().equals(stock.getOrder().getId()))) {
            if (stock.getOrder() != null) {
                orderService.updateStatus(stock.getOrder(), OrderStatus.DRAFT);
            }
            Order order = orderRepository.findById(stockOutDTO.getOrderId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", stockOutDTO.getOrderId()));
            orderService.updateStatus(order, OrderStatus.COMPLETED);
            stock.setOrder(order);
            stock.setOrderId(order.getId());

            stock.setDeliveryBranch(order.getDeliveryBranch());
            stock.setDeliveryBranchId(order.getDeliveryBranchId());

            order.setStockOutId(stock.getId());
            order.setStockOut(stock);
            order.setStockOutNumber(stock.getStockNumber());
            orderRepository.save(order);
        }

        if (stockOutDTO.getStockNumber() != null && !stockOutDTO.getStockNumber().equals(stock.getStockNumber())) {
            if (stockOutRepository.existsStockByStockNumberAndCompanyIdAndTransferTypeAndIdNot(stockOutDTO.getStockNumber(), stock.getCompanyId(), stock.getTransferType(), stock.getId())) {
                throw validationService.conflictExceptionThrow(localizationService.localize(MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM, new Object[]{localizationService.localize(MessageConstants.STOCK_OUT), localizationService.localize(MessageConstants.NUMBER), stockOutDTO.getStockNumber(),})).get();
            }
            stock.setStockNumber(stockOutDTO.getStockNumber());
        }

        Category category = categoryRepository.findFirstByMainIsTrueAndBranchIdAndDeletedIsFalse(stockOutDTO.getBranchId()).orElse(null);
        if (category == null) {
            category = categoryRepository.findFirstByBranchIdAndDeletedIsFalse(stockOutDTO.getBranchId()).orElseThrow(validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.BEFORE_STOCK_IN_SYNC_YOUR_CATALOG)));
        }

        Unit unit = getUnit();

        int sOrder = 0;
        boolean hasDiscount = false;
        HashMap<Long, StockProduct> stockProductsMap = new HashMap<>();
        stock.getProducts().forEach(stockProduct -> stockProductsMap.put(stockProduct.getId(), stockProduct));

        for (StockOutProductDTO stockOutProductDTO : stockOutDTO.getProducts()) {
            StockProduct stockProduct;
            if (stockOutProductDTO.getStockProductId() != null && stockProductsMap.get(stockOutProductDTO.getStockProductId()) != null) {
                stockProduct = stockProductsMap.get(stockOutProductDTO.getStockProductId());
            } else {
                stockProduct = new StockProduct();
            }

            Product product;
            if (stockOutProductDTO.getId() != null && stockOutProductDTO.getId() > 0) {
                product = productRepository.findById(stockOutProductDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockOutProductDTO.getId()));
            } else {
                product = productService.getProductByBarcodeAndVatBarcode(stockOutProductDTO.getBarcode(), stockOutProductDTO.getVatBarCode(), stockOutDTO.getBranchId());
            }

            if (product == null) {
                ProductDTO productDTO = ProductDTO
                        .builder()
                        .name(stockOutProductDTO.getName())
                        .vatBarcode(stockOutProductDTO.getVatBarCode())
                        .barcode(stockOutProductDTO.getBarcode())
                        .categoryIds(List.of(category.getId()))
                        .unitId(unit.getId())
                        .companyId(stock.getCompanyId())
                        .branchId(stockOutDTO.getBranchId())
                        .vat(Vat.getByAmount(stockOutProductDTO.getVatRate()))
                        .price(stockOutProductDTO.getSellingPrice())
                        .companyCommittentId(stockOutProductDTO.getCompanyCommittentId())
                        .productType(MarkUtils.getProductType(stock.getCompany().getProductGroup(), stockOutProductDTO.getVatBarCode()))
                        .hasMark(MarkUtils.getProductType(stock.getCompany().getProductGroup(), stockOutProductDTO.getVatBarCode()) != null)
                        .build();
                product = productService.createAndReturn(productDTO);
            }

            if (stockOutProductDTO.getProductType() != null && product.getProductType() == null) {
                product.setProductType(stockOutProductDTO.getProductType());
                product.setHasMark(true);
                productRepository.save(product);
            }

            stockProduct.setProductId(product.getId());
            stockProduct.setProduct(product);
            stockProduct.setSOrder(++sOrder);
            stockProduct.setWarehouseId(stockOutProductDTO.getWarehouseId());
            stockProduct.setQty(stockOutProductDTO.getQty());
            stockProduct.setQtyInPackage(stockOutProductDTO.getQtyInPackage());
            stockProduct.setRequestedQty(stockOutProductDTO.getRequestedQty());
            stockProduct.setUnitId(unit.getId());

            stockProduct.setSellingPrice(stockOutProductDTO.getSellingPrice());
            stockProduct.setSellingForeignPrice(stockOutProductDTO.getSellingForeignPrice());
            stockProduct.setSellingPriceWithoutVat(stockOutProductDTO.getSellingPriceWithoutVat());
            stockProduct.setTotalSellingPrice(stockOutProductDTO.getTotalSellingPrice());
            stockProduct.setTotalSellingForeignPrice(stockOutProductDTO.getTotalSellingForeignPrice());
            stockProduct.setTotalSellingPriceWithoutVat(stockOutProductDTO.getTotalSellingPriceWithoutVat());

            stockProduct.setVatRate(stockOutProductDTO.getVatRate());
            stockProduct.setVatPrice(stockOutProductDTO.getVatPrice());
            stockProduct.setTotalVatPrice(stockOutProductDTO.getTotalVatPrice());
            stockProduct.setPriceListPrice(stockOutProductDTO.getPriceListPrice());
            stockProduct.setExciseRate(stockOutProductDTO.getExciseRate());
            stockProduct.setExciseSum(stockOutProductDTO.getExciseSum());

            stockProduct.setMarkupPrice(stockOutProductDTO.getMarkupPrice());
            stockProduct.setMarkupPercent(stockOutProductDTO.getMarkupPercent());
            stockProduct.setBasePrice(stockOutProductDTO.getBasePrice());
            stockProduct.setCompanyCommittentId(stockOutProductDTO.getCompanyCommittentId());
            stockProduct.setProductSerialId(stockOutProductDTO.getProductSerialId());
            stockProduct.setDiscountProductUniqueId(stockOutProductDTO.getDiscountProductUniqueId());
            stockProduct.setParentDiscountProductUniqueId(stockOutProductDTO.getParentDiscountProductUniqueId());
            stockProduct.setDiscountId(stockOutProductDTO.getDiscountId());
            stockProduct.setDiscountItemProductId(stockOutProductDTO.getDiscountItemProductId());
            stockProduct.setDiscountPrice(stockOutProductDTO.getDiscountPrice());
            stockProduct.setDiscountPercent(stockOutProductDTO.getDiscountPercent());
            stockProduct.setTotalDiscountPrice(stockOutProductDTO.getTotalDiscountPrice());
            stockProduct.setTotalBrutto(stockOutProductDTO.getTotalBrutto());
            stockProduct.setTotalNetto(stockOutProductDTO.getTotalNetto());
            stockProduct.setDiscountProductUniqueId(stockOutProductDTO.getDiscountProductUniqueId());
            stockProduct.setParentDiscountProductUniqueId(stockOutProductDTO.getParentDiscountProductUniqueId());
            if (stockOutProductDTO.getScannedQty() != null) {
                stockProduct.setScannedQty(stockOutProductDTO.getScannedQty());
            }

            stockProduct.setLgotaVatSum(stockOutProductDTO.getLgotaVatSum());
            if (StringUtils.isNotEmpty(stockOutProductDTO.getPackageCode())) {
                stockProduct.setPackageCode(stockOutProductDTO.getPackageCode());
                stockProduct.setPackageName(stockOutProductDTO.getPackageName());
                if (!stockOutProductDTO.getPackageCode().equals(stockProduct.getProduct().getPackageCode())) {
                    Product globalProduct = stockProduct.getProduct();
                    globalProduct.setPackageCode(stockOutProductDTO.getPackageCode());
                    globalProduct.setPackageName(stockOutProductDTO.getPackageName());
                    productRepository.save(globalProduct);
                }
            }

            if (stockOutProductDTO.getProductContractId() != null) {
                ProductContract productContract = productContractRepository.findById(stockOutProductDTO.getProductContractId()).orElseThrow(validationService.entityNotFoundExceptionThrow(ProductContract.class.getSimpleName(), "id", stockOutProductDTO.getProductContractId()));
                stockProduct.setProductContract(productContract);
                stockProduct.setProductContractId(productContract.getId());
                productContractRepository.save(productContract);
            } else {
                stockProduct.setProductContractId(null);
                stockProduct.setProductContract(null);
            }

            stockProduct.setStockId(stock.getId());
            hasDiscount = stockProduct.getDiscountId() != null && BigDecimal.valueOf(100).compareTo(Optional.ofNullable(stockProduct.getDiscountPercent()).orElse(BigDecimal.ZERO)) == 0;

            if (stockProduct.getId() != null) {
                stockProductsMap.remove(stockProduct.getId());
            }
            stockProductRepository.save(stockProduct);
            if (CollectionUtils.isEmpty(stockOutProductDTO.getPackageQuantities())) {
                packageQuantityService.deleteByCompositeId(stockProduct);
            } else {
                packageQuantityService.create(stockProduct, stockOutProductDTO.getPackageQuantities());
            }
            int aggregationCount = 0;
            int markCount = 0;
            int aggregationBlockCount = 0;

            if (stockOutProductDTO.getAggregationCodes() != null) {
                stockProductAggregationRepository.deleteAllByStockProductId(stockProduct.getId());
                if (!CollectionUtils.isEmpty(stockOutProductDTO.getAggregationCodes())) {
                    aggregationCount = aggregationCount + stockOutProductDTO.getAggregationCodes().size();
                    List<StockProductAggregation> stockProductAggregations = new ArrayList<>();
                    for (CheckedCodeDTO checkedStockOutCodeDTO : stockOutProductDTO.getAggregationCodes()) {
                        StockProductAggregation stockProductAggregation = new StockProductAggregation();
                        stockProductAggregation.setStockProductId(stockProduct.getId());
                        stockProductAggregation.setCode(checkedStockOutCodeDTO.getCode());
                        stockProductAggregation.setOriginalCode(checkedStockOutCodeDTO.getOriginalCode());
                        stockProductAggregation.setChecked(checkedStockOutCodeDTO.isChecked());
                        stockProductAggregation.setQty(checkedStockOutCodeDTO.getQty());
                        stockProductAggregation.setProductId(product.getId());
                        stockProductAggregation.setTransferType(TransferType.OUT);
                        stockProductAggregations.add(stockProductAggregation);
                    }

                    stockProductAggregationRepository.saveAll(stockProductAggregations);
                }
            }

            if (stockOutProductDTO.getMarkCodes() != null) {
                stockProductMarkRepository.deleteAllByStockProductId(stockProduct.getId());
                if (!CollectionUtils.isEmpty(stockOutProductDTO.getMarkCodes())) {
                    markCount = markCount + stockOutProductDTO.getMarkCodes().size();
                    List<StockProductMark> stockProductMarks = new ArrayList<>();
                    for (CheckedCodeDTO checkedStockOutCodeDTO : stockOutProductDTO.getMarkCodes()) {
                        StockProductMark stockProductMark = new StockProductMark();
                        stockProductMark.setStockProductId(stockProduct.getId());
                        stockProductMark.setCode(checkedStockOutCodeDTO.getCode());
                        stockProductMark.setOriginalCode(checkedStockOutCodeDTO.getOriginalCode());
                        stockProductMark.setChecked(checkedStockOutCodeDTO.isChecked());
                        stockProductMark.setQty(checkedStockOutCodeDTO.getQty());
                        stockProductMark.setProductId(product.getId());
                        stockProductMark.setTransferType(TransferType.OUT);
                        stockProductMarks.add(stockProductMark);
                    }
                    stockProductMarkRepository.saveAll(stockProductMarks);
                }
            }

            if (stockOutProductDTO.getAggregationBlockCodes() != null) {
                stockProductAggregationBlockRepository.deleteAllByStockProductId(stockProduct.getId());
                if (!CollectionUtils.isEmpty(stockOutProductDTO.getAggregationBlockCodes())) {
                    aggregationBlockCount = aggregationBlockCount + stockOutProductDTO.getAggregationBlockCodes().size();
                    List<StockProductAggregationBlock> stockProductAggregationBlocks = new ArrayList<>();
                    for (CheckedCodeDTO checkedStockOutCodeDTO : stockOutProductDTO.getAggregationBlockCodes()) {
                        StockProductAggregationBlock stockProductAggregationBlock = new StockProductAggregationBlock();
                        stockProductAggregationBlock.setStockProductId(stockProduct.getId());
                        stockProductAggregationBlock.setCode(checkedStockOutCodeDTO.getCode());
                        stockProductAggregationBlock.setOriginalCode(checkedStockOutCodeDTO.getOriginalCode());
                        stockProductAggregationBlock.setChecked(checkedStockOutCodeDTO.isChecked());
                        stockProductAggregationBlock.setQty(checkedStockOutCodeDTO.getQty());
                        stockProductAggregationBlock.setProductId(product.getId());
                        stockProductAggregationBlock.setTransferType(TransferType.OUT);
                        stockProductAggregationBlocks.add(stockProductAggregationBlock);
                    }
                    stockProductAggregationBlockRepository.saveAll(stockProductAggregationBlocks);
                }
            }

            if (stockOutProductDTO.getAggregationCodes() != null || stockOutProductDTO.getMarkCodes() != null || stockOutProductDTO.getAggregationBlockCodes() != null) {
                stockProduct.setHasMarking(aggregationCount > 0 || markCount > 0 || aggregationBlockCount > 0);
                stockProduct.setAggregationQty(aggregationCount);
                stockProduct.setMarkQty(markCount);
                stockProduct.setAggregationBlockQty(aggregationBlockCount);
                stockProductRepository.save(stockProduct);
            }
        }

        if (!CollectionUtils.isEmpty(stockProductsMap)) {
            List<Long> stockProductIds = new ArrayList<>(stockProductsMap.keySet());
            stockProductAggregationRepository.deleteAllByStockProductIds(stockProductIds);
            stockProductMarkRepository.deleteAllByStockProductIds(stockProductIds);
            stockProductAggregationBlockRepository.deleteAllByStockProductIds(stockProductIds);
            stockProductRepository.deleteByIds(stockProductIds);
        }
        stock.setHasDiscount(hasDiscount);

        return id;
    }

    @Transactional
    public Long updateMark(Long stockProductId, MobileStockOutProductDTO mobileStockOutProductDTO) {
        return stockProductRepository.findById(stockProductId).map(stockProduct -> {
            int aggregationCount = 0;
            int markCount = 0;
            int aggregationBlockCount = 0;
            stockProduct.setScannedQty(mobileStockOutProductDTO.getScannedQty());
            if (mobileStockOutProductDTO.getAggregationCodes() != null) {
                stockProductAggregationRepository.deleteAllByStockProductId(stockProduct.getId());
                if (!CollectionUtils.isEmpty(mobileStockOutProductDTO.getAggregationCodes())) {
                    aggregationCount = aggregationCount + mobileStockOutProductDTO.getAggregationCodes().size();
                    List<StockProductAggregation> stockProductAggregations = new ArrayList<>();
                    for (CheckedCodeDTO checkedStockOutCodeDTO : mobileStockOutProductDTO.getAggregationCodes()) {
                        StockProductAggregation stockProductAggregation = new StockProductAggregation();
                        stockProductAggregation.setStockProductId(stockProduct.getId());
                        stockProductAggregation.setCode(checkedStockOutCodeDTO.getCode());
                        stockProductAggregation.setOriginalCode(checkedStockOutCodeDTO.getOriginalCode());
                        stockProductAggregation.setChecked(checkedStockOutCodeDTO.isChecked());
                        stockProductAggregation.setQty(checkedStockOutCodeDTO.getQty());
                        stockProductAggregation.setProductId(stockProduct.getProductId());
                        stockProductAggregation.setTransferType(TransferType.OUT);
                        stockProductAggregations.add(stockProductAggregation);
                    }
                    stockProductAggregationRepository.saveAll(stockProductAggregations);
                }
            }

            if (mobileStockOutProductDTO.getMarkCodes() != null) {
                stockProductMarkRepository.deleteAllByStockProductId(stockProduct.getId());
                if (!CollectionUtils.isEmpty(mobileStockOutProductDTO.getMarkCodes())) {
                    markCount = markCount + mobileStockOutProductDTO.getMarkCodes().size();
                    List<StockProductMark> stockProductMarks = new ArrayList<>();
                    for (CheckedCodeDTO checkedStockOutCodeDTO : mobileStockOutProductDTO.getMarkCodes()) {
                        StockProductMark stockProductMark = new StockProductMark();
                        stockProductMark.setStockProductId(stockProduct.getId());
                        stockProductMark.setCode(checkedStockOutCodeDTO.getCode());
                        stockProductMark.setOriginalCode(checkedStockOutCodeDTO.getOriginalCode());
                        stockProductMark.setChecked(checkedStockOutCodeDTO.isChecked());
                        stockProductMark.setQty(checkedStockOutCodeDTO.getQty());
                        stockProductMark.setProductId(stockProduct.getProductId());
                        stockProductMark.setTransferType(TransferType.OUT);
                        stockProductMarks.add(stockProductMark);
                    }
                    stockProductMarkRepository.saveAll(stockProductMarks);
                }
            }

            if (mobileStockOutProductDTO.getAggregationBlockCodes() != null) {
                stockProductAggregationBlockRepository.deleteAllByStockProductId(stockProduct.getId());
                if (!CollectionUtils.isEmpty(mobileStockOutProductDTO.getAggregationBlockCodes())) {
                    aggregationBlockCount = aggregationBlockCount + mobileStockOutProductDTO.getAggregationBlockCodes().size();
                    List<StockProductAggregationBlock> stockProductAggregationBlocks = new ArrayList<>();
                    for (CheckedCodeDTO checkedStockOutCodeDTO : mobileStockOutProductDTO.getAggregationBlockCodes()) {
                        StockProductAggregationBlock stockProductAggregationBlock = new StockProductAggregationBlock();
                        stockProductAggregationBlock.setStockProductId(stockProduct.getId());
                        stockProductAggregationBlock.setCode(checkedStockOutCodeDTO.getCode());
                        stockProductAggregationBlock.setOriginalCode(checkedStockOutCodeDTO.getOriginalCode());
                        stockProductAggregationBlock.setChecked(checkedStockOutCodeDTO.isChecked());
                        stockProductAggregationBlock.setQty(checkedStockOutCodeDTO.getQty());
                        stockProductAggregationBlock.setProductId(stockProduct.getProductId());
                        stockProductAggregationBlock.setTransferType(TransferType.OUT);
                        stockProductAggregationBlocks.add(stockProductAggregationBlock);
                    }
                    stockProductAggregationBlockRepository.saveAll(stockProductAggregationBlocks);
                }
            }

            if (mobileStockOutProductDTO.getAggregationCodes() != null || mobileStockOutProductDTO.getMarkCodes() != null || mobileStockOutProductDTO.getAggregationBlockCodes() != null) {
                stockProduct.setHasMarking(aggregationCount > 0 || markCount > 0 || aggregationBlockCount > 0);
                stockProduct.setAggregationQty(aggregationCount);
                stockProduct.setMarkQty(markCount);
                stockProduct.setAggregationBlockQty(aggregationBlockCount);
            }
            stockProductRepository.save(stockProduct);

            return stockProductId;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(StockProduct.class.getSimpleName(), "id", stockProductId));
    }

    public Page<StockOutListDTO> getListForTerminal(StockFilter filter) {
        if (isExpeditor()) {
            filter.setCreatorId(SecurityUtils.getCurrentUserId());
            filter.setBranchId(SecurityUtils.getCurrentBranchId());
            WarehouseExpeditor warehouseExpeditor = warehouseExpeditorRepository.findFirstByExpeditorIdAndWarehouseIdOrderByTransferDateDesc(SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentWarehouseId()).orElse(null);
            if (warehouseExpeditor == null) {
                return new PageImpl<>(new ArrayDeque<>(), filter.getSortedPageable(), 0);
            } else {
                filter.setWarehouseId(warehouseExpeditor.getCarId());
            }
        } else if (isCashier()) {
            filter.setCashierId(SecurityUtils.getCurrentUserId());
        }
        return this.getList(filter);
    }

    public Page<StockOutListDTO> getList(StockFilter filter) {
        ResultList<Stock> resultList = stockOutRepository.getResultList(filter);
        List<StockOutListDTO> result = resultList
                .getList()
                .stream()
                .map(stock -> {
                    StockOutListDTO stockOutListDTO = new StockOutListDTO();
                    stockOutListDTO.setId(stock.getId());
                    stockOutListDTO.setStockNumber(stock.getStockNumber());
                    stockOutListDTO.setTransferDate(stock.getTransferDate());
                    if (stock.getOrder() != null) {
                        stockOutListDTO.setOrder(stock.getOrder().toCommonDTO());
                        stockOutListDTO.setOrderDate(stock.getOrder().getOrderDate());
                        if (stock.getOrder().getExpeditor() != null) {
                            stockOutListDTO.setExpeditor(stock.getOrder().getExpeditor().toCommonDTO());
                        }
                        if (stock.getOrder().getPlanRoute() != null) {
                            stockOutListDTO.setPlanRoute(stock.getOrder().getPlanRoute().toCommonDTO());
                        }
                    }
                    stockOutListDTO.setCustomer(stock.getCustomer().toCommonBaseDTO());
                    stockOutListDTO.setBranch(stock.getBranch().toCommonDTO());
                    if (stock.getWarehouse() != null) {
                        stockOutListDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
                    }
                    if (stock.getVehicle() != null) {
                        stockOutListDTO.setVehicle(stock.getVehicle().toLookupDTO());
                    }
                    stockOutListDTO.setFacturas(stock.getFacturas().stream().map(Factura::toLink).collect(Collectors.toSet()));

                    if (stock.getOldStock() != null) {
                        stockOutListDTO.setOldStock(stock.getOldStock().toCommonDTO());
                    }
                    stockOutListDTO.setTotalPrice(stock.getTotalPrice());
                    stockOutListDTO.setTotalForeignPrice(stock.getTotalForeignPrice());
                    stockOutListDTO.setExchangeRate(stock.getExchangeRate());
                    stockOutListDTO.setCurrencyType(stock.getCurrencyType());
                    stockOutListDTO.setTotalDiscountPrice(stock.getTotalDiscountPrice());
                    stockOutListDTO.setTotalQty(stock.getTotalQty());
                    stockOutListDTO.setStatus(stock.getStatus());
                    stockOutListDTO.setLastModifiedDate(stock.getUpdatedDate());
                    stockOutListDTO.setPaymentType(stock.getPaymentType());
                    if (PaymentType.TRANSFER != stock.getPaymentType()) {
                        stockOutListDTO.setReceipt(stock.getReceiptUid());
                        stockOutListDTO.setReceipts(stock.getReceipts().stream().map(Receipt::toLinkDTO).toList());
                    }
                    return stockOutListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<StockOutLookUpDTO> lookUp(StockFilter filter) {
        return stockOutRepository.lookUp(filter)
                .stream()
                .map(stock -> {
                    StockOutLookUpDTO stockOutLookUpDTO = new StockOutLookUpDTO();
                    stockOutLookUpDTO.setId(stock.getId());
                    stockOutLookUpDTO.setName(stock.getStockNumber());
                    if (stock.getFactura() != null) {
                        stockOutLookUpDTO.setFactura(stock.getFactura().toCommonDTO());
                    }
                    stockOutLookUpDTO.setTotalPrice(stock.getTotalPrice());
                    partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(stock.getCompanyId(), stock.getCustomerId(), stock.getCompanyId())
                            .ifPresent(partners -> {
                                stockOutLookUpDTO.setLimitBalance(partners.getLimitBalance());
                                stockOutLookUpDTO.setCurrentBalance(partners.getCurrentBalance());
                            });
                    return stockOutLookUpDTO;
                }).toList();
    }

    public StockOutDetailDTO get(Long id) {
        Stock stock = stockOutRepository.getDetail(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        this.validateCompany(stock.getCompanyId());
        StockOutDetailDTO stockOutDetailDTO = new StockOutDetailDTO();
        stockOutDetailDTO.setId(stock.getId());
        stockOutDetailDTO.setStockNumber(stock.getStockNumber());
        if (stock.getOrder() != null) {
            if (stock.getOrder().getExpeditor() != null) {
                stockOutDetailDTO.setExpeditor(stock.getOrder().getExpeditor().toCommonDTO());
            }
            if (stock.getOrder().getPlanRoute() != null) {
                stockOutDetailDTO.setPlanRoute(stock.getOrder().getPlanRoute().toCommonDTO());
            }
        }
        stockOutDetailDTO.setStatus(stock.getStatus());
        if (stock.getContractId() != null) {
            stockOutDetailDTO.setContract(stock.getContract().toLookUpDTO());
        }
        if (stock.getStockInId() != null) {
            stockOutDetailDTO.setStockIn(stock.getStockIn().toCommonDTO());
        }
        stockOutDetailDTO.setDescription(stock.getDescription());
        partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(stock.getCompanyId(), stock.getCustomerId(), stock.getSupplierId()).ifPresent(partners -> stockOutDetailDTO.setCustomer(new CustomerWithLimitDTO(partners.getCustomer().toCommonBaseDTO(), partners.getLimitBalance(), partners.getCurrentBalance(), partners.isHasLimit(), partners.getCustomer().getPhone(), partners.getCustomer().getBrand())));
        stockOutDetailDTO.setSupplier(stock.getSupplier().toCommonBaseDTO());
        stockOutDetailDTO.setTransferDate(stock.getTransferDate());
        stockOutDetailDTO.setReceipts(stock.getReceipts().stream().map(Receipt::toLinkDTO).toList());
        if (stock.getWarehouse() != null) {
            stockOutDetailDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
        }
        if (stock.getBranchId() != null) {
            stockOutDetailDTO.setBranch(stock.getBranch().toCommonDTO());
        }
        if (stock.getVehicle() != null) {
            stockOutDetailDTO.setVehicle(stock.getVehicle().toLookupDTO());
        }
        if (stock.getFacturaId() != null) {
            stockOutDetailDTO.setFactura(stock.getFactura().toLink());
        }
        if (!CollectionUtils.isEmpty(stock.getFacturas())) {
            stockOutDetailDTO.setFacturas(stock.getFacturas().stream().filter(factura -> !factura.isDeleted()).map(Factura::toLink).collect(Collectors.toSet()));
        }
        if (stock.getOrder() != null) {
            stockOutDetailDTO.setOrder(stock.getOrder().toOrderBaseDTO());
        }
        if (stock.getOldStockId() != null) {
            stockOutDetailDTO.setOldStock(stock.getOldStock().toCommonDTO());
        }
        if (stock.getPriceListId() != null) {
            stockOutDetailDTO.setPriceList(stock.getPriceList().toCommonDTO());
        }
        if (stock.getDeliveryBranchId() != null) {
            stockOutDetailDTO.setDeliveryBranch(stock.getDeliveryBranch().toCommonDTO());
        }
        stockOutDetailDTO.setTotalQty(stock.getTotalQty());
        stockOutDetailDTO.setTotalPrice(stock.getTotalPrice());
        stockOutDetailDTO.setTotalForeignPrice(stock.getTotalForeignPrice());
        stockOutDetailDTO.setExchangeRate(stock.getExchangeRate());
        stockOutDetailDTO.setCurrencyType(stock.getCurrencyType());
        stockOutDetailDTO.setTotalPriceWithoutVat(stock.getTotalPriceWithoutVat());
        stockOutDetailDTO.setTotalVatPrice(stock.getTotalVatPrice());
        if (stock.getOrder() != null) {
            stockOutDetailDTO.setAppType(stock.getOrder().getAppType());
            stockOutDetailDTO.setDeliveryType(stock.getOrder().getDeliveryType());
            if (stock.getOrder().getDeliveryAddress() != null) {
                stockOutDetailDTO.setDeliveryAddress(addressMapper.toDetailDTO(stock.getOrder().getDeliveryAddress()));
            } else if (stock.getOrder().getDeliveryBranch() != null) {
                stockOutDetailDTO.setDeliveryAddress(addressMapper.toDetailDTO(stock.getOrder().getDeliveryBranch().getAddress()));
            }
            stockOutDetailDTO.setPaymentType(stock.getOrder().getPaymentType());
        }
        stockOutDetailDTO.setPaymentType(stock.getPaymentType());
        stockOutDetailDTO.setAllowWithLimit(stock.isWithLimit());
        stockOutDetailDTO.setLastModifiedDate(stock.getUpdatedDate());
        if (StringUtils.isNotEmpty(stock.getReceiptUid())) {
            stockOutDetailDTO.setReceiptUIDs(Arrays.stream(stock.getReceiptUid().split(",")).toList());
        }
        stockOutDetailDTO.setTotalDiscountPrice(stock.getTotalDiscountPrice());
        if (StockStatus.COMPLETED == stock.getStatus()) {
            stockOutDetailDTO.setHasReceipt(receiptRepository.existsByStockOutIdAndDeletedIsFalse(stock.getId()));
        }
        stockOutDetailDTO.setTotalBrutto(stock.getTotalBrutto());
        stockOutDetailDTO.setTotalNetto(stock.getTotalNetto());
        if (stock.getCashierId() != null) {
            stockOutDetailDTO.setCashier(stock.getCashier().toCommonDTO());
        }

        Map<String, List<StockOutProductDetailDTO>> productMap = new LinkedHashMap<>();
        for (StockProduct stockProduct : stock.getProducts()) {
            StockOutProductDetailDTO stockOutProductDetailDTO = new StockOutProductDetailDTO();
            stockOutProductDetailDTO.setStockProductId(stockProduct.getId());
            stockOutProductDetailDTO.setId(stockProduct.getProduct().getId());
            stockOutProductDetailDTO.setName(stockProduct.getProduct().getName());
            stockOutProductDetailDTO.setVatBarCode(stockProduct.getProduct().getVatBarcode());
            stockOutProductDetailDTO.setProductType(stockProduct.getProduct().getProductType());
            stockOutProductDetailDTO.setBarcode(stockProduct.getProduct().getBarcode());
            stockOutProductDetailDTO.setQtyInPackage(stockProduct.getQtyInPackage());
            stockOutProductDetailDTO.setCapacity(stockProduct.getProduct().getCapacity());
            stockOutProductDetailDTO.setHasMark(stockProduct.getProduct().isHasMark());
            stockOutProductDetailDTO.setBoxBarcode(stockProduct.getProduct().getBoxBarcode());
            stockOutProductDetailDTO.setBlockBarcode(stockProduct.getProduct().getBlockBarcode());
            stockOutProductDetailDTO.setBlockProducts(stockProduct.getProduct().getBlockProducts());
            if (stockProduct.getProduct().getParent() != null) {
                stockOutProductDetailDTO.setParent(stockProduct.getProduct().getParent().toProductCommonDTO());
                if (StringUtils.isEmpty(stockOutProductDetailDTO.getBlockBarcode())) {
                    stockOutProductDetailDTO.setBlockBarcode(stockProduct.getProduct().getParent().getBarcode());
                }
            }
            stockOutProductDetailDTO.setScannedQty(stockProduct.getScannedQty());
            stockOutProductDetailDTO.setDiscountProductUniqueId(stockProduct.getDiscountProductUniqueId());
            stockOutProductDetailDTO.setParentDiscountProductUniqueId(stockProduct.getParentDiscountProductUniqueId());
            stockOutProductDetailDTO.setDiscountPrice(stockProduct.getDiscountPrice());
            stockOutProductDetailDTO.setTotalDiscountPrice(stockProduct.getTotalDiscountPrice());
            stockOutProductDetailDTO.setDiscountPercent(stockProduct.getDiscountPercent());
            if (stockProduct.getDiscount() != null) {
                stockOutProductDetailDTO.setDiscount(stockProduct.getDiscount().toListDTO());
                if (stockProduct.getDiscountItemProduct() != null) {
                    stockOutProductDetailDTO.setDiscountItemProduct(stockProduct.getDiscountItemProduct().toCommonDTO());
                    stockOutProductDetailDTO.setDiscountValueType(stockProduct.getDiscountItemProduct().getValueType());
                    stockOutProductDetailDTO.setDiscountLimit(stockProduct.getDiscountItemProduct().getDiscountLimit());
                }
            }
            if (stockProduct.getWarehouse() != null) {
                stockOutProductDetailDTO.setWarehouse(stockProduct.getWarehouse().toCommonDTO());
            }
            stockOutProductDetailDTO.setProductType(stockProduct.getProduct().getProductType());
            stockOutProductDetailDTO.setQty(stockProduct.getQty());

            stockOutProductDetailDTO.setSellingPrice(stockProduct.getSellingPrice());
            stockOutProductDetailDTO.setSellingForeignPrice(stockProduct.getSellingForeignPrice());
            stockOutProductDetailDTO.setSellingPriceWithoutVat(stockProduct.getSellingPriceWithoutVat());
            stockOutProductDetailDTO.setTotalSellingPrice(stockProduct.getTotalSellingPrice());
            stockOutProductDetailDTO.setTotalSellingForeignPrice(stockProduct.getTotalSellingForeignPrice());
            stockOutProductDetailDTO.setTotalSellingPriceWithoutVat(stockProduct.getTotalSellingPriceWithoutVat());

            stockOutProductDetailDTO.setMarkupPercent(stockProduct.getMarkupPercent());
            stockOutProductDetailDTO.setMarkupPrice(stockProduct.getMarkupPrice());
            stockOutProductDetailDTO.setFacturaProfitRate(stockProduct.getFacturaProfitRate());

            stockOutProductDetailDTO.setVatPrice(stockProduct.getVatPrice());
            stockOutProductDetailDTO.setVatRate(stockProduct.getVatRate());
            stockOutProductDetailDTO.setTotalVatPrice(stockProduct.getTotalVatPrice());
            stockOutProductDetailDTO.setExciseRate(stockProduct.getExciseRate());
            stockOutProductDetailDTO.setExciseSum(stockProduct.getExciseSum());

            stockOutProductDetailDTO.setUnit(stockProduct.getUnit().toDTO());
            stockOutProductDetailDTO.setRequestedQty(stockProduct.getRequestedQty());
            stockOutProductDetailDTO.setCatalogPrice(stockProduct.getProduct().getPrice());
            stockOutProductDetailDTO.setCatalogForeignPrice(stockProduct.getProduct().getForeignPrice());
            stockOutProductDetailDTO.setBasePrice(stockProduct.getBasePrice());

            stockOutProductDetailDTO.setHasMarking(stockProduct.isHasMarking());

            stockOutProductDetailDTO.setAggregationQty(stockProduct.getAggregationQty());
            stockOutProductDetailDTO.setMarkQty(stockProduct.getMarkQty());
            stockOutProductDetailDTO.setAggregationBlockQty(stockProduct.getAggregationBlockQty());
            stockOutProductDetailDTO.setTotalBrutto(stockProduct.getTotalBrutto());
            stockOutProductDetailDTO.setTotalNetto(stockProduct.getTotalNetto());

            stockOutProductDetailDTO.setPriceListPrice(stockProduct.getPriceListPrice());
            stockOutProductDetailDTO.setPackageQuantities(packageQuantityService.getByCompositeId(stockProduct));
            if (stockProduct.getProductSerialId() != null) {
                stockOutProductDetailDTO.setProductSerial(stockProduct.getProductSerial().toLookUpDTO());
            }
            if (stockProduct.getCompanyCommittentId() != null) {
                stockOutProductDetailDTO.setCompanyCommittent(stockProduct.getCompanyCommittent().toLookUpDTO());
            }
            if (stockProduct.getProductContractId() != null) {
                stockOutProductDetailDTO.setProductContract(stockProduct.getProductContract().toCommonDTO());
            }
            if (stockProduct.getLgota() != null) {
                stockOutProductDetailDTO.setLgota(stockProduct.getLgota().toLookUpDTO());
            }
            stockOutProductDetailDTO.setLgotaVatSum(stockProduct.getLgotaVatSum());
            if (StringUtils.isNotEmpty(stockProduct.getPackageCode())) {
                stockOutProductDetailDTO.setPackageCode(stockProduct.getPackageCode());
                stockOutProductDetailDTO.setPackageName(stockProduct.getPackageName());
            } else {
                stockOutProductDetailDTO.setPackageName(stockProduct.getProduct().getPackageName());
                stockOutProductDetailDTO.setPackageCode(stockProduct.getProduct().getPackageCode());
            }

            if (!StockStatus.COMPLETED.equals(stock.getStatus())) {
                stockOutProductDetailDTO.setReservationQty(
                        warehouseProductRepository.getReservationQty(
                                Optional.ofNullable(stock.getWarehouseId()).orElse(stockProduct.getWarehouseId()),
                                stockProduct.getProductId(),
                                stockProduct.getUnitId()
                        )
                );
                stockOutProductDetailDTO.setQtyInWarehouse(warehouseProductRepository.getQty(Optional.ofNullable(stock.getWarehouseId()).orElseGet(stockProduct::getWarehouseId), stockProduct.getProductId(), stockProduct.getUnitId()));
                if (stockProduct.getProductContractId() != null) {
                    stockOutProductDetailDTO.setQtyInWarehouseByContract(productContractWarehouseRepository.getQty(Optional.ofNullable(stock.getWarehouseId()).orElseGet(stockProduct::getWarehouseId), stockProduct.getProductContractId()));
                }
                if (stock.getOrder() != null) {
                    stockOutProductDetailDTO.setQtyInOrders(orderService.checkQtyInOrders(stockProduct.getProductId(), stock.getOrder().getId(), stock.getWarehouseId()));
                } else {
                    stockOutProductDetailDTO.setQtyInOrders(orderService.checkQtyInOrders(stockProduct.getProductId(), null, stock.getWarehouseId()));
                }
                if (stockProduct.getProductSerialId() != null) {
                    stockOutProductDetailDTO.setQtyInWarehouseBySerial(productSerialWarehouseRepository.getQty(Optional.ofNullable(stock.getWarehouseId()).orElseGet(stockProduct::getWarehouseId), stockProduct.getProductSerialId()));
                }
                if (stockProduct.getCompanyCommittentId() != null) {
                    stockOutProductDetailDTO.setQtyInWarehouseByCommittent(companyCommittentWarehouseRepository.getQty(Optional.ofNullable(stock.getWarehouseId()).orElseGet(stockProduct::getWarehouseId), stockProduct.getCompanyCommittentId()));
                }
            }
            if (StringUtils.isNotEmpty(stockProduct.getDiscountProductUniqueId())) {
                stockOutDetailDTO.addProduct(stockOutProductDetailDTO);
                productMap.put(stockProduct.getDiscountProductUniqueId(), new ArrayList<>());
            } else if (StringUtils.isNotEmpty(stockProduct.getParentDiscountProductUniqueId())) {
                if (productMap.containsKey(stockProduct.getParentDiscountProductUniqueId())) {
                    productMap.get(stockProduct.getParentDiscountProductUniqueId()).add(stockOutProductDetailDTO);
                } else {
                    productMap.put(stockProduct.getParentDiscountProductUniqueId(), new ArrayList<>(List.of(stockOutProductDetailDTO)));
                }
            } else {
                stockOutDetailDTO.addProduct(stockOutProductDetailDTO);
            }
        }
        if (!CollectionUtils.isEmpty(productMap)) {
            stockOutDetailDTO.getProducts().forEach(orderProductDetailDTO -> {
                if (productMap.containsKey(orderProductDetailDTO.getDiscountProductUniqueId())) {
                    orderProductDetailDTO.getDiscountProducts().addAll(productMap.get(orderProductDetailDTO.getDiscountProductUniqueId()));
                }
            });
        }
        return stockOutDetailDTO;
    }

    public TerminalStockOutDetailDTO getForTerminal(Long id) {
        Stock stock = stockOutRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        this.validateCompany(stock.getCompanyId());
        TerminalStockOutDetailDTO stockOutDetailDTO = new TerminalStockOutDetailDTO();
        stockOutDetailDTO.setId(stock.getId());
        stockOutDetailDTO.setStockNumber(stock.getStockNumber());
        stockOutDetailDTO.setStatus(stock.getStatus());
        if (stock.getContractId() != null) {
            stockOutDetailDTO.setContract(stock.getContract().toLookUpDTO());
        }
        stockOutDetailDTO.setDescription(stock.getDescription());
        partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(stock.getCompanyId(), stock.getCustomerId(), stock.getSupplierId()).ifPresent(partners -> {
            TerminalCustomerListDTO customerListDTO = new TerminalCustomerListDTO();
            customerListDTO.setId(partners.getCustomerId());
            customerListDTO.setTin(partners.getCustomer().getTin());
            customerListDTO.setName(partners.getCustomer().getName());
            customerListDTO.setBrand(partners.getCustomer().getBrand());
            if (partners.getCustomer().getAddress() != null) {
                customerListDTO.setAddress(addressMapper.toDetailDTO(partners.getCustomer().getAddress()));
            }
            customerListDTO.setBalance(partners.getCurrentBalance());
            stockOutDetailDTO.setCustomer(customerListDTO);
        });
        stockOutDetailDTO.setSupplier(stock.getSupplier().toCommonBaseDTO());
        stockOutDetailDTO.setTransferDate(stock.getTransferDate());
        if (stock.getWarehouse() != null) {
            stockOutDetailDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
        }
        if (stock.getBranchId() != null) {
            stockOutDetailDTO.setBranch(stock.getBranch().toCommonDTO());
        }
        if (stock.getFacturaId() != null) {
            stockOutDetailDTO.setFactura(stock.getFactura().toLink());
        }
        if (!CollectionUtils.isEmpty(stock.getFacturas())) {
            stockOutDetailDTO.setFacturas(stock.getFacturas().stream().filter(factura -> !factura.isDeleted()).map(Factura::toLink).collect(Collectors.toSet()));
        }
        if (stock.getOrder() != null) {
            stockOutDetailDTO.setOrder(stock.getOrder().toOrderBaseDTO());
        }

        if (stock.getDeliveryBranchId() != null) {
            stockOutDetailDTO.setDeliveryBranch(stock.getDeliveryBranch().toCommonDTO());
        }
        stockOutDetailDTO.setTotalQty(stock.getTotalQty());
        stockOutDetailDTO.setTotalPrice(stock.getTotalPrice());
        stockOutDetailDTO.setTotalPriceWithoutVat(stock.getTotalPriceWithoutVat());
        stockOutDetailDTO.setTotalVatPrice(stock.getTotalVatPrice());
        stockOutDetailDTO.setTotalDiscountPrice(stock.getTotalDiscountPrice());
        if (stock.getOrder() != null) {
            stockOutDetailDTO.setDeliveryType(stock.getOrder().getDeliveryType());
            if (stock.getOrder().getDeliveryAddress() != null) {
                stockOutDetailDTO.setDeliveryAddress(addressMapper.toDetailDTO(stock.getOrder().getDeliveryAddress()));
            } else if (stock.getOrder().getDeliveryBranch() != null) {
                stockOutDetailDTO.setDeliveryAddress(addressMapper.toDetailDTO(stock.getOrder().getDeliveryBranch().getAddress()));
            }
            stockOutDetailDTO.setPaymentType(stock.getOrder().getPaymentType());
        }
        stockOutDetailDTO.setPaymentType(stock.getPaymentType());
        stockOutDetailDTO.setLastModifiedDate(stock.getUpdatedDate());
        stockOutDetailDTO.setTotalDiscountPrice(stock.getTotalDiscountPrice());
        stockOutDetailDTO.setTotalBrutto(stock.getTotalBrutto());
        stockOutDetailDTO.setTotalNetto(stock.getTotalNetto());
        stockOutDetailDTO.setHasReceipt(receiptRepository.existsByStockOutIdAndDeletedIsFalse(stock.getId()));
        if (stockOutDetailDTO.isHasReceipt()) {
            stockOutDetailDTO.setReceipt(receiptRepository.findFirstByStockOutIdAndDeletedIsFalse(stock.getId()).map(Receipt::toLinkDTO).orElse(null));
        }

        Map<String, List<TerminalStockOutProductDetailDTO>> productMap = new LinkedHashMap<>();

        for (StockProduct stockProduct : stock.getProducts()) {
            TerminalStockOutProductDetailDTO stockOutProductDetailDTO = new TerminalStockOutProductDetailDTO();
            stockOutProductDetailDTO.setStockProductId(stockProduct.getId());
            stockOutProductDetailDTO.setId(stockProduct.getProduct().getId());
            stockOutProductDetailDTO.setName(stockProduct.getProduct().getName());
            stockOutProductDetailDTO.setVatBarCode(stockProduct.getProduct().getVatBarcode());
            stockOutProductDetailDTO.setProductType(stockProduct.getProduct().getProductType());
            stockOutProductDetailDTO.setBarcode(stockProduct.getProduct().getBarcode());
            stockOutProductDetailDTO.setQtyInPackage(stockProduct.getQtyInPackage());
            stockOutProductDetailDTO.setCapacity(stockProduct.getProduct().getCapacity());
            stockOutProductDetailDTO.setHasMark(stockProduct.getProduct().isHasMark());
            stockOutProductDetailDTO.setBoxBarcode(stockProduct.getProduct().getBoxBarcode());
            stockOutProductDetailDTO.setBlockBarcode(stockProduct.getProduct().getBlockBarcode());
            stockOutProductDetailDTO.setBlockProducts(stockProduct.getProduct().getBlockProducts());
            if (stockProduct.getProduct().getParent() != null) {
                stockOutProductDetailDTO.setParent(stockProduct.getProduct().getParent().toProductCommonDTO());
                if (StringUtils.isEmpty(stockOutProductDetailDTO.getBlockBarcode())) {
                    stockOutProductDetailDTO.setBlockBarcode(stockProduct.getProduct().getParent().getBarcode());
                }
            }
            stockOutProductDetailDTO.setScannedQty(stockProduct.getScannedQty());
            if (stockProduct.getWarehouse() != null) {
                stockOutProductDetailDTO.setWarehouse(stockProduct.getWarehouse().toCommonDTO());
            }
            stockOutProductDetailDTO.setProductType(stockProduct.getProduct().getProductType());
            stockOutProductDetailDTO.setQty(stockProduct.getQty());

            stockOutProductDetailDTO.setSellingPrice(stockProduct.getSellingPrice());
            stockOutProductDetailDTO.setSellingPriceWithoutVat(stockProduct.getSellingPriceWithoutVat());
            stockOutProductDetailDTO.setTotalSellingPrice(stockProduct.getTotalSellingPrice());
            stockOutProductDetailDTO.setTotalSellingPriceWithoutVat(stockProduct.getTotalSellingPriceWithoutVat());

            stockOutProductDetailDTO.setVatPrice(stockProduct.getVatPrice());
            stockOutProductDetailDTO.setVatRate(stockProduct.getVatRate());
            stockOutProductDetailDTO.setTotalVatPrice(stockProduct.getTotalVatPrice());
            stockOutProductDetailDTO.setExciseRate(stockProduct.getExciseRate());
            stockOutProductDetailDTO.setExciseSum(stockProduct.getExciseSum());

            stockOutProductDetailDTO.setUnit(stockProduct.getUnit().toDTO());
            stockOutProductDetailDTO.setRequestedQty(stockProduct.getRequestedQty());
            stockOutProductDetailDTO.setCatalogPrice(stockProduct.getProduct().getPrice());

            stockOutProductDetailDTO.setHasMarking(stockProduct.isHasMarking());

            stockOutProductDetailDTO.setAggregationQty(stockProduct.getAggregationQty());
            stockOutProductDetailDTO.setMarkQty(stockProduct.getMarkQty());
            stockOutProductDetailDTO.setAggregationBlockQty(stockProduct.getAggregationBlockQty());
            stockOutProductDetailDTO.setDiscountPercent(stockProduct.getDiscountPercent());
            stockOutProductDetailDTO.setDiscountProductUniqueId(stockProduct.getDiscountProductUniqueId());
            stockOutProductDetailDTO.setParentDiscountProductUniqueId(stockProduct.getParentDiscountProductUniqueId());
            stockOutProductDetailDTO.setDiscountPrice(stockProduct.getDiscountPrice());
            stockOutProductDetailDTO.setTotalDiscountPrice(stockProduct.getTotalDiscountPrice());
            stockOutProductDetailDTO.setTotalBrutto(stockProduct.getTotalBrutto());
            stockOutProductDetailDTO.setTotalNetto(stockProduct.getTotalNetto());
            if (stockProduct.getDiscountId() != null) {
                stockOutProductDetailDTO.setDiscount(stockProduct.getDiscount().toListDTO());
                if (stockProduct.getDiscountItemProductId() != null) {
                    stockOutProductDetailDTO.setDiscountItemProduct(stockProduct.getDiscountItemProduct().toCommonDTO());
                    stockOutProductDetailDTO.setDiscountValueType(stockProduct.getDiscountItemProduct().getValueType());
                    stockOutProductDetailDTO.setDiscountLimit(stockProduct.getDiscountItemProduct().getDiscountLimit());
                }
            }
            if (stockProduct.getProductSerialId() != null) {
                stockOutProductDetailDTO.setProductSerial(stockProduct.getProductSerial().toLookUpDTO());
            }
            if (stockProduct.getCompanyCommittentId() != null) {
                stockOutProductDetailDTO.setCompanyCommittent(stockProduct.getCompanyCommittent().toLookUpDTO());
            }
            if (stockProduct.getProductContractId() != null) {
                stockOutProductDetailDTO.setProductContract(stockProduct.getProductContract().toCommonDTO());
            }
            if (StringUtils.isNotEmpty(stockProduct.getPackageCode())) {
                stockOutProductDetailDTO.setPackageCode(stockProduct.getPackageCode());
                stockOutProductDetailDTO.setPackageName(stockProduct.getPackageName());
            } else {
                stockOutProductDetailDTO.setPackageName(stockProduct.getProduct().getPackageName());
                stockOutProductDetailDTO.setPackageCode(stockProduct.getProduct().getPackageCode());
            }

            if (!StockStatus.COMPLETED.equals(stock.getStatus())) {
                stockOutProductDetailDTO.setQtyInWarehouse(warehouseProductRepository.getQty(stock.getWarehouseId(), stockProduct.getProductId(), stockProduct.getUnitId()));
                stockOutProductDetailDTO.setReservationQty(
                        warehouseProductRepository.getReservationQty(
                                Optional.ofNullable(stock.getWarehouseId()).orElse(stockProduct.getWarehouseId()),
                                stockProduct.getProductId(),
                                stockProduct.getUnitId()
                        )
                );
                if (stockProduct.getProductContractId() != null) {
                    stockOutProductDetailDTO.setQtyInWarehouseByContract(productContractWarehouseRepository.getQty(stock.getWarehouseId(), stockProduct.getProductContractId()));
                }
                if (stockProduct.getProductSerialId() != null) {
                    stockOutProductDetailDTO.setQtyInWarehouseBySerial(productSerialWarehouseRepository.getQty(stock.getWarehouseId(), stockProduct.getProductSerialId()));
                }
                if (stockProduct.getCompanyCommittentId() != null) {
                    stockOutProductDetailDTO.setQtyInWarehouseByCommittent(companyCommittentWarehouseRepository.getQty(stock.getWarehouseId(), stockProduct.getCompanyCommittentId()));
                }
                if (stock.getOrder() != null) {
                    stockOutProductDetailDTO.setQtyInOrders(orderService.checkQtyInOrders(stockProduct.getProductId(), stock.getOrder().getId(), stock.getWarehouseId()));
                } else {
                    stockOutProductDetailDTO.setQtyInOrders(orderService.checkQtyInOrders(stockProduct.getProductId(), null, stock.getWarehouseId()));
                }
            }
            if (StringUtils.isNotEmpty(stockProduct.getDiscountProductUniqueId())) {
                stockOutDetailDTO.addProduct(stockOutProductDetailDTO);
                productMap.put(stockProduct.getDiscountProductUniqueId(), new ArrayList<>());
            } else if (StringUtils.isNotEmpty(stockProduct.getParentDiscountProductUniqueId())) {
                if (productMap.containsKey(stockProduct.getParentDiscountProductUniqueId())) {
                    productMap.get(stockProduct.getParentDiscountProductUniqueId()).add(stockOutProductDetailDTO);
                } else {
                    productMap.put(stockProduct.getParentDiscountProductUniqueId(), new ArrayList<>(List.of(stockOutProductDetailDTO)));
                }
            } else {
                stockOutDetailDTO.addProduct(stockOutProductDetailDTO);
            }
        }
        if (!CollectionUtils.isEmpty(productMap)) {
            stockOutDetailDTO.getProducts().forEach(terminalStockOutProductDetailDTO -> {
                if (productMap.containsKey(terminalStockOutProductDetailDTO.getDiscountProductUniqueId())) {
                    terminalStockOutProductDetailDTO.getDiscountProducts().addAll(productMap.get(terminalStockOutProductDetailDTO.getDiscountProductUniqueId()));
                }
            });
        }
        return stockOutDetailDTO;
    }

    public StockOutDetailDTO convertByStockIn(Long stockInId) {
        Stock stockIn = stockOutRepository.findById(stockInId).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "stockInId", stockInId));

        this.validateCompany(stockIn.getCompanyId());
        StockOutDetailDTO stockOutDetailDTO = new StockOutDetailDTO();
        stockOutDetailDTO.setId(stockIn.getId());
        stockOutDetailDTO.setStockNumber(stockIn.getStockNumber());
        stockOutDetailDTO.setStatus(stockIn.getStatus());
        if (stockIn.getContract() != null) {
            stockOutDetailDTO.setContract(stockIn.getContract().toLookUpDTO());
        }
        stockOutDetailDTO.setDescription(stockIn.getDescription());
        partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(stockIn.getCompanyId(), stockIn.getCustomerId(), stockIn.getSupplierId()).ifPresent(partners -> stockOutDetailDTO.setCustomer(new CustomerWithLimitDTO(partners.getCustomer().toCommonBaseDTO(), partners.getLimitBalance(), partners.getCurrentBalance(), partners.isHasLimit(), partners.getCustomer().getPhone(), partners.getCustomer().getBrand())));
        stockOutDetailDTO.setSupplier(stockIn.getSupplier().toCommonBaseDTO());
        stockOutDetailDTO.setTransferDate(stockIn.getTransferDate());
        stockOutDetailDTO.setWarehouse(stockIn.getWarehouse().toCommonDTO());
        if (stockIn.getBranch() != null) {
            stockOutDetailDTO.setBranch(stockIn.getBranch().toCommonDTO());
        }
        if (stockIn.getFactura() != null) {
            stockOutDetailDTO.setFactura(stockIn.getFactura().toLink());
        }
        if (stockIn.getOrder() != null) {
            stockOutDetailDTO.setOrder(stockIn.getOrder().toOrderBaseDTO());
        }
        if (stockIn.getOldStock() != null) {
            stockOutDetailDTO.setOldStock(stockIn.getOldStock().toCommonDTO());
        }
        stockOutDetailDTO.setTotalQty(stockIn.getTotalQty());
        stockOutDetailDTO.setTotalPrice(stockIn.getTotalPrice());
        stockOutDetailDTO.setTotalForeignPrice(stockIn.getTotalForeignPrice());
        stockOutDetailDTO.setExchangeRate(stockIn.getExchangeRate());
        stockOutDetailDTO.setCurrencyType(stockIn.getCurrencyType());
        stockOutDetailDTO.setTotalPriceWithoutVat(stockIn.getTotalPriceWithoutVat());
        stockOutDetailDTO.setTotalVatPrice(stockIn.getTotalVatPrice());
        stockOutDetailDTO.setTotalBrutto(stockIn.getTotalBrutto());
        stockOutDetailDTO.setTotalNetto(stockIn.getTotalNetto());

        stockIn.getProducts().forEach(stockProduct -> {
            StockOutProductDetailDTO stockOutProductDetailDTO = new StockOutProductDetailDTO();
            stockOutProductDetailDTO.setStockProductId(stockProduct.getId());
            stockOutProductDetailDTO.setId(stockProduct.getProduct().getId());
            stockOutProductDetailDTO.setName(stockProduct.getProduct().getName());
            stockOutProductDetailDTO.setProductType(stockProduct.getProduct().getProductType());
            stockOutProductDetailDTO.setHasMark(stockProduct.getProduct().isHasMark());
            stockOutProductDetailDTO.setVatBarCode(stockProduct.getProduct().getVatBarcode());
            stockOutProductDetailDTO.setBarcode(stockProduct.getProduct().getBarcode());
            stockOutProductDetailDTO.setCapacity(stockProduct.getProduct().getCapacity());
            stockOutProductDetailDTO.setBoxBarcode(stockProduct.getProduct().getBoxBarcode());
            stockOutProductDetailDTO.setBlockBarcode(stockProduct.getProduct().getBlockBarcode());
            if (stockProduct.getProduct().getParent() != null) {
                stockOutProductDetailDTO.setParent(stockProduct.getProduct().getParent().toProductCommonDTO());
                if (StringUtils.isEmpty(stockOutProductDetailDTO.getBlockBarcode())) {
                    stockOutProductDetailDTO.setBlockBarcode(stockProduct.getProduct().getParent().getBarcode());
                }
            }
            if (stockProduct.getWarehouse() != null) {
                stockOutProductDetailDTO.setWarehouse(stockProduct.getWarehouse().toCommonDTO());
            }
            stockOutProductDetailDTO.setUnit(stockProduct.getUnit().toDTO());
            stockOutProductDetailDTO.setQty(stockProduct.getQty());

            stockOutProductDetailDTO.setSellingPrice(stockProduct.getSellingPrice());
            stockOutProductDetailDTO.setSellingForeignPrice(stockProduct.getSellingForeignPrice());
            stockOutProductDetailDTO.setSellingPriceWithoutVat(stockProduct.getSellingPriceWithoutVat());
            stockOutProductDetailDTO.setTotalSellingPrice(stockProduct.getTotalSellingPrice());
            stockOutProductDetailDTO.setTotalSellingForeignPrice(stockProduct.getTotalSellingForeignPrice());
            stockOutProductDetailDTO.setTotalSellingPriceWithoutVat(stockProduct.getTotalSellingPriceWithoutVat());
            stockOutProductDetailDTO.setVatPrice(stockProduct.getVatPrice());
            stockOutProductDetailDTO.setVatRate(stockProduct.getVatRate());
            stockOutProductDetailDTO.setTotalVatPrice(stockProduct.getTotalVatPrice());
            stockOutProductDetailDTO.setExciseRate(stockProduct.getExciseRate());
            stockOutProductDetailDTO.setExciseSum(stockProduct.getExciseSum());

            stockOutProductDetailDTO.setBasePrice(stockProduct.getBasePrice());
            stockOutProductDetailDTO.setCatalogPrice(stockProduct.getProduct().getPrice());
            stockOutProductDetailDTO.setCatalogForeignPrice(stockProduct.getProduct().getForeignPrice());
            stockOutProductDetailDTO.setQtyInPackage(stockProduct.getQtyInPackage());

            stockOutProductDetailDTO.setAggregationQty(stockProduct.getAggregationQty());
            stockOutProductDetailDTO.setMarkQty(stockProduct.getMarkQty());
            stockOutProductDetailDTO.setAggregationBlockQty(stockProduct.getAggregationBlockQty());
            stockOutProductDetailDTO.setTotalBrutto(stockProduct.getTotalBrutto());
            stockOutProductDetailDTO.setTotalNetto(stockProduct.getTotalNetto());

            stockOutProductDetailDTO.setQtyInWarehouse(warehouseProductRepository.getQty(stockIn.getWarehouseId(), stockProduct.getProductId(), stockProduct.getUnitId()));
            if (stockProduct.getProductSerialId() != null) {
                stockOutProductDetailDTO.setProductSerial(stockProduct.getProductSerial().toLookUpDTO());
            }
            stockOutProductDetailDTO.setReservationQty(
                    warehouseProductRepository.getReservationQty(
                            Optional.ofNullable(stockIn.getWarehouseId()).orElse(stockProduct.getWarehouseId()),
                            stockProduct.getProductId(),
                            stockProduct.getUnitId()
                    )
            );
            if (stockProduct.getCompanyCommittentId() != null) {
                stockOutProductDetailDTO.setCompanyCommittent(stockProduct.getCompanyCommittent().toLookUpDTO());
            }
            if (stockProduct.getLgota() != null) {
                stockOutProductDetailDTO.setLgota(stockProduct.getLgota().toLookUpDTO());
            }
            stockOutProductDetailDTO.setLgotaVatSum(stockProduct.getLgotaVatSum());

            stockOutDetailDTO.addProduct(stockOutProductDetailDTO);
        });
        return stockOutDetailDTO;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Stock stock = stockOutRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        this.validateCompany(stock.getCompanyId());
        if (stock.getFactura() != null && !(stock.getFactura().isDeleted() || Status.CANCELLED.equals(stock.getFactura().getStatus()) || Status.REJECTED.equals(stock.getFactura().getStatus()))) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.DELETING_STOCK_OUT_MESSAGE, new Object[]{stock.getStockNumber(), stock.getFactura().getFacturaNo()})).get();
        }
        if (!StockStatus.DRAFT.equals(stock.getStatus())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ACTION_UNAVAILABLE, new Object[]{localizationService.localize(MessageConstants.DELETE), localizationService.localize(MessageConstants.STOCK_OUT_2), localizationService.localize(MessageConstants.DRAFT)})).get();
        }
        if (StringUtils.isNotEmpty(stock.getReceiptUid())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.STOCK_OUT_HAS_RECEIPT)).get();
        }
        if (receiptRepository.existsByStockOutIdAndDeletedIsFalse(stock.getId())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.STOCK_OUT_HAS_RECEIPT)).get();
        }

        stock.setDeleted(true);
        stock.setStatus(StockStatus.DELETED);
        stockOutRepository.save(stock);

        if (stock.getOrder() != null) {
            orderService.updateStatus(stock.getOrder().getId(), new OrderStatusUpdateDTO(OrderStatus.IN_DELIVERY, stock.getOrder().getUpdatedDate()));
        }
    }

    @Transactional
    public void deleteStockProduct(Long stockProductId) {
        stockProductRepository.findById(stockProductId).map(stockProduct -> {
            stockProduct.getStockProductAggregations().clear();
            stockProduct.getStockProductAggregationBlocks().clear();
            stockProduct.getStockProductMarks().clear();
            stockProduct.setDeleted(true);
            Stock stock = stockProduct.getStock();
            stock.setTotalPrice(stock.getTotalPrice().subtract(stockProduct.getTotalSellingPrice()));
            stock.setTotalQty(stock.getTotalQty().subtract(stockProduct.getQty()));
            stock.setTotalPriceWithoutVat(stock.getTotalPriceWithoutVat().subtract(stockProduct.getTotalSellingPriceWithoutVat()));
            stock.setTotalVatPrice(stock.getTotalVatPrice().subtract(stockProduct.getTotalVatPrice()));
            if (stock.getTotalDiscountPrice() != null && stockProduct.getTotalDiscountPrice() != null) {
                stock.setTotalDiscountPrice(stock.getTotalDiscountPrice().subtract(stockProduct.getTotalDiscountPrice()));
            }
            stockOutRepository.save(stock);
            stockProductRepository.save(stockProduct);
            return stockProduct;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(StockProduct.class.getSimpleName(), "id", stockProductId));
    }

    @Transactional
    public CheckStatusResponseDTO updateStatus(Long id, StockStatus status, LocalDateTime lastModifiedDate) {
        Stock stock = stockOutRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        if (!stock.getUpdatedDate().equals(lastModifiedDate)) {
            return new CheckStatusResponseDTO(false, 1);
        }
        if (StockStatus.DRAFT == status) {
            log.error("Status from UI: {}", status);
            throw new BadRequestException("Changing status to DRAFT is not permitted");
        }
        if (StockStatus.CANCELLED != status && StockStatus.CANCELLED == stock.getStatus()) {
            throw new BadRequestException("Update status is not permitted");
        }
        this.validateCompany(stock.getCompanyId());
        if (StockStatus.COMPLETED.equals(status)) {
            if (!StockStatus.DRAFT.equals(stock.getStatus())) {
                throw new BadRequestException(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.ACTION_UNAVAILABLE, new Object[]{localizationService.localize(MessageConstants.COMPLETE), localizationService.localize(MessageConstants.STOCK_OUT_2), localizationService.localize(MessageConstants.DRAFT)}));
            }
            partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(stock.getCompanyId(), stock.getCustomerId(), stock.getSupplierId()).ifPresent(partners -> {
                BigDecimal currentBalance = Optional.ofNullable(partners.getCurrentBalance()).orElse(BigDecimal.ZERO);
                if (!stock.isWithLimit() && partners.isHasLimit()) {
                    BigDecimal limitBalance = Optional.ofNullable(partners.getLimitBalance()).orElse(BigDecimal.ZERO);
                    if (limitBalance.negate().compareTo(currentBalance.subtract(Optional.ofNullable(stock.getTotalPrice()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(stock.getTotalDiscountPrice()).orElse(BigDecimal.ZERO)))) > 0) {
                        throw new BadRequestException("Отгрузка не может быть выполнена!", "Сумма отгрузки превышает установленный лимит");
                    }
                }
                partners.setCurrentBalance(currentBalance.subtract(Optional.ofNullable(stock.getTotalPrice()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(stock.getTotalDiscountPrice()).orElse(BigDecimal.ZERO))));
                if (stock.getDeliveryBranchId() != null) {
                    CustomerBranch customerBranch = stock.getDeliveryBranch();
                    BigDecimal balance = Optional.ofNullable(customerBranch.getCurrentBalance()).orElse(BigDecimal.ZERO);
                    customerBranch.setCurrentBalance(balance.subtract(Optional.ofNullable(stock.getTotalPrice()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(stock.getTotalDiscountPrice()).orElse(BigDecimal.ZERO))));
                    customerBranchRepository.save(customerBranch);
                }
                financialOperationService.createFromStockOut(stock);
                partnerRepository.save(partners);
            });
            stock.setStatus(StockStatus.COMPLETED);
            stock.setCompletedDate(LocalDateTime.now());
            if (stock.getWarehouseId() != null) {
                warehouseProductService.out(stock);
                stock.getProducts().forEach(stockProduct -> warehouseProductService.updateCodes(stockProduct, true));
            } else {
                warehouseProductService.outFromMultiWarehouse(stock);
                stock.getProducts().forEach(stockProduct -> warehouseProductService.updateCodes(stockProduct, true));
            }
            if (stock.getOrder() != null) {
                orderService.updateStatus(stock.getOrder(), OrderStatus.COMPLETED);
            }
            if (stock.getContractId() != null) {
                Contract contract = stock.getContract();
                contract.setSalePrice(Optional.ofNullable(contract.getSalePrice()).orElse(BigDecimal.ZERO).add(stock.getTotalPrice()));
                contractRepository.save(contract);
            }
            stockOutRepository.save(stock);
            if (stock.getPaymentType() == null || PaymentType.TRANSFER == stock.getPaymentType()) {
                eDocService.generateDoc(new EDocsGenerateDTO(stock.getId(), EntityTypeEnum.STOCK_OUT, stock.getCompanyId()));
            }
            notificationProducer.send(TemplateType.COMPLETE_STOCK_OUT,
                    getCurrentCompanyId(),
                    Map.of(
                            TemplateTypeAlias.TRANSACTION_NUMBER, stock.getStockNumber(),
                            TemplateTypeAlias.CREATOR, getCurrentUserId()
                    ));
        } else if (StockStatus.CANCELLED.equals(status)) {
            if (!StockStatus.COMPLETED.equals(stock.getStatus())) {
                throw new BadRequestException("Вы можете отклонять только Завершен");
            }
            if (PaymentType.TRANSFER != stock.getPaymentType()) {
                if (StringUtils.isNotEmpty(stock.getReceiptUid())) {
                    throw new BadRequestException(localizationService.localize(MessageConstants.STOCK_OUT_HAS_RECEIPT));
                }
                if (receiptRepository.existsByStockOutIdAndDeletedIsFalse(stock.getId())) {
                    throw new BadRequestException(localizationService.localize(MessageConstants.STOCK_OUT_HAS_RECEIPT));
                }
            }
            if (!CollectionUtils.isEmpty(stock.getFacturas())) {
                for (Factura factura : stock.getFacturas()) {
                    if (factura != null && !factura.isDeleted()) {
                        if (!(Status.CANCELLED.equals(factura.getStatus()) || Status.REJECTED.equals(factura.getStatus()))) {
                            throw new BadRequestException(localizationService.localize(MessageConstants.YOU_SHOULD_CANCEL_INVOICE, new Object[]{factura.getFacturaNo()}));
                        }
                        stock.setFacturaId(null);
                        stock.setFactura(null);
                        factura.setStockId(null);
                        factura.setStock(null);
                        facturaRepository.save(factura);
                    }
                }
            }
            stock.setStatus(StockStatus.CANCELLED);
            financialOperationService.deleteByStockOutId(id);
            if (stock.getOrder() != null) {
                orderService.updateStatus(stock.getOrder(), OrderStatus.APPROVED);
            }
            if (stock.getContractId() != null) {
                Contract contract = stock.getContract();
                contract.setSalePrice(Optional.ofNullable(contract.getSalePrice()).orElse(BigDecimal.ZERO).subtract(stock.getTotalPrice()));
                contractRepository.save(contract);
            }
            financialOperationRepository.findFirstByStockOutIdAndOperationTypeAndDeletedIsFalse(id, FinancialOperationType.CREDIT)
                    .ifPresent(financialOperation -> {
                        if (financialOperation.getCustomerBranchId() != null) {
                            CustomerBranch customerBranch = financialOperation.getCustomerBranch();
                            BigDecimal currentBalance = Optional.ofNullable(customerBranch.getCurrentBalance()).orElse(BigDecimal.ZERO);
                            customerBranch.setCurrentBalance(currentBalance.add(Optional.ofNullable(stock.getTotalPrice()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(stock.getTotalDiscountPrice()).orElse(BigDecimal.ZERO))));
                            customerBranchRepository.save(customerBranch);
                        }
                        partnerRepository.
                                findByCompanyIdAndCustomerIdAndSupplierId(stock.getCompanyId(), stock.getCustomerId(), stock.getSupplierId())
                                .ifPresent(partners -> {
                                    BigDecimal currentBalance = Optional.ofNullable(partners.getCurrentBalance()).orElse(BigDecimal.ZERO);
                                    partners.setCurrentBalance(currentBalance.add(Optional.ofNullable(stock.getTotalPrice()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(stock.getTotalDiscountPrice()).orElse(BigDecimal.ZERO))));
                                    partnerRepository.save(partners);
                                });
                        financialOperation.setStatus(CommonStatus.IN_ACTIVE);
                        financialOperationRepository.save(financialOperation);
                    });
            if (stock.getWarehouseId() != null) {
                warehouseProductService.in(stock, true);
                stock.getProducts().forEach(stockProduct -> warehouseProductService.updateCodes(stockProduct, false));
            } else {
                warehouseProductService.inToMultiWarehouse(stock, true);
                stock.getProducts().forEach(stockProduct -> warehouseProductService.updateCodes(stockProduct, false));
            }
            stockOutRepository.save(stock);
        }
        return new CheckStatusResponseDTO(true, 0);
    }

    @Transactional
    public void assignCashier(Long id, Long cashierId) {
        stockOutRepository
                .findById(id)
                .map(stockOut -> {
                    stockOut.setCashierId(cashierId);
                    stockOutRepository.save(stockOut);
                    return stockOut;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
    }

    @Transactional
    public CheckStatusResponseDTO updatePaymentType(Long id, PaymentType paymentType, LocalDateTime lastModifiedDate) {
        Stock stock = stockOutRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        if (!stock.getUpdatedDate().equals(lastModifiedDate)) {
            return new CheckStatusResponseDTO(false, 1);
        }
        this.validateCompany(stock.getCompanyId());
        stock.setPaymentType(paymentType);
        stockOutRepository.save(stock);
        return new CheckStatusResponseDTO(true, 0);
    }

    @Transactional
    public Long copy(Long id) {
        Stock oldStock = stockOutRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        if (!StockStatus.CANCELLED.equals(oldStock.getStatus())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COPY_NOT_ALLOWED)).get();
        }
        if (oldStock.getOrder() != null) {
            Set<OrderStatus> COPY_ALLOWED_ORDER_STATUSES = EnumSet.of(OrderStatus.REJECTED, OrderStatus.CANCELLED, OrderStatus.DELETED);
            CompanySettings companySettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(oldStock.getCompanyId(), GenericSettingsType.WORK_WITH_RESERVATION)
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(CompanySettings.class.getSimpleName(), "companyId", oldStock.getCompanyId()));
            if (companySettings.isEnabled() && !COPY_ALLOWED_ORDER_STATUSES.contains(oldStock.getOrder().getStatus())) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COPY_NOT_ALLOWED)).get();
            }
        }
        this.validateCompany(oldStock.getCompanyId());

        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_OUT, oldStock.getCompanyId());

        Stock stock = new Stock();
        stock.setOldStockId(oldStock.getId());
        stock.setTransferType(TransferType.OUT);
        stock.setTransferDate(Optional.ofNullable(oldStock.getTransferDate()).orElse(LocalDateTime.now()));
        stock.setStockNumber(numberingMap.get(NUMBER));
        stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stock.setContractId(oldStock.getContractId());
        stock.setDescription(oldStock.getDescription());
        stock.setWarehouseId(oldStock.getWarehouseId());
        stock.setVehicleId(oldStock.getVehicleId());
        stock.setBranchId(oldStock.getBranchId());
        stock.setCustomerId(oldStock.getCustomerId());
        stock.setSupplierId(oldStock.getSupplierId());
        stock.setCompanyId(oldStock.getCompanyId());
        stock.setPriceListId(oldStock.getPriceListId());
        stock.setDeliveryBranchId(oldStock.getDeliveryBranchId());
        if (oldStock.getOrder() != null) {
            stock.setOrderId(oldStock.getOrder().getId());
        }
        stock.setPaymentType(oldStock.getPaymentType());
        stock.setTotalNetto(oldStock.getTotalNetto());
        stock.setTotalBrutto(oldStock.getTotalBrutto());
        stock.setHasDiscount(oldStock.isHasDiscount());
        stock = stockOutRepository.save(stock);
        if (oldStock.getOrder() != null) {
            Order order = oldStock.getOrder();
            order.setStockOutId(stock.getId());
            order.setStockOut(stock);
            order.setStockOutNumber(stock.getStockNumber());
            orderRepository.save(order);
        }

        for (StockProduct oldStockProduct : oldStock.getProducts()) {
            StockProduct stockProduct = new StockProduct();
            stockProduct.setProductId(oldStockProduct.getProductId());
            stockProduct.setWarehouseId(oldStockProduct.getWarehouseId());
            stockProduct.setQty(oldStockProduct.getQty());
            stockProduct.setQtyInPackage(oldStockProduct.getQtyInPackage());
            stockProduct.setRequestedQty(oldStockProduct.getRequestedQty());
            stockProduct.setUnitId(oldStockProduct.getUnitId());

            stockProduct.setSellingPrice(oldStockProduct.getSellingPrice());
            stockProduct.setSellingForeignPrice(oldStockProduct.getSellingForeignPrice());
            stockProduct.setSellingPriceWithoutVat(oldStockProduct.getSellingPriceWithoutVat());
            stockProduct.setTotalSellingPrice(oldStockProduct.getTotalSellingPrice());
            stockProduct.setTotalSellingForeignPrice(oldStockProduct.getTotalSellingForeignPrice());
            stockProduct.setTotalSellingPriceWithoutVat(oldStockProduct.getTotalSellingPriceWithoutVat());

            stockProduct.setVatRate(oldStockProduct.getVatRate());
            stockProduct.setVatPrice(oldStockProduct.getVatPrice());
            stockProduct.setTotalVatPrice(oldStockProduct.getTotalVatPrice());
            stockProduct.setPriceListPrice(oldStockProduct.getPriceListPrice());
            stockProduct.setExciseRate(oldStockProduct.getExciseRate());
            stockProduct.setExciseSum(oldStockProduct.getExciseSum());

            stockProduct.setMarkupPrice(oldStockProduct.getMarkupPrice());
            stockProduct.setMarkupPercent(oldStockProduct.getMarkupPercent());
            stockProduct.setBasePrice(oldStockProduct.getBasePrice());
            stockProduct.setCompanyCommittentId(oldStockProduct.getCompanyCommittentId());
            stockProduct.setProductSerialId(oldStockProduct.getProductSerialId());

            stockProduct.setAggregationQty(oldStockProduct.getAggregationQty());
            stockProduct.setMarkQty(oldStockProduct.getMarkQty());
            stockProduct.setAggregationBlockQty(oldStockProduct.getAggregationBlockQty());

            stockProduct.setProductContractId(oldStockProduct.getProductContractId());
            stockProduct.setProductContract(oldStockProduct.getProductContract());

            stockProduct.setLgotaVatSum(oldStockProduct.getLgotaVatSum());

            stockProduct.setPackageCode(oldStockProduct.getPackageCode());
            stockProduct.setPackageName(oldStockProduct.getPackageName());

            stockProduct.setScannedQty(oldStockProduct.getScannedQty());

            stockProduct.setStockId(stock.getId());
            stockProduct.setDiscountPercent(oldStockProduct.getDiscountPercent());
            stockProduct.setDiscountProductUniqueId(oldStockProduct.getDiscountProductUniqueId());
            stockProduct.setParentDiscountProductUniqueId(oldStockProduct.getParentDiscountProductUniqueId());
            stockProduct.setDiscountId(oldStockProduct.getDiscountId());
            stockProduct.setDiscountItemProductId(oldStockProduct.getDiscountItemProductId());
            stockProduct.setDiscountPrice(oldStockProduct.getDiscountPrice());
            stockProduct.setTotalDiscountPrice(oldStockProduct.getTotalDiscountPrice());
            stockProduct.setTotalBrutto(oldStockProduct.getTotalBrutto());
            stockProduct.setTotalNetto(oldStockProduct.getTotalNetto());
            stockProduct.setHasMarking(!CollectionUtils.isEmpty(oldStockProduct.getStockProductAggregations()) || !CollectionUtils.isEmpty(oldStockProduct.getStockProductMarks()) || !CollectionUtils.isEmpty(oldStockProduct.getStockProductAggregationBlocks()));
            stockProduct = stockProductRepository.save(stockProduct);

            if (!CollectionUtils.isEmpty(packageQuantityService.getByCompositeId(oldStockProduct))) {
                packageQuantityService.create(stockProduct, packageQuantityService.getByCompositeId(oldStockProduct));
            }

            if (!CollectionUtils.isEmpty(oldStockProduct.getStockProductAggregations())) {
                List<StockProductAggregation> stockProductAggregations = new ArrayList<>();
                for (StockProductAggregation oldStockProductAggregation : oldStockProduct.getStockProductAggregations()) {
                    StockProductAggregation stockProductAggregation = new StockProductAggregation();
                    stockProductAggregation.setStockProductId(stockProduct.getId());
                    stockProductAggregation.setCode(oldStockProductAggregation.getCode());
                    stockProductAggregation.setOriginalCode(oldStockProductAggregation.getOriginalCode());
                    stockProductAggregation.setQty(oldStockProductAggregation.getQty());
                    stockProductAggregation.setProductId(oldStockProductAggregation.getProductId());
                    stockProductAggregation.setPallet(oldStockProductAggregation.getPallet());
                    stockProductAggregation.setTransferType(TransferType.OUT);
                    stockProductAggregations.add(stockProductAggregation);
                }
                stockProductAggregationRepository.saveAll(stockProductAggregations);
                stockProductAggregationRepository.deleteAll(oldStockProduct.getStockProductAggregations());
            }
            if (!CollectionUtils.isEmpty(oldStockProduct.getStockProductMarks())) {
                List<StockProductMark> stockProductMarks = new ArrayList<>();
                for (StockProductMark oldStockProductMark : oldStockProduct.getStockProductMarks()) {
                    StockProductMark stockProductMark = new StockProductMark();
                    stockProductMark.setStockProductId(stockProduct.getId());
                    stockProductMark.setCode(oldStockProductMark.getCode());
                    stockProductMark.setOriginalCode(oldStockProductMark.getOriginalCode());
                    stockProductMark.setQty(oldStockProductMark.getQty());
                    stockProductMark.setProductId(oldStockProductMark.getProductId());
                    stockProductMark.setBox(oldStockProductMark.getBox());
                    stockProductMark.setBlock(oldStockProductMark.getBlock());
                    stockProductMark.setTransferType(TransferType.OUT);
                    stockProductMarks.add(stockProductMark);
                }
                stockProductMarkRepository.saveAll(stockProductMarks);
                stockProductMarkRepository.deleteAll(stockProduct.getStockProductMarks());
            }
            if (!CollectionUtils.isEmpty(oldStockProduct.getStockProductAggregationBlocks())) {
                List<StockProductAggregationBlock> stockProductAggregationBlocks = new ArrayList<>();
                for (StockProductAggregationBlock oldStockProductAggregationBlock : oldStockProduct.getStockProductAggregationBlocks()) {
                    StockProductAggregationBlock stockProductAggregationBlock = new StockProductAggregationBlock();
                    stockProductAggregationBlock.setStockProductId(stockProduct.getId());
                    stockProductAggregationBlock.setCode(oldStockProductAggregationBlock.getCode());
                    stockProductAggregationBlock.setOriginalCode(oldStockProductAggregationBlock.getOriginalCode());
                    stockProductAggregationBlock.setQty(oldStockProductAggregationBlock.getQty());
                    stockProductAggregationBlock.setProductId(oldStockProductAggregationBlock.getProductId());
                    stockProductAggregationBlock.setTransferType(TransferType.OUT);
                    stockProductAggregationBlocks.add(stockProductAggregationBlock);
                }
                stockProductAggregationBlockRepository.saveAll(stockProductAggregationBlocks);
                stockProductAggregationBlockRepository.deleteAll(stockProduct.getStockProductAggregationBlocks());
            }
        }
        stock.setTotalQty(oldStock.getTotalQty());
        stock.setTotalPrice(oldStock.getTotalPrice());
        stock.setTotalForeignPrice(oldStock.getTotalForeignPrice());
        stock.setExchangeRate(oldStock.getExchangeRate());
        stock.setCurrencyType(oldStock.getCurrencyType());
        stock.setTotalPriceWithoutVat(oldStock.getTotalPriceWithoutVat());
        stock.setTotalVatPrice(oldStock.getTotalVatPrice());
        stock.setTotalDiscountPrice(oldStock.getTotalDiscountPrice());
        stockOutRepository.save(stock);
        return stock.getId();
    }

    public Map<CodeType, List<CheckedCodeDTO>> getAggregationCodes(Long stockProductId) {
        Map<CodeType, List<CheckedCodeDTO>> result = new HashMap<>();

        List<CheckedCodeDTO> aggregationCodes = stockProductAggregationRepository.findAllByStockProductIdAndDeletedIsFalseAndParentIsTrue(stockProductId).stream().map(StockProductAggregation::toCheckDTO).toList();

        result.put(CodeType.AGGREGATION, aggregationCodes);

        List<CheckedCodeDTO> markCodes = stockProductMarkRepository.findAllByStockProductIdAndDeletedIsFalseAndParentIsTrue(stockProductId).stream().map(StockProductMark::toCheckDTO).toList();

        result.put(CodeType.MARK, markCodes);

        List<CheckedCodeDTO> aggregationBlockCodes = stockProductAggregationBlockRepository.findAllByStockProductIdAndDeletedIsFalseAndParentIsTrue(stockProductId).stream().map(StockProductAggregationBlock::toCheckDTO).toList();

        result.put(CodeType.BLOCK, aggregationBlockCodes);

        return result;
    }

    public List<CisInfoResponse> batchCheck(BatchValidationDTO validationDTO) {
        Map<String, CisInfoResponse> resultMap = new LinkedHashMap<>();
        List<String> markCodes = validationDTO.getMarkCodes();
        List<String> aggregationBlockCodes = validationDTO.getAggregationBlockCodes();
        Company company = getCurrentCompany();
        CompanySettings xtraceSettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.XTRACE_ENABLED).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanySettings.class.getSimpleName(), "companyId", getCurrentCompanyId()));

        if (!CollectionUtils.isEmpty(validationDTO.getAggregationCodes())) {
            List<String> clearAggregationCodes = validationDTO.getAggregationCodes().stream().map( code ->  {
                CisInfo cisInfo = new CisInfo();
                code = MarkUtils.clearAggregationCode(code);
                cisInfo.setRequestedCis(code);
                cisInfo.setOriginalCode(code);
                resultMap.put(code, new CisInfoResponse(cisInfo));
                return code;
            }).collect(Collectors.toCollection(ArrayList::new));

            if (xtraceSettings.isEnabled()) {
                cisInfoService.batchCheckAggregation(validationDTO, resultMap, clearAggregationCodes);
            }
            List<String> aggregationCodesForUsedCheck = new ArrayList<>(resultMap.values().stream().filter(cisInfoResponse -> TuronMarkStatus.isSuccess(cisInfoResponse.getCisInfo().getStatus()) && PackageType.isAggregation(PackageType.valueOf(cisInfoResponse.getCisInfo().getPackageType())))
                    .map(cisInfoResponse -> Optional.ofNullable(cisInfoResponse.getCisInfo().getRequestedCis()).orElseGet(() -> cisInfoResponse.getCisInfo().getOriginalCode())).toList());
            if (!aggregationCodesForUsedCheck.isEmpty()) {
                List<StockProductAggregation> stockProductAggregations = stockProductAggregationRepository.getByTransferTypeAndCodesAndStatusNot(
                        TransferType.IN,
                        aggregationCodesForUsedCheck,
                        company.getId(),
                        StockStatus.CANCELLED
                );
                if (!CollectionUtils.isEmpty(stockProductAggregations)) {
                    Map<String, StockProductAggregation> aggregationMap = new LinkedHashMap<>();
                    stockProductAggregations.forEach(stockProductAggregation -> {
                        if (!aggregationMap.containsKey(stockProductAggregation.getCode())) {
                            aggregationMap.put(stockProductAggregation.getCode(), stockProductAggregation);
                        }
                    });
                    aggregationMap.forEach((code, stockProductAggregation) -> {
                        if (TransferType.OUT.equals(stockProductAggregation.getTransferType())) {
                            CisInfoResponse cisInfoResponse = resultMap.get(stockProductAggregation.getOriginalCode());
                            if (cisInfoResponse == null) {
                                cisInfoResponse = resultMap.get(stockProductAggregation.getCode());
                            }
                            if (cisInfoResponse == null) {
                                cisInfoResponse = resultMap.values().stream().filter(tempCisInfo -> tempCisInfo.getCisInfo().getCode().equals(code)).findFirst().orElse(null);
                            }
                            if (cisInfoResponse != null) {
                                CisInfo cisInfo = cisInfoResponse.getCisInfo();
                                cisInfo.setStatus(TuronMarkStatus.EMITTED);
                                cisInfo.setErrorMessage("Код уже использован");
                                cisInfo.setEntityId(stockProductAggregation.getStockProduct().getStockId());
                                cisInfo.setEntityType(EntityTypeEnum.STOCK_OUT.name());
                                cisInfo.setEntityNumber(stockProductAggregation.getStockProduct().getStock().getStockNumber());

                                if (StringUtils.isNotEmpty(validationDTO.getBarcode()) && StringUtils.isEmpty(cisInfo.getGtin())) {
                                    cisInfo.setGtin(validationDTO.getBarcode());
                                }
                                cisInfo.setProductName(stockProductAggregation.getStockProduct().getProduct().getName());
                                cisInfoResponse.setCisInfo(cisInfo);
                                resultMap.remove(cisInfo.getOriginalCode());
                                resultMap.put(cisInfo.getOriginalCode(), cisInfoResponse);
                            }
                        }
                    });
                }
            }
        }

        //Mark codes
        if (!CollectionUtils.isEmpty(markCodes)) {
            List<String> tempMarkCodes = new ArrayList<>(markCodes);
            tempMarkCodes.forEach(markCode -> {
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
            List<String> markCodesForUsedCheck = new ArrayList<>(resultMap.values().stream().filter(cisInfoResponse -> TuronMarkStatus.isSuccess(cisInfoResponse.getCisInfo().getStatus()) && PackageType.isUnit(PackageType.valueOf(cisInfoResponse.getCisInfo().getPackageType())))
                    .map(cisInfoResponse -> Optional.ofNullable(cisInfoResponse.getCisInfo().getRequestedCis()).orElseGet(() -> cisInfoResponse.getCisInfo().getOriginalCode())).toList());
            if (!markCodesForUsedCheck.isEmpty()) {
                List<StockProductMark> stockProductMarks = stockProductMarkRepository.getByTransferTypeAndCodesAndStatusNot(
                        TransferType.IN,
                        markCodesForUsedCheck,
                        company.getId(),
                        StockStatus.CANCELLED
                );

                if (!CollectionUtils.isEmpty(stockProductMarks)) {
                    Map<String, StockProductMark> stockProductMarkMap = new HashMap<>();
                    stockProductMarks.forEach(stockProductMark -> {
                        if (!stockProductMarkMap.containsKey(stockProductMark.getCode())) {
                            stockProductMarkMap.put(stockProductMark.getCode(), stockProductMark);
                        }
                    });
                    stockProductMarkMap.forEach((code, stockProductMark) -> {
                        if (TransferType.OUT.equals(stockProductMark.getTransferType())) {
                            CisInfoResponse cisInfoResponse = resultMap.get(stockProductMark.getOriginalCode());
                            if (cisInfoResponse == null) {
                                cisInfoResponse = resultMap.get(stockProductMark.getCode());
                            }
                            if (cisInfoResponse == null) {
                                cisInfoResponse = resultMap.values().stream().filter(tempCisInfo -> tempCisInfo.getCisInfo().getCode().equals(code)).findFirst().orElse(null);
                            }
                            if (cisInfoResponse != null) {
                                CisInfo cisInfo = cisInfoResponse.getCisInfo();
                                cisInfo.setStatus(TuronMarkStatus.EMITTED);
                                cisInfo.setErrorMessage("Код уже использован");
                                cisInfo.setEntityId(stockProductMark.getStockProduct().getStockId());
                                cisInfo.setEntityType(EntityTypeEnum.STOCK_OUT.name());
                                cisInfo.setEntityNumber(stockProductMark.getStockProduct().getStock().getStockNumber());
                                if (StringUtils.isNotEmpty(validationDTO.getBarcode()) && StringUtils.isEmpty(cisInfo.getGtin())) {
                                    cisInfo.setGtin(validationDTO.getBarcode());
                                }
                                cisInfo.setProductName(stockProductMark.getStockProduct().getProduct().getName());
                                cisInfoResponse.setCisInfo(cisInfo);
                                resultMap.remove(cisInfo.getOriginalCode());
                                resultMap.put(cisInfo.getOriginalCode(), cisInfoResponse);
                            }
                        }
                    });
                }
            }
        }

        //block codes
        if (!CollectionUtils.isEmpty(aggregationBlockCodes)) {
            List<String> tempBlockCodes = new ArrayList<>(aggregationBlockCodes);
            tempBlockCodes.forEach(blockCode -> {
                CisInfo cisInfo = new CisInfo();
                cisInfo.setRequestedCis(MarkUtils.clearOriginalCode(blockCode, validationDTO.getProductType()));
                cisInfo.setOriginalCode(blockCode);
                cisInfo.setGtin(validationDTO.getBarcode());
                resultMap.put(cisInfo.getOriginalCode(), new CisInfoResponse(cisInfo));
            });
            if (xtraceSettings.isEnabled()) {
                cisInfoService.batchCheckAggregationBlock(validationDTO, resultMap, aggregationBlockCodes);
            }
            List<String> aggregationBlockCodesForUsedCheck = new ArrayList<>(resultMap.values().stream().filter(cisInfoResponse -> TuronMarkStatus.isSuccess(cisInfoResponse.getCisInfo().getStatus()) && PackageType.isBlock(PackageType.valueOf(cisInfoResponse.getCisInfo().getPackageType())))
                    .map(cisInfoResponse -> Optional.ofNullable(cisInfoResponse.getCisInfo().getRequestedCis()).orElseGet(() -> cisInfoResponse.getCisInfo().getOriginalCode())).toList());
            if (!aggregationBlockCodesForUsedCheck.isEmpty()) {
                List<StockProductAggregationBlock> stockProductAggregationBlocks = stockProductAggregationBlockRepository.getByTransferTypeAndCodesAndStatusNot(
                        TransferType.IN,
                        aggregationBlockCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockStatus.CANCELLED
                );
                if (!stockProductAggregationBlocks.isEmpty()) {
                    Map<String, StockProductAggregationBlock> aggregationBlockMap = new HashMap<>();
                    stockProductAggregationBlocks.forEach(blockCode -> {
                        if (!aggregationBlockMap.containsKey(blockCode.getCode())) {
                            aggregationBlockMap.put(blockCode.getCode(), blockCode);
                        }
                    });
                    aggregationBlockMap.forEach((code, stockProductAggregationBlock) -> {
                        if (TransferType.OUT.equals(stockProductAggregationBlock.getTransferType())) {
                            CisInfoResponse cisInfoResponse = resultMap.get(stockProductAggregationBlock.getOriginalCode());
                            if (cisInfoResponse == null) {
                                cisInfoResponse = resultMap.get(stockProductAggregationBlock.getCode());
                            }
                            if (cisInfoResponse == null) {
                                cisInfoResponse = resultMap.values().stream().filter(tempCisInfo -> tempCisInfo.getCisInfo().getCode().equals(code)).findFirst().orElse(null);
                            }
                            if (cisInfoResponse != null) {
                                CisInfo cisInfo = cisInfoResponse.getCisInfo();
                                cisInfo.setStatus(TuronMarkStatus.EMITTED);
                                cisInfo.setErrorMessage("Код уже использован");
                                cisInfo.setEntityId(stockProductAggregationBlock.getStockProduct().getStockId());
                                cisInfo.setEntityType(EntityTypeEnum.STOCK_OUT.name());
                                cisInfo.setEntityNumber(stockProductAggregationBlock.getStockProduct().getStock().getStockNumber());

                                if (StringUtils.isNotEmpty(validationDTO.getBarcode()) && StringUtils.isEmpty(cisInfo.getGtin())) {
                                    cisInfo.setGtin(validationDTO.getBarcode());
                                }
                                cisInfo.setProductName(stockProductAggregationBlock.getStockProduct().getProduct().getName());
                                cisInfoResponse.setCisInfo(cisInfo);

                                resultMap.remove(cisInfo.getOriginalCode());
                                resultMap.put(cisInfo.getOriginalCode(), cisInfoResponse);
                            }
                        }
                    });
                }
            }
        }

        return Arrays.asList(resultMap.values().toArray(new CisInfoResponse[0]));
    }

    public CommonStockStatisticsDTO getStatistics(StockFilter filter) {
        return stockOutRepository.getStatistic(filter);
    }

    public ResponseEntity<?> exportToCsvProducts(Long stockProductId) {
        StockProductFilter filter = new StockProductFilter();
        filter.setStockProductId(stockProductId);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", "StockOutProduct.xlsx"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stockOutDetailCsvHandler.generate(filter));
    }

    public ResponseEntity<?> exportToExcelProducts(Long stockProductId) {
        StockProductFilter filter = new StockProductFilter();
        filter.setStockProductId(stockProductId);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", "StockOutProduct.csv"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stockOutDetailExcelHandler.generate(filter));
    }

    @Transactional
    public Long convertFromOrder(Long orderId) {
        return orderRepository.findById(orderId).map(order -> {
            if (!OrderStatus.IN_DELIVERY.equals(order.getStatus())) {
                throw validationService.badRequestExceptionThrow("Only order with status IN_DELIVERY can be converted").get();
            }
            if (PaymentType.TRANSFER == order.getPaymentType() && (order.getCustomer().getTin() == null || order.getCustomer().getTin().isEmpty())) {
                throw validationService.badRequestExceptionThrow("invalid.customer.tin").get();
            }

            StockOutDTO stockOutDTO = new StockOutDTO();
            if (isExpeditor()) {
                stockOutDTO.setBranchId(SecurityUtils.getCurrentBranchId());
                warehouseExpeditorRepository.findFirstByExpeditorIdAndWarehouseIdOrderByTransferDateDesc(SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentWarehouseId())
                        .ifPresent(warehouseExpeditor -> stockOutDTO.setWarehouseId(warehouseExpeditor.getCarId()));
            } else {
                stockOutDTO.setBranchId(order.getBranchId());
            }
            stockOutDTO.setCustomerId(order.getCustomerId());
            stockOutDTO.setOrderId(order.getId());
            stockOutDTO.setPriceListId(order.getPriceListId());
            stockOutDTO.setDescription(order.getDescription());
            stockOutDTO.setContractId(order.getContractId());
            stockOutDTO.setDeliveryBranchId(order.getDeliveryBranchId());
            stockOutDTO.setAppType(order.getAppType());
            stockOutDTO.setPaymentType(order.getPaymentType());
            stockOutDTO.setTotalQty(order.getTotalQty());
            stockOutDTO.setTotalPrice(order.getTotalPrice());
            stockOutDTO.setTotalForeignPrice(order.getTotalForeignPrice());
            stockOutDTO.setExchangeRate(order.getExchangeRate());
            stockOutDTO.setCurrencyType(order.getCurrencyType());
            stockOutDTO.setTotalPriceWithoutVat(order.getTotalPriceWithoutVat());
            stockOutDTO.setTotalVatPrice(order.getTotalVatPrice());
            stockOutDTO.setTotalDiscountPrice(order.getTotalDiscountPrice());
            stockOutDTO.setTotalBrutto(order.getTotalBrutto());
            stockOutDTO.setTotalNetto(order.getTotalNetto());
            if (!CollectionUtils.isEmpty(order.getProducts())) {
                for (OrderProduct orderProduct : order.getProducts()) {
                    StockOutProductDTO stockOutProductDTO = new StockOutProductDTO();
                    stockOutProductDTO.setId(orderProduct.getProductId());
                    stockOutProductDTO.setName(orderProduct.getName());
                    stockOutProductDTO.setVatBarCode(orderProduct.getProduct().getVatBarcode());
                    stockOutProductDTO.setQtyInPackage(orderProduct.getProduct().getQtyInPackage());
                    stockOutProductDTO.setQtyInBox(orderProduct.getProduct().getQtyInBox());
                    stockOutProductDTO.setQtyInPallet(orderProduct.getProduct().getQtyInPallet());
                    stockOutProductDTO.setCapacity(orderProduct.getProduct().getCapacity());
                    stockOutProductDTO.setHasMark(orderProduct.getProduct().isHasMark());
                    stockOutProductDTO.setProductType(orderProduct.getProduct().getProductType());
                    stockOutProductDTO.setQty(orderProduct.getQty());
                    stockOutProductDTO.setSellingPrice(orderProduct.getPrice());
                    stockOutProductDTO.setSellingForeignPrice(orderProduct.getForeignPrice());
                    stockOutProductDTO.setSellingPriceWithoutVat(orderProduct.getPriceWithoutVat());
                    stockOutProductDTO.setTotalSellingPrice(orderProduct.getTotalPrice());
                    stockOutProductDTO.setTotalSellingForeignPrice(orderProduct.getTotalForeignPrice());
                    stockOutProductDTO.setTotalSellingPriceWithoutVat(orderProduct.getTotalPriceWithoutVat());
                    stockOutProductDTO.setPurchasePrice(orderProduct.getPrice());
                    stockOutProductDTO.setPurchaseForeignPrice(orderProduct.getForeignPrice());
                    stockOutProductDTO.setTotalPurchasePrice(orderProduct.getTotalPrice());
                    stockOutProductDTO.setTotalPurchaseForeignPrice(orderProduct.getTotalForeignPrice());
                    stockOutProductDTO.setPurchasePriceWithoutVat(orderProduct.getTotalPriceWithoutVat());

                    stockOutProductDTO.setMarkupPercent(orderProduct.getMarkupPercent());
                    stockOutProductDTO.setMarkupPrice(orderProduct.getMarkupPrice());
                    stockOutProductDTO.setVatRate(orderProduct.getVatRate());
                    stockOutProductDTO.setVatPrice(orderProduct.getVatPrice());
                    stockOutProductDTO.setTotalVatPrice(orderProduct.getTotalVatPrice());
                    stockOutProductDTO.setBasePrice(orderProduct.getBasePrice());
                    stockOutProductDTO.setExciseRate(orderProduct.getExciseRate());
                    stockOutProductDTO.setExciseSum(orderProduct.getExciseSum());

                    stockOutProductDTO.setWarehouseId(orderProduct.getWarehouseId());
                    stockOutProductDTO.setPriceListPrice(orderProduct.getPriceListPrice());
                    stockOutProductDTO.setRequestedQty(orderProduct.getQty());
                    stockOutProductDTO.setProductSerialId(orderProduct.getProductSerialId());
                    stockOutProductDTO.setCompanyCommittentId(orderProduct.getCompanyCommittentId());
                    stockOutProductDTO.setOrderProductId(orderProduct.getId());
                    stockOutProductDTO.setProductContractId(orderProduct.getProductContractId());
                    stockOutProductDTO.setDiscountId(orderProduct.getDiscountId());
                    stockOutProductDTO.setLgotaId(orderProduct.getLgotaId());
                    stockOutProductDTO.setLgotaVatSum(orderProduct.getLgotaVatSum());
                    stockOutProductDTO.setPackageCode(orderProduct.getPackageCode());
                    stockOutProductDTO.setPackageName(orderProduct.getPackageName());
                    stockOutProductDTO.setDiscountPercent(orderProduct.getDiscountPercent());
                    stockOutProductDTO.setDiscountPrice(orderProduct.getDiscountPrice());
                    stockOutProductDTO.setTotalDiscountPrice(orderProduct.getTotalDiscountPrice());
                    stockOutProductDTO.setTotalBrutto(orderProduct.getTotalBrutto());
                    stockOutProductDTO.setTotalNetto(orderProduct.getTotalNetto());
                    stockOutProductDTO.setDiscountItemProductId(orderProduct.getDiscountItemProductId());
                    stockOutProductDTO.setParentDiscountProductUniqueId(orderProduct.getParentDiscountProductUniqueId());
                    stockOutProductDTO.setDiscountProductUniqueId(orderProduct.getDiscountProductUniqueId());
                    stockOutDTO.addProduct(stockOutProductDTO);
                }
            }
            return this.create(stockOutDTO);
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", orderId));
    }

    @Transactional
    public void reverse(Long id) {
        Stock stock = stockOutRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        if (StockStatus.DELETED != stock.getStatus()) {
            throw validationService.badRequestExceptionThrow("Only deleted stock should be reverted").get();
        }
        stock.setDeleted(false);
        stock.setStatus(StockStatus.DRAFT);
        stockOutRepository.save(stock);

        stockProductRepository.reverseByStockId(stock.getId());
        stockProductAggregationRepository.reverseByStockId(stock.getId());
        stockProductAggregationBlockRepository.reverseByStockId(stock.getId());
        stockProductMarkRepository.reverseByStockId(stock.getId());
    }

    public StockWaybillCreateDTO getWayBill(Long id) {
        Stock stock = stockOutRepository.findByIdAndCompanyIdAndDeletedIsFalse(id, SecurityUtils.getCompanyId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));

        StockWaybillCreateDTO dto = new StockWaybillCreateDTO();
        dto.setOwner(Optional.ofNullable(stock.getCompany().getOwner()).map(SimpleEntity::toCommonDTO).orElse(null));
        dto.setCustomer(Optional.ofNullable(stock.getCustomer()).map(SimpleEntity::toCommonDTO).orElse(null));
        dto.setCompany(Optional.ofNullable(stock.getCompany()).map(SimpleEntity::toCommonDTO).orElse(null));
        dto.setStockNumber(stock.getStockNumber());
        if (stock.getProducts() != null)
            stock.getProducts().forEach(product -> {
                ProductWaybillDTO dtoProduct = new ProductWaybillDTO();
                dtoProduct.setId(product.getId());
                dtoProduct.setName(product.getName());
                dtoProduct.setQty(product.getQty());
                dtoProduct.setUnit(product.getUnit().toDTO());
                dtoProduct.setSellingPrice(product.getSellingPrice());
                dtoProduct.setSellingPriceWithoutVat(product.getSellingPriceWithoutVat());
                dtoProduct.setTotalSellingPrice(product.getTotalSellingPrice());
                dtoProduct.setTotalSellingPriceWithoutVat(product.getTotalSellingPriceWithoutVat());
                dtoProduct.setVatRate(product.getVatRate());
                dtoProduct.setVatPrice(product.getVatPrice());
                dtoProduct.setDiscountPrice(product.getDiscountPrice());
                dtoProduct.setTotalDiscountPrice(product.getTotalDiscountPrice());
                dtoProduct.setDiscountPercent(product.getDiscountPercent());
                dto.getProducts().add(dtoProduct);
            });

        //sum frm financial operation
        BigDecimal sumByStockId = financialOperationRepository.sumAmountByStockOutId(stock.getId(), FinancialOperationType.DEBIT);

        dto.setOrderStatus(Optional.ofNullable(stock.getOrder()).map(Order::getStatus).orElse(null));
        dto.setPrice(Optional.ofNullable(stock.getContract()).map(Contract::getPrice).orElse(null));
        dto.setSalePrice(Optional.ofNullable(stock.getContract()).map(Contract::getSalePrice).orElse(null));
        dto.setPaymentAmount(sumByStockId);
        return dto;
    }

    public TerminalStockOutStatisticsDTO getStockOutStatisticForTerminal(StockFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        filter.setUserId(SecurityUtils.getCurrentUserId());
        return stockOutRepository.getStockOutStatisticForTerminal(filter);
    }

    public Page<TerminalTopProductsStatisticsDTO> getTopProducts(StockFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        filter.setUserId(SecurityUtils.getCurrentUserId());
        ResultList<TerminalTopProductsStatisticsDTO> resultList = stockProductRepository.getTopProducts(filter);
        return new PageImpl<>(resultList.getList(), filter.getPageable(),resultList.getCount());
    }
}