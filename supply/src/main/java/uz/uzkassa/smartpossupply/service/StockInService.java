package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.*;
import uz.uzkassa.smartpossupply.domain.acceptancetransferact.AcceptanceTransferAct;
import uz.uzkassa.smartpossupply.domain.aggregation.Aggregation;
import uz.uzkassa.smartpossupply.domain.aggregation.InvalidAggregation;
import uz.uzkassa.smartpossupply.domain.aic.Aic;
import uz.uzkassa.smartpossupply.domain.category.Category;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyCommittent;
import uz.uzkassa.smartpossupply.domain.company.Partners;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.domain.mark.Mark;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.domain.order.OrderProduct;
import uz.uzkassa.smartpossupply.domain.party.Party;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.product.ProductContract;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerial;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.stock.*;
import uz.uzkassa.smartpossupply.domain.stocktransfer.*;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseProduct;
import uz.uzkassa.smartpossupply.dto.bank.BankDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDateDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonNameDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.category.CategoryDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDTO;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.dto.payload.OneCIntegrationPayload;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.dto.product.ProductCapacityDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductDTO;
import uz.uzkassa.smartpossupply.dto.productcontract.ProductContractDTO;
import uz.uzkassa.smartpossupply.dto.productcontract.ProductContractLookupDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialDTO;
import uz.uzkassa.smartpossupply.dto.stock.*;
import uz.uzkassa.smartpossupply.dto.stockin.StockInDTO;
import uz.uzkassa.smartpossupply.dto.stockin.StockInDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockin.StockInListDTO;
import uz.uzkassa.smartpossupply.dto.stockin.StockInProductDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockin.onec.StockIn1CDTO;
import uz.uzkassa.smartpossupply.dto.stockin.onec.StockIn1CSyncDTO;
import uz.uzkassa.smartpossupply.dto.stockin.onec.StockInProduct1CDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockBalanceReport;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportDTO;
import uz.uzkassa.smartpossupply.dto.supplier.SupplierDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.filters.StockInProductFilter;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.integration.OneCClient;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqNotificationProducer;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqOneCIntegrationProducer;
import uz.uzkassa.smartpossupply.repository.*;
import uz.uzkassa.smartpossupply.utils.CalcUtils;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
@Service
//@Transactional(readOnly = true)
@FieldDefaults(level = PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class StockInService extends BaseService {

    StockInRepository stockInRepository;
    StockOutRepository stockOutRepository;
    StockProductRepository stockProductRepository;
    BankRepository bankRepository;
    WarehouseProductRepository warehouseProductRepository;
    WarehouseRepository warehouseRepository;
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    StockProductAggregationRepository stockProductAggregationRepository;
    StockProductMarkRepository stockProductMarkRepository;
    StockProductAggregationBlockRepository stockProductAggregationBlockRepository;
    OrderRepository orderRepository;
    FacturaRepository facturaRepository;
    InvalidAggregationService invalidAggregationService;
    InvalidAggregationRepository invalidAggregationRepository;
    MarkRepository markRepository;
    AggregationRepository aggregationRepository;
    PartyRepository partyRepository;
    PartnerRepository partnerRepository;
    AicRepository aicRepository;
    AcceptanceTransferActRepository acceptanceTransferActRepository;
    ProductSerialService productSerialService;
    CompanyCommittentRepository companyCommittentRepository;
    SupplierService supplierService;
    ProductContractService productContractService;
    ProductService productService;
    AslBelgiClient aslBelgiClient;
    MarkOrderPartyRepository markOrderPartyRepository;
    WarehouseProductService warehouseProductService;
    OneCIntegrationRepository oneCIntegrationRepository;
    RabbitMqOneCIntegrationProducer rabbitMqOneCIntegrationProducer;
    OneCClient oneCClient;
    CategoryService categoryService;
    TransactionalExecutorService transactionalExecutorService;
    NumberingContext numberingContext;
    PackageQuantityService packageQuantityService;
    RabbitMqNotificationProducer notificationProducer;
    ValidationContext validationContext;

    @Transactional
    public void createByOrder(Order order) {
        Map<CompanyCommittent, List<OrderProduct>> committentMap = new HashMap<>();
        order.getProducts().forEach(orderProduct -> {
            if (orderProduct.getCompanyCommittent() != null) {
                if (committentMap.get(orderProduct.getCompanyCommittent()) == null) {
                    List<OrderProduct> orderProducts = new ArrayList<>();
                    orderProducts.add(orderProduct);
                    committentMap.put(orderProduct.getCompanyCommittent(), orderProducts);
                } else {
                    committentMap.get(orderProduct.getCompanyCommittent()).add(orderProduct);
                }
            }
        });

        if (!committentMap.isEmpty()) {
            for (CompanyCommittent companyCommittent : committentMap.keySet()) {
                this.createByOrderInterval(order, committentMap.get(companyCommittent), companyCommittent);
            }
        } else {
            this.createByOrderInterval(order, order.getProducts(), null);
        }
    }

    private void createByOrderInterval(Order order, List<OrderProduct> products, CompanyCommittent companyCommittent) {
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_IN, order.getCompanyId());

        Stock stock = new Stock();
        stock.setTransferType(TransferType.IN);
        stock.setTransferDate(order.getOrderDate());
        stock.setWarehouseId(order.getWarehouseId());
        stock.setBranchId(order.getBranchId());
        stock.setDescription(order.getDescription());
        stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stock.setStockNumber(numberingMap.get(NUMBER));
        stock.setContractId(order.getContractId());
        stock.setCustomerId(order.getCompanyId());
        stock.setSupplierId(companyCommittent != null ? companyCommittent.getCommittentId() : order.getCustomerId());
        stock.setCompanyCommittentId(companyCommittent != null ? companyCommittent.getId() : null);
        stock.setCompanyId(order.getCompanyId());
        stock.setCompany(order.getCompany());
        stock.setAdvancePaymentAmount(order.getAdvancePaymentAmount());
        stock.setAdvancePaymentPercent(order.getAdvancePaymentPercent());
        stock.setSpecificationNumber(order.getSpecificationNumber());
        stock.setSpecificationDate(order.getSpecificationDate());
        stock.setTotalBrutto(order.getTotalBrutto());
        stock.setTotalNetto(order.getTotalNetto());

        stock.setOrderId(order.getId());
        stock = stockInRepository.save(stock);

        order.setStockInId(stock.getId());
        orderRepository.save(order);

        BigDecimal grandTotalQty = BigDecimal.ZERO;
        BigDecimal grandTotalPrice = BigDecimal.ZERO;
        BigDecimal grandTotalPriceWithoutVat = BigDecimal.ZERO;
        BigDecimal grandTotalVatPrice = BigDecimal.ZERO;

        for (OrderProduct orderProduct : products) {
            StockProduct stockProduct = new StockProduct();
            stockProduct.setProductId(orderProduct.getProductId());
            stockProduct.setWarehouseId(orderProduct.getWarehouseId());
            stockProduct.setUnitId(orderProduct.getUnitId());
            stockProduct.setQty(orderProduct.getQty());

            stockProduct.setPurchasePrice(orderProduct.getPrice());
            stockProduct.setTotalPurchasePrice(orderProduct.getTotalPrice());
            stockProduct.setPurchasePriceWithoutVat(CalcUtils.getPriceWithoutVat(orderProduct.getPrice(), orderProduct.getVatRate()));
            stockProduct.setTotalPurchasePriceWithoutVat(CalcUtils.getPriceWithoutVat(orderProduct.getTotalPrice(), orderProduct.getVatRate()));

            stockProduct.setSellingPrice(orderProduct.getPrice());
            stockProduct.setTotalSellingPrice(orderProduct.getTotalPrice());
            stockProduct.setSellingPriceWithoutVat(CalcUtils.getPriceWithoutVat(orderProduct.getPrice(), orderProduct.getVatRate()));
            stockProduct.setTotalSellingPriceWithoutVat(CalcUtils.getPriceWithoutVat(orderProduct.getTotalPrice(), orderProduct.getVatRate()));

            stockProduct.setMarkupPercent(orderProduct.getMarkupPercent());
            stockProduct.setMarkupPrice(orderProduct.getMarkupPrice());
            stockProduct.setVatRate(orderProduct.getVatRate());
            stockProduct.setVatPrice(orderProduct.getVatPrice());
            stockProduct.setTotalVatPrice(orderProduct.getTotalVatPrice());
            stockProduct.setExciseRate(orderProduct.getExciseRate());
            stockProduct.setExciseSum(orderProduct.getExciseSum());

            stockProduct.setExpiryDate(orderProduct.getExpiryDate());
            stockProduct.setCode1C(orderProduct.getCode1C());
            stockProduct.setBasePrice(orderProduct.getBasePrice());
            stockProduct.setProductSerialId(orderProduct.getProductSerialId());
            stockProduct.setCompanyCommittentId(orderProduct.getCompanyCommittentId());
            stockProduct.setProductContractId(orderProduct.getProductContractId());
            stockProduct.setTotalBrutto(orderProduct.getTotalBrutto());
            stockProduct.setTotalNetto(orderProduct.getTotalNetto());
            stockProduct.setLgotaVatSum(orderProduct.getLgotaVatSum());

            grandTotalQty = grandTotalQty.add(orderProduct.getQty());
            if (stockProduct.getTotalPurchasePrice() != null) {
                grandTotalPrice = grandTotalPrice.add(stockProduct.getTotalPurchasePrice());
            }
            if (stockProduct.getTotalPurchasePriceWithoutVat() != null) {
                grandTotalPriceWithoutVat = grandTotalPriceWithoutVat.add(stockProduct.getTotalPurchasePriceWithoutVat());
            }
            if (stockProduct.getTotalVatPrice() != null) {
                grandTotalVatPrice = grandTotalVatPrice.add(stockProduct.getTotalVatPrice());
            }

            stock.addProduct(stockProduct);
        }

        stock.setTotalQty(grandTotalQty);
        stock.setTotalPrice(grandTotalPrice);
        stock.setTotalPriceWithoutVat(grandTotalPriceWithoutVat);
        stock.setTotalVatPrice(grandTotalVatPrice);
        stock.setStatus(StockStatus.COMPLETED);
        stockInRepository.save(stock);
        if (stock.getWarehouseId() != null) {
            warehouseProductService.in(stock, false);
        } else {
            warehouseProductService.inToMultiWarehouse(stock, false);
        }
        CompanySettings settings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(stock.getCompanyId(), GenericSettingsType.ONE_C_SYNC_ENABLED).orElse(null);
        if (settings != null) {
            if (settings.isEnabled()) {
                this.sendToQueue(new StockIn1CSyncDTO(List.of(stock.getId())));
            }
        } else if (stock.getCompany().isOneCEnabled()) {
            this.sendToQueue(new StockIn1CSyncDTO(List.of(stock.getId())));
        }
    }

    @Transactional
    public Long createByAggregation(Long aggregationId) {
        Aggregation aggregation = aggregationRepository.findById(aggregationId).orElseThrow(validationService.entityNotFoundExceptionThrow(Aggregation.class.getSimpleName(), "id", aggregationId));
        Unit unit = getUnit();
        Company company = getCurrentCompany();
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_IN, company.getId());

        Stock stock = new Stock();
        stock.setTransferType(TransferType.IN);
        stock.setTransferDate(aggregation.getCreatedDate());
        stock.setDescription(aggregation.getDescription());
        stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stock.setStockNumber(numberingMap.get(NUMBER));
        stock.setCustomerId(company.getId());
        stock.setCompanyId(company.getId());
        stock = stockInRepository.save(stock);

        BigDecimal grandTotalQty = BigDecimal.ZERO;
        BigDecimal grandTotalPrice = BigDecimal.ZERO;
        BigDecimal grandTotalPriceWithoutVat = BigDecimal.ZERO;
        BigDecimal grandTotalVatPrice = BigDecimal.ZERO;

        Product product = aggregation.getProduct();
        StockProduct stockProduct = new StockProduct();
        stockProduct.setProductId(product.getId());
        stockProduct.setUnitId(unit.getId());
        stockProduct.setQty(BigDecimal.ONE);

        stockProduct.setPurchasePrice(product.getPrice());
        stockProduct.setTotalPurchasePrice(product.getPrice());

        stockProduct.setPurchasePriceWithoutVat(CalcUtils.getPriceWithoutVat(product.getPrice(), product.getVat()));
        stockProduct.setTotalPurchasePriceWithoutVat(CalcUtils.getPriceWithoutVat(product.getPrice(), product.getVat()));

        stockProduct.setSellingPrice(product.getPrice());
        stockProduct.setTotalSellingPrice(product.getPrice());

        stockProduct.setSellingPriceWithoutVat(stockProduct.getPurchasePriceWithoutVat());
        stockProduct.setTotalSellingPriceWithoutVat(stockProduct.getTotalPurchasePriceWithoutVat());

        stockProduct.setMarkupPercent(BigDecimal.ZERO);
        stockProduct.setMarkupPrice(BigDecimal.ZERO);

        stockProduct.setVatRate(product.getVat().getAmount());

        if (Vat.WITHOUT_VAT.equals(product.getVat())) {
            stockProduct.setVatPrice(BigDecimal.ZERO);
            stockProduct.setTotalVatPrice(BigDecimal.ZERO);
        } else {
            BigDecimal priceWithoutVat = CalcUtils.getPriceWithoutVat(product.getPrice(), product.getVat());
            BigDecimal vatPrice = product.getPrice().subtract(priceWithoutVat);
            stockProduct.setVatPrice(vatPrice);
            stockProduct.setTotalVatPrice(vatPrice);
        }

        stockProduct.setExciseRate(product.getExciseRate());
        stockProduct.setExciseSum(product.getExciseSum());

        stockProduct.setStockId(stock.getId());
        stockProduct.setStock(stock);
        stockProduct.setHasMarking(true);
        stockProduct.setAggregationQty(1);
        stockProduct = stockProductRepository.save(stockProduct);

        grandTotalQty = grandTotalQty.add(BigDecimal.ONE);
        grandTotalPrice = grandTotalPrice.add(stockProduct.getTotalPurchasePrice());
        grandTotalPriceWithoutVat = grandTotalPriceWithoutVat.add(stockProduct.getTotalPurchasePriceWithoutVat());
        grandTotalVatPrice = grandTotalVatPrice.add(stockProduct.getTotalVatPrice());

        {
            StockProductAggregation stockProductAggregation = new StockProductAggregation();
            stockProductAggregation.setCode(aggregation.getCode());
            stockProductAggregation.setOriginalCode(aggregation.getOriginalCode());
            stockProductAggregation.setProductId(product.getId());
            stockProductAggregation.setTransferType(TransferType.IN);
            stockProduct.addStockProductAggregation(stockProductAggregation);
        }

        if (ProductType.isContainGroupMarkOrder(product.getProductType())) {
            {
                List<Aggregation> aggregations = aggregationRepository.findAllByParentCode(aggregation.getCode());
                if (!CollectionUtils.isEmpty(aggregations)) {
                    stockProduct.setAggregationQty(aggregations.size() + 1);
                    for (Aggregation blockAggregation : aggregations) {
                        StockProductAggregation stockProductAggregation = new StockProductAggregation();
                        stockProductAggregation.setCode(blockAggregation.getCode());
                        stockProductAggregation.setOriginalCode(blockAggregation.getOriginalCode());
                        stockProductAggregation.setProductId(product.getId());
                        stockProductAggregation.setTransferType(TransferType.IN);
                        stockProductAggregation.setUsed(false);
                        stockProduct.addStockProductAggregation(stockProductAggregation);
                    }
                }
            }
        } else {
            List<Mark> marks = markRepository.findAllByParentCodeOrderByCreatedDateDesc(aggregation.getCode());
            if (!CollectionUtils.isEmpty(marks)) {
                stockProduct.setMarkQty(marks.size());
                for (Mark mark : marks) {
                    StockProductMark stockProductMark = new StockProductMark();
                    stockProductMark.setOriginalCode(mark.getOriginalCode());
                    stockProductMark.setCode(mark.getCode());
                    stockProductMark.setQty(BigDecimal.ONE);
                    stockProductMark.setProductId(product.getId());
                    stockProductMark.setTransferType(TransferType.IN);
                    stockProductMark.setUsed(false);
                    stockProduct.addStockProductMark(stockProductMark);
                }
            }
        }

        stock.setTotalQty(grandTotalQty);
        stock.setTotalPrice(grandTotalPrice);
        stock.setTotalPriceWithoutVat(grandTotalPriceWithoutVat);
        stock.setTotalVatPrice(grandTotalVatPrice);

        stockInRepository.save(stock);
        return stock.getId();
    }

    @Transactional
    public Long create(StockInDTO stockInDTO) {
        validationContext.validate(stockInDTO);
        EnvironmentType environmentType = SecurityUtils.getEnvironmentType();
        Company company = getCurrentCompany();
        if (stockInDTO.getFacturaId() != null) {
            stockInRepository.findFirstByCompanyIdAndFacturaIdAndTransferTypeAndDeletedIsFalse(company.getId(), stockInDTO.getFacturaId(), TransferType.IN).ifPresent(stock -> {
                if (StockStatus.CANCELLED != stock.getStatus()) {
                    throw validationService.conflictExceptionThrow(
                            localizationService.localize(
                                    "there.was.income.by.this.invoice",
                                    new Object[]{stock.getStockNumber(), stock.getFactura().getFacturaNo()}
                            )
                    ).get();
                }
            });
        }
        if (stockInDTO.getPartyId() != null) {
            stockInRepository.findFirstByCompanyIdAndPartyIdAndTransferTypeAndDeletedIsFalse(company.getId(), stockInDTO.getPartyId(), TransferType.IN)
                    .ifPresent(stock -> {
                        if (StockStatus.CANCELLED != stock.getStatus()) {
                            throw validationService.conflictExceptionThrow(
                                    localizationService.localize(
                                            "there.was.income.by.this.party",
                                            new Object[]{stock.getStockNumber(), stock.getParty().getPartyNumber()}
                                    )
                            ).get();
                        }
                    });
        }

        if (stockInDTO.getAicId() != null) {
            stockInRepository.findFirstByCompanyIdAndAicIdAndTransferTypeAndDeletedIsFalse(company.getId(), stockInDTO.getAicId(), TransferType.IN)
                    .ifPresent(stock -> {
                        if (StockStatus.CANCELLED != stock.getStatus()) {
                            throw validationService.conflictExceptionThrow(
                                    localizationService.localize(
                                            "there.was.income.by.this.aic",
                                            new Object[]{stock.getStockNumber(), stock.getAic().getReportId()}
                                    )
                            ).get();
                        }
                    });
        }

        if (stockInDTO.getAcceptanceTransferActId() != null) {
            stockInRepository.findFirstByCompanyIdAndAcceptanceTransferActIdAndTransferTypeAndDeletedIsFalse(company.getId(), stockInDTO.getAcceptanceTransferActId(), TransferType.IN)
                    .ifPresent(stock -> {
                        if (StockStatus.CANCELLED != stock.getStatus()) {
                            throw validationService.conflictExceptionThrow(
                                    localizationService.localize(
                                            "there.was.income.by.this.act",
                                            new Object[]{stock.getAcceptanceTransferAct().getActNo(), stock.getStockNumber()}
                                    )
                            ).get();
                        }
                    });
        }

        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_IN, company.getId());
        Company supplier;
        if (stockInDTO.getSupplier().getId() != null) {
            supplier =
                    companyRepository
                            .findById(stockInDTO.getSupplier().getId())
                            .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", stockInDTO.getSupplier().getId()));
        } else if (StringUtils.isNotEmpty(stockInDTO.getSupplier().getTin())) {
            supplier = companyRepository.findFirstByTinAndDeletedIsFalse(stockInDTO.getSupplier().getTin()).orElse(new Company());
        } else {
            supplier = new Company();
        }
        if (supplier.getId() == null) {
            SupplierDTO supplierDTO = new SupplierDTO();
            supplierDTO.setTin(stockInDTO.getSupplier().getTin());
            supplierDTO.setName(stockInDTO.getSupplier().getName());
            supplierDTO.setCompanyId(getCurrentCompanyId());

            if (stockInDTO.getSupplier().getBank() != null) {
                Bank bank = bankRepository.findByMfo(stockInDTO.getSupplier().getBank().getMfo()).orElse(null);
                if (bank != null) {
                    CompanyBankDTO supplierBankDTO = new CompanyBankDTO();
                    supplierBankDTO.setBankId(bank.getId());
                    supplierBankDTO.setAccountNumber(stockInDTO.getSupplier().getBank().getAccountNumber());
                    supplierBankDTO.setOked(stockInDTO.getSupplier().getBank().getOked());
                    supplierBankDTO.setMain(true);
                    supplierDTO.setBank(supplierBankDTO);
                }
            }
            supplier = supplierService.create(supplierDTO);
        }

        Stock stock = new Stock();
        stock.setTransferType(TransferType.IN);
        stock.setTransferDate(Optional.ofNullable(stockInDTO.getTransferDate()).orElse(LocalDateTime.now()));

        if (stockInDTO.getFacturaId() != null) {
            Factura factura = facturaRepository
                    .findById(stockInDTO.getFacturaId())
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", stockInDTO.getFacturaId()));
            factura.setStockInId(stock.getId());
            stock.setFacturaId(factura.getId());
            stock.setFactura(factura);
        }
        if (stockInDTO.getPartyId() != null) {
            Party party = partyRepository.findById(stockInDTO.getPartyId())
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(Party.class.getSimpleName(), "id", stockInDTO.getPartyId()));
            party.setStockId(stock.getId());
            stock.setPartyId(party.getId());
        }
        if (stockInDTO.getAicId() != null) {
            Aic aic = aicRepository.findById(stockInDTO.getAicId())
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(Aic.class.getSimpleName(), "id", stockInDTO.getAicId()));
            aic.setStockId(stock.getId());
            stock.setAicId(aic.getId());
        }
        stock.setAcceptanceTransferActId(stockInDTO.getAcceptanceTransferActId());
        stock.setWarehouseId(stockInDTO.getWarehouseId());
        stock.setBranchId(stockInDTO.getBranchId());
        stock.setDescription(stockInDTO.getDescription());
        stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stock.setStockNumber(numberingMap.get(NUMBER));
        stock.setCustomerId(company.getId());
        stock.setSupplierId(supplier.getId());
        stock.setCompanyCommittentId(stockInDTO.getCompanyCommittentId());
        stock.setCompanyId(company.getId());
        stock.setTotalQty(stockInDTO.getTotalQty());
        stock.setTotalPrice(stockInDTO.getTotalPrice());
        stock.setTotalForeignPrice(stockInDTO.getTotalForeignPrice());
        stock.setExchangeRate(stockInDTO.getExchangeRate());
        stock.setCurrencyType(stockInDTO.getCurrencyType());
        stock.setTotalPriceWithoutVat(stockInDTO.getTotalPriceWithoutVat());
        stock.setTotalVatPrice(stockInDTO.getTotalVatPrice());
        stock.setTotalBrutto(stockInDTO.getTotalBrutto());
        stock.setTotalNetto(stockInDTO.getTotalNetto());
        stock = stockInRepository.save(stock);
        if (stock.getFacturaId() != null) {
            Factura factura = stock.getFactura();
            factura.setStockInId(stock.getId());
            facturaRepository.save(factura);
        }

        Unit unit = getUnit();
        boolean hasMark = false;
        for (StockInProductDTO stockInProductDTO : stockInDTO.getProducts()) {
            Product product;
            if (stockInProductDTO.getId() != null && stockInProductDTO.getId() > 0) {
                product =
                        productRepository
                                .findById(stockInProductDTO.getId())
                                .orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockInProductDTO.getId()));
            } else {
                product = productService.getProductByBarcodeAndVatBarcode(stockInProductDTO.getBarcode(), stockInProductDTO.getVatBarCode(), stockInDTO.getBranchId());
            }

            if (product == null) {
                Category category = null;
                if (StringUtils.isNotEmpty(stockInProductDTO.getVatBarCode())) {
                    category =
                            categoryRepository.findFirstByBranchIdAndCatalogCodeAndEnvironmentTypeAndDeletedIsFalse(
                                    stockInDTO.getBranchId(),
                                    MarkUtils.clearVatBarcode(stockInProductDTO.getVatBarCode()),
                                    environmentType
                            ).orElse(null);
                }
                if (category == null) {
                    category = categoryRepository.findFirstByMainIsTrueAndBranchIdAndDeletedIsFalse(stockInDTO.getBranchId()).orElse(null);
                    if (category == null) {
                        category =
                                categoryRepository
                                        .findFirstByBranchIdAndDeletedIsFalse(stockInDTO.getBranchId())
                                        .orElseThrow(validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.BEFORE_STOCK_IN_SYNC_YOUR_CATALOG)));
                    }
                }

                ProductDTO productDTO = ProductDTO
                        .builder()
                        .name(stockInProductDTO.getName())
                        .vatBarcode(stockInProductDTO.getVatBarCode())
                        .barcode(stockInProductDTO.getBarcode())
                        .categoryIds(List.of(category.getId()))
                        .unitId(unit.getId())
                        .companyId(company.getId())
                        .branchId(stockInDTO.getBranchId())
                        .vat(Vat.getByAmount(stockInProductDTO.getVatRate()))
                        .price(stockInProductDTO.getSellingPrice())
                        .companyCommittentId(stockInDTO.getCompanyCommittentId())
                        .productType(MarkUtils.getProductType(company.getProductGroup(), stockInProductDTO.getVatBarCode()))
                        .hasMark(stockInProductDTO.isHasMark())
                        .build();
                product = productService.createAndReturn(productDTO);
            }

            if (stockInProductDTO.getProductType() != null && product.getProductType() == null) {
                product.setProductType(stockInProductDTO.getProductType());
                product.setHasMark(stockInProductDTO.isHasMark());
                product = productRepository.save(product);
            }

            StockProduct stockProduct = new StockProduct();
            stockProduct.setProductId(product.getId());
            stockProduct.setName(stockInProductDTO.getStockProductName());
            stockProduct.setUnitId(unit.getId());
            stockProduct.setQty(stockInProductDTO.getQty());
            stockProduct.setBasePrice(stockInProductDTO.getBasePrice());

            stockProduct.setPurchasePrice(stockInProductDTO.getPurchasePrice());
            stockProduct.setPurchaseForeignPrice(stockInProductDTO.getPurchaseForeignPrice());
            stockProduct.setPurchasePriceWithoutVat(stockInProductDTO.getPurchasePriceWithoutVat());
            stockProduct.setTotalPurchasePrice(stockInProductDTO.getTotalPurchasePrice());
            stockProduct.setTotalPurchaseForeignPrice(stockInProductDTO.getTotalPurchaseForeignPrice());
            stockProduct.setTotalPurchasePriceWithoutVat(stockInProductDTO.getTotalPurchasePriceWithoutVat());

            stockProduct.setSellingPrice(stockInProductDTO.getSellingPrice());
            stockProduct.setSellingForeignPrice(stockInProductDTO.getSellingForeignPrice());
            stockProduct.setSellingPriceWithoutVat(stockInProductDTO.getSellingPriceWithoutVat());
            stockProduct.setTotalSellingPrice(stockInProductDTO.getTotalSellingPrice());
            stockProduct.setTotalSellingForeignPrice(stockInProductDTO.getTotalSellingForeignPrice());
            stockProduct.setTotalSellingPriceWithoutVat(stockInProductDTO.getTotalSellingPriceWithoutVat());

            stockProduct.setMarkupPercent(stockInProductDTO.getMarkupPercent());
            stockProduct.setMarkupPrice(stockInProductDTO.getMarkupPrice());
            stockProduct.setFacturaProfitRate(stockInProductDTO.getFacturaProfitRate());

            stockProduct.setVatRate(stockInProductDTO.getVatRate());
            stockProduct.setVatPrice(stockInProductDTO.getVatPrice());
            stockProduct.setTotalVatPrice(stockInProductDTO.getTotalVatPrice());

            stockProduct.setExciseRate(stockInProductDTO.getExciseRate());
            stockProduct.setExciseSum(stockInProductDTO.getExciseSum());

            if (StringUtils.isNotEmpty(stockInProductDTO.getSerialNumber())) {
                ProductSerial productSerial = productSerialService.createOrGet(ProductSerialDTO
                        .builder()
                        .productId(product.getId())
                        .serialNumber(stockInProductDTO.getSerialNumber())
                        .baseSumma(stockInProductDTO.getBasePrice())
                        .build()
                );
                stockProduct.setProductSerialId(productSerial.getId());
                stockProduct.setProductSerial(productSerial);
            }
            stockProduct.setCompanyCommittentId(stockInDTO.getCompanyCommittentId());
            stockProduct.setHasMarking(!CollectionUtils.isEmpty(stockInProductDTO.getMarkCodes()) || !CollectionUtils.isEmpty(stockInProductDTO.getAggregationCodes()) || !CollectionUtils.isEmpty(stockInProductDTO.getAggregationBlocks()));
            stockProduct.setMarkQty(Optional.ofNullable(stockInProductDTO.getMarkCodes()).orElse(new ArrayList<>()).size());
            stockProduct.setAggregationQty(Optional.ofNullable(stockInProductDTO.getAggregationCodes()).orElse(new ArrayList<>()).size());
            stockProduct.setAggregationBlockQty(Optional.ofNullable(stockInProductDTO.getAggregationBlocks()).orElse(new ArrayList<>()).size());
            stockProduct.setTotalBrutto(stockInProductDTO.getTotalBrutto());
            stockProduct.setTotalNetto(stockInProductDTO.getTotalNetto());
            stockProduct.setStockId(stock.getId());
            stockProduct.setStock(stock);
            stockProduct = stockProductRepository.save(stockProduct);

            if (!CollectionUtils.isEmpty(stockInProductDTO.getPackageQuantities())) {
                packageQuantityService.create(stockProduct, stockInProductDTO.getPackageQuantities());
            }

            if (stockProduct.isHasMarking()) {
                hasMark = true;
            }

            if (!CollectionUtils.isEmpty(stockInProductDTO.getAggregationCodes())) {
                for (String aggregationCode : stockInProductDTO.getAggregationCodes()) {
                    StockProductAggregation stockProductAggregation = new StockProductAggregation();
                    String aggregationCleanCode = MarkUtils.clearAggregationCode(aggregationCode);
                    stockProductAggregation.setCode(aggregationCleanCode);
                    stockProductAggregation.setOriginalCode(aggregationCode);
                    stockProductAggregation.setProductId(product.getId());
                    stockProductAggregation.setTransferType(TransferType.IN);
                    stockProductAggregation.setCompanyCommittentId(stockInDTO.getCompanyCommittentId());
                    stockProductAggregation.setUsed(false);
                    stockProduct.addStockProductAggregation(stockProductAggregation);
                }
            }

            if (!CollectionUtils.isEmpty(stockInProductDTO.getAggregationBlocks())) {
                for (String aggregationBlockCode : stockInProductDTO.getAggregationBlocks()) {
                    StockProductAggregationBlock stockProductAggregationBlock = new StockProductAggregationBlock();
                    String blockCleanCode = MarkUtils.clearOriginalCode(aggregationBlockCode, product.getProductType());

                    stockProductAggregationBlock.setCode(blockCleanCode);
                    stockProductAggregationBlock.setOriginalCode(aggregationBlockCode);
                    stockProductAggregationBlock.setProductId(product.getId());
                    stockProductAggregationBlock.setTransferType(TransferType.IN);
                    stockProductAggregationBlock.setCompanyCommittentId(stockInDTO.getCompanyCommittentId());
                    stockProductAggregationBlock.setUsed(false);
                    stockProduct.addStockProductAggregationBlock(stockProductAggregationBlock);
                }
            }

            if (!CollectionUtils.isEmpty(stockInProductDTO.getMarkCodes())) {
                for (String markCode : stockInProductDTO.getMarkCodes()) {
                    StockProductMark stockProductMark = new StockProductMark();
                    String markCleanCode = MarkUtils.clearOriginalCode(markCode, product.getProductType());
                    stockProductMark.setCode(markCleanCode);
                    stockProductMark.setOriginalCode(markCode);
                    stockProductMark.setQty(BigDecimal.ONE);
                    stockProductMark.setProductId(product.getId());
                    stockProductMark.setTransferType(TransferType.IN);
                    stockProductMark.setCompanyCommittentId(stockInDTO.getCompanyCommittentId());
                    stockProductMark.setUsed(false);
                    stockProduct.addStockProductMark(stockProductMark);
                }
            }
            if (stockInDTO.isHasContract()) {
                if (stockInProductDTO.getProductContractId() != null) {
                    stockProduct.setProductContractId(stockInProductDTO.getProductContractId());
                } else if (StringUtils.isNotEmpty(stockInProductDTO.getStockProductName()) && stockInProductDTO.getBasePrice() != null && stockInProductDTO.getFacturaProfitRate() != null) {
                    ProductContract productContract = productContractService.createOrGet(
                            ProductContractDTO
                                    .builder()
                                    .name(stockInProductDTO.getStockProductName())
                                    .productId(product.getId())
                                    .basePrice(stockInProductDTO.getBasePrice())
                                    .profitRate(stockInProductDTO.getFacturaProfitRate())
                                    .price(stockInProductDTO.getSellingPriceWithoutVat())
                                    .branchId(stockInDTO.getBranchId())
                                    .build()
                    );
                    stockProduct.setProductContractId(productContract.getId());
                    stockProduct.setProductContract(productContract);
                }
            }
        }

        if (hasMark) {
            stock.setStatus(StockStatus.CHECKING);
            stockInRepository.save(stock);
            CompanySettings xtraceSettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.XTRACE_ENABLED).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanySettings.class.getSimpleName(), "companyId", getCurrentCompanyId()));
            CheckAslBelgiDTO checkAslBelgiDTO = new CheckAslBelgiDTO();
            checkAslBelgiDTO.setStockId(stock.getId());
            checkAslBelgiDTO.setToken(xtraceSettings.isEnabled() ? this.getXTraceInfo().getApiKey() : null);
            checkAslBelgiDTO.setXtraceEnabled(xtraceSettings.isEnabled());
            checkAslBelgiDTO.setFromFactura(stock.getFacturaId() != null);
            invalidAggregationService.check(checkAslBelgiDTO);
        }
        return stock.getId();
    }

    @Transactional
    public Long update(Long id, StockInDTO stockInDTO) {
        validationContext.validate(stockInDTO);
        Stock stock = stockInRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));

        this.validateCompany(stock.getCompanyId());

        if (!(StockStatus.DRAFT.equals(stock.getStatus()) || StockStatus.CHECKING_ERROR.equals(stock.getStatus()))) {
            throw validationService.badRequestExceptionThrow("Вы можете изменить только черновик и на проверке статусы").get();
        }

        invalidAggregationRepository.deleteAllByStockId(id);

        stock.setTransferDate(stockInDTO.getTransferDate());
        stock.setDescription(stockInDTO.getDescription());

        if (stockInDTO.getFacturaId() != null) {
            Factura factura = facturaRepository.findById(stockInDTO.getFacturaId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", stockInDTO.getFacturaId()));
            stock.setFactura(factura);
            stock.setFacturaId(factura.getId());
            factura.setStockInId(stock.getId());
            facturaRepository.save(factura);
        }

        if (stockInDTO.getPartyId() != null) {
            Party party = partyRepository.findById(stockInDTO.getPartyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Party.class.getSimpleName(), "id", stockInDTO.getPartyId()));
            stock.setParty(party);
            stock.setPartyId(party.getId());
        }

        if (stockInDTO.getAcceptanceTransferActId() != null) {
            AcceptanceTransferAct acceptanceTransferAct = acceptanceTransferActRepository.findById(stockInDTO.getAcceptanceTransferActId()).orElseThrow(validationService.entityNotFoundExceptionThrow(AcceptanceTransferAct.class.getSimpleName(), "id", stockInDTO.getAcceptanceTransferActId()));
            stock.setAcceptanceTransferAct(acceptanceTransferAct);
            stock.setAcceptanceTransferActId(acceptanceTransferAct.getId());
        }

        if (stockInDTO.getCompanyCommittentId() != null) {
            CompanyCommittent companyCommittent = companyCommittentRepository.findById(stockInDTO.getCompanyCommittentId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanyCommittent.class.getSimpleName(), "id", stockInDTO.getCompanyCommittentId()));
            stock.setCompanyCommittent(companyCommittent);
            stock.setCompanyCommittentId(companyCommittent.getId());
        }

        if (stockInDTO.getStatus() != null) {
            stock.setStatus(stockInDTO.getStatus());
        }
        if (
                stockInDTO.getSupplier() != null &&
                        stockInDTO.getSupplier().getId() != null &&
                        !stock.getSupplierId().equals(stockInDTO.getSupplier().getId())
        ) {
            if (!partnerRepository.existsBySupplierIdAndCustomerIdAndCompanyId(stockInDTO.getSupplier().getId(), stock.getCustomerId(), getCurrentCompanyId())) {
                partnerRepository.save(new Partners(stockInDTO.getSupplier().getId(), stock.getCustomerId(), getCurrentCompanyId()));
            }
        }

        Warehouse warehouse = warehouseRepository.findById(stockInDTO.getWarehouseId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Warehouse.class.getSimpleName(), "id", stockInDTO.getWarehouseId()));
        stock.setWarehouse(warehouse);
        stock.setWarehouseId(warehouse.getId());

        Branch branch = branchRepository.findById(stockInDTO.getBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", stockInDTO.getBranchId()));
        stock.setBranch(branch);
        stock.setBranchId(branch.getId());

        if (stockInDTO.getStockNumber() != null && !stockInDTO.getStockNumber().equals(stock.getStockNumber())) {
            if (
                    stockInRepository.existsStockByStockNumberAndCompanyIdAndTransferTypeAndIdNot(
                            stockInDTO.getStockNumber(),
                            stock.getCompanyId(),
                            stock.getTransferType(),
                            stock.getId()
                    )
            ) {
                throw validationService.conflictExceptionThrow(
                        localizationService.localize(
                                MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                                new Object[]{
                                        localizationService.localize(MessageConstants.STOCK_IN),
                                        localizationService.localize(MessageConstants.NUMBER),
                                        stockInDTO.getStockNumber(),
                                }
                        )
                ).get();
            }
            stock.setStockNumber(stockInDTO.getStockNumber());
        }

        stock.setTotalQty(stockInDTO.getTotalQty());
        stock.setTotalPrice(stockInDTO.getTotalPrice());
        stock.setTotalForeignPrice(stockInDTO.getTotalForeignPrice());
        stock.setExchangeRate(stockInDTO.getExchangeRate());
        stock.setCurrencyType(stockInDTO.getCurrencyType());
        stock.setTotalPriceWithoutVat(stockInDTO.getTotalPriceWithoutVat());
        stock.setTotalVatPrice(stockInDTO.getTotalVatPrice());
        stock.setTotalBrutto(stockInDTO.getTotalBrutto());
        stock.setTotalNetto(stockInDTO.getTotalNetto());

        Unit unit = getUnit();

        Map<Long, StockProduct> stockProductsMap = stock
                .getProducts()
                .stream()
                .collect(Collectors.toMap(StockProduct::getId, Function.identity()));

        boolean hasMark = false;
        for (StockInProductDTO stockInProductDTO : stockInDTO.getProducts()) {
            Product product;
            if (stockInProductDTO.getId() != null && stockInProductDTO.getId() > 0) {
                product = productRepository.findById(stockInProductDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockInProductDTO.getId()));
            } else {
                product = productService.getProductByBarcodeAndVatBarcode(stockInProductDTO.getBarcode(), stockInProductDTO.getVatBarCode(), stockInDTO.getBranchId());
            }

            if (product == null) {
                Category category = categoryRepository
                        .findFirstByMainIsTrueAndBranchIdAndDeletedIsFalse(stockInDTO.getBranchId())
                        .orElse(null);
                if (category == null) {
                    category =
                            categoryRepository
                                    .findFirstByBranchIdAndDeletedIsFalse(stockInDTO.getBranchId())
                                    .orElseThrow(validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.BEFORE_STOCK_IN_SYNC_YOUR_CATALOG)));
                }

                ProductDTO productDTO = ProductDTO
                        .builder()
                        .name(stockInProductDTO.getName())
                        .vatBarcode(stockInProductDTO.getVatBarCode())
                        .barcode(stockInProductDTO.getBarcode())
                        .categoryIds(List.of(category.getId()))
                        .unitId(unit.getId())
                        .companyId(stock.getCompanyId())
                        .branchId(stockInDTO.getBranchId())
                        .vat(Vat.getByAmount(stockInProductDTO.getVatRate()))
                        .price(stockInProductDTO.getSellingPrice())
                        .companyCommittentId(stockInDTO.getCompanyCommittentId())
                        .productType(MarkUtils.getProductType(stock.getCompany().getProductGroup(), stockInProductDTO.getVatBarCode()))
                        .hasMark(MarkUtils.getProductType(stock.getCompany().getProductGroup(), stockInProductDTO.getVatBarCode()) != null)
                        .build();
                product = productService.createAndReturn(productDTO);
            }

            if (stockInProductDTO.getProductType() != null && product.getProductType() == null) {
                product.setProductType(stockInProductDTO.getProductType());
                product.setHasMark(true);
                product = productRepository.save(product);
            }

            StockProduct stockProduct;
            if (stockInProductDTO.getStockProductId() != null && stockProductsMap.get(stockInProductDTO.getStockProductId()) != null) {
                stockProduct = stockProductsMap.get(stockInProductDTO.getStockProductId());
            } else {
                stockProduct = new StockProduct();
            }

            stockProduct.setProductId(product.getId());
            stockProduct.setName(stockInProductDTO.getStockProductName());
            stockProduct.setUnitId(unit.getId());
            stockProduct.setQty(stockInProductDTO.getQty());
            stockProduct.setBasePrice(stockInProductDTO.getBasePrice());

            stockProduct.setPurchasePrice(stockInProductDTO.getPurchasePrice());
            stockProduct.setPurchaseForeignPrice(stockInProductDTO.getPurchaseForeignPrice());
            stockProduct.setPurchasePriceWithoutVat(stockInProductDTO.getPurchasePriceWithoutVat());
            stockProduct.setTotalPurchasePrice(stockInProductDTO.getTotalPurchasePrice());
            stockProduct.setTotalPurchaseForeignPrice(stockInProductDTO.getTotalPurchaseForeignPrice());
            stockProduct.setTotalPurchasePriceWithoutVat(stockInProductDTO.getTotalPurchasePriceWithoutVat());

            stockProduct.setSellingPrice(stockInProductDTO.getSellingPrice());
            stockProduct.setSellingForeignPrice(stockInProductDTO.getSellingForeignPrice());
            stockProduct.setSellingPriceWithoutVat(stockInProductDTO.getSellingPriceWithoutVat());
            stockProduct.setTotalSellingPrice(stockInProductDTO.getTotalSellingPrice());
            stockProduct.setTotalSellingForeignPrice(stockInProductDTO.getTotalSellingForeignPrice());
            stockProduct.setTotalSellingPriceWithoutVat(stockInProductDTO.getTotalSellingPriceWithoutVat());

            stockProduct.setMarkupPercent(stockInProductDTO.getMarkupPercent());
            stockProduct.setMarkupPrice(stockInProductDTO.getMarkupPrice());
            stockProduct.setFacturaProfitRate(stockInProductDTO.getFacturaProfitRate());

            stockProduct.setVatRate(stockInProductDTO.getVatRate());
            stockProduct.setVatPrice(stockInProductDTO.getVatPrice());
            stockProduct.setTotalVatPrice(stockInProductDTO.getTotalVatPrice());

            stockProduct.setExciseRate(stockInProductDTO.getExciseRate());
            stockProduct.setExciseSum(stockInProductDTO.getExciseSum());

            stockProduct.setProductSerialId(stockInProductDTO.getProductSerialId());
            if (StringUtils.isNotEmpty(stockInProductDTO.getSerialNumber())) {
                ProductSerial productSerial = productSerialService.createOrGet(
                        ProductSerialDTO
                                .builder()
                                .productId(product.getId())
                                .serialNumber(stockInProductDTO.getSerialNumber())
                                .baseSumma(stockInProductDTO.getBasePrice())
                                .build()
                );
                stockProduct.setProductSerialId(productSerial.getId());
                stockProduct.setProductSerial(productSerial);
            }
            stockProduct.setCompanyCommittentId(stockInDTO.getCompanyCommittentId());
            stockProduct.setTotalBrutto(stockInDTO.getTotalBrutto());
            stockProduct.setTotalNetto(stockInDTO.getTotalNetto());
            stockProduct.setStockId(stock.getId());
            stockProduct.setStock(stock);

            if (stockInDTO.isHasContract()) {
                if (stockInProductDTO.getProductContractId() != null) {
                    stockProduct.setProductContractId(stockInProductDTO.getProductContractId());
                } else if (StringUtils.isNotEmpty(stockInProductDTO.getStockProductName()) && stockInProductDTO.getBasePrice() != null && stockInProductDTO.getFacturaProfitRate() != null) {
                    ProductContract productContract = productContractService.createOrGet(
                            ProductContractDTO
                                    .builder()
                                    .name(stockInProductDTO.getStockProductName())
                                    .basePrice(stockInProductDTO.getBasePrice())
                                    .price(stockInProductDTO.getSellingPriceWithoutVat())
                                    .profitRate(stockInProductDTO.getFacturaProfitRate())
                                    .productId(product.getId())
                                    .branchId(stockInDTO.getBranchId())
                                    .build()
                    );
                    stockProduct.setProductContractId(productContract.getId());
                    stockProduct.setProductContract(productContract);
                }
            } else {
                stockProduct.setProductContractId(null);
                stockProduct.setProductContract(null);
            }

            stockProduct = stockProductRepository.save(stockProduct);
            boolean removeAggregations = false;
            if (stockInProductDTO.getAggregationCodes() != null) {
                removeAggregations = true;
                stockProductAggregationRepository.deleteAllByStockProductId(stockProduct.getId());
                if (!stockInProductDTO.getAggregationCodes().isEmpty()) {
                    for (String aggregationCode : stockInProductDTO.getAggregationCodes()) {
                        StockProductAggregation stockProductAggregation = new StockProductAggregation();
                        String aggregationCleanCode = MarkUtils.clearAggregationCode(aggregationCode);
                        stockProductAggregation.setCode(aggregationCleanCode);
                        stockProductAggregation.setOriginalCode(aggregationCode);
                        stockProductAggregation.setProductId(product.getId());
                        stockProductAggregation.setTransferType(TransferType.IN);
                        stockProductAggregation.setCompanyCommittentId(stockInDTO.getCompanyCommittentId());
                        stockProductAggregation.setUsed(false);
                        stockProduct.addStockProductAggregation(stockProductAggregation);
                    }
                }
            }

            if (stockInProductDTO.getMarkCodes() != null) {
                removeAggregations = true;
                stockProductMarkRepository.deleteAllByStockProductId(stockProduct.getId());
                if (!stockInProductDTO.getMarkCodes().isEmpty()) {
                    List<StockProductMark> stockProductMarks = new ArrayList<>();
                    for (String markCode : stockInProductDTO.getMarkCodes()) {
                        StockProductMark stockProductMark = new StockProductMark();
                        String markCleanCode = MarkUtils.clearOriginalCode(markCode, product.getProductType());
                        stockProductMark.setCode(markCleanCode);
                        stockProductMark.setOriginalCode(markCode);
                        stockProductMark.setQty(BigDecimal.ONE);
                        stockProductMark.setProductId(product.getId());
                        stockProductMark.setTransferType(TransferType.IN);
                        stockProductMark.setCompanyCommittentId(stockInDTO.getCompanyCommittentId());
                        stockProductMark.setUsed(false);

                        stockProduct.addStockProductMark(stockProductMark);
                    }
                    stockProductMarkRepository.saveAll(stockProductMarks);
                }
            }

            if (stockInProductDTO.getAggregationBlocks() != null) {
                removeAggregations = true;
                stockProductAggregationBlockRepository.deleteAllByStockProductId(stockProduct.getId());
                if (!stockInProductDTO.getAggregationBlocks().isEmpty()) {
                    List<StockProductAggregationBlock> stockProductAggregationBlocks = new ArrayList<>();
                    for (String aggregationBlockCode : stockInProductDTO.getAggregationBlocks()) {
                        StockProductAggregationBlock stockProductAggregationBlock = new StockProductAggregationBlock();
                        String blockCleanCode = MarkUtils.clearOriginalCode(aggregationBlockCode, product.getProductType());
                        stockProductAggregationBlock.setCode(blockCleanCode);
                        stockProductAggregationBlock.setOriginalCode(aggregationBlockCode);
                        stockProductAggregationBlock.setProductId(product.getId());
                        stockProductAggregationBlock.setTransferType(TransferType.IN);
                        stockProductAggregationBlock.setCompanyCommittentId(stockInDTO.getCompanyCommittentId());
                        stockProductAggregationBlock.setUsed(false);
                        stockProduct.addStockProductAggregationBlock(stockProductAggregationBlock);
                    }

                    stockProductAggregationBlockRepository.saveAll(stockProductAggregationBlocks);
                }
            }
            if (removeAggregations) {
                invalidAggregationRepository.deleteAllByStockProductId(stockProduct.getId());
            }

            if (stockProduct.getId() != null) {
                stockProductsMap.remove(stockProduct.getId());
            }

            stockProduct.setHasMarking(stockInProductDTO.isHasMarking() || !CollectionUtils.isEmpty(stockInProductDTO.getMarkCodes()) || !CollectionUtils.isEmpty(stockInProductDTO.getAggregationCodes()) || !CollectionUtils.isEmpty(stockInProductDTO.getAggregationBlocks()));
            stockProduct.setMarkQty(Optional.ofNullable(stockInProductDTO.getMarkCodes()).orElse(new ArrayList<>()).size());
            stockProduct.setAggregationQty(Optional.ofNullable(stockInProductDTO.getAggregationCodes()).orElse(new ArrayList<>()).size());
            stockProduct.setAggregationBlockQty(Optional.ofNullable(stockInProductDTO.getAggregationBlocks()).orElse(new ArrayList<>()).size());
            stockProductRepository.save(stockProduct);

            if (CollectionUtils.isEmpty(stockInProductDTO.getPackageQuantities())) {
                packageQuantityService.deleteByCompositeId(stockProduct);
            } else {
                packageQuantityService.create(stockProduct, stockInProductDTO.getPackageQuantities());
            }
            if (stockProduct.isHasMarking()) {
                hasMark = true;
            }
        }

        if (!CollectionUtils.isEmpty(stockProductsMap)) {
            List<Long> oldStockProductIds = new ArrayList<>(stockProductsMap.keySet());
            stockProductAggregationRepository.deleteAllByStockProductIds(oldStockProductIds);
            stockProductMarkRepository.deleteAllByStockProductIds(oldStockProductIds);
            stockProductAggregationBlockRepository.deleteAllByStockProductIds(oldStockProductIds);
            stockProductRepository.deleteByIds(oldStockProductIds);
        }

        if (hasMark) {
            stock.setStatus(StockStatus.CHECKING);
            stockInRepository.saveAndFlush(stock);
            stockProductRepository.updateStockProduct(stock.getId());
            CompanySettings xtraceSettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.XTRACE_ENABLED).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanySettings.class.getSimpleName(), "companyId", getCurrentCompanyId()));
            CheckAslBelgiDTO checkAslBelgiDTO = new CheckAslBelgiDTO();
            checkAslBelgiDTO.setStockId(stock.getId());
            checkAslBelgiDTO.setToken(xtraceSettings.isEnabled() ? getXTraceInfo().getApiKey() : null);
            checkAslBelgiDTO.setXtraceEnabled(xtraceSettings.isEnabled());
            checkAslBelgiDTO.setFromFactura(stock.getFacturaId() != null);
            invalidAggregationService.check(checkAslBelgiDTO);
        }
        return id;
    }

    public Page<StockInListDTO> getList(StockFilter filter) {
        ResultList<Stock> resultList = stockInRepository.getResultList(filter);
        List<StockInListDTO> result = resultList
                .getList()
                .stream()
                .map(stock -> {
                    StockInListDTO stockInListDTO = new StockInListDTO();
                    stockInListDTO.setId(stock.getId());
                    stockInListDTO.setStockNumber(stock.getStockNumber());
                    stockInListDTO.setTransferDate(stock.getTransferDate());
                    stockInListDTO.setSupplier(stock.getSupplier().toCommonBaseDTO());
                    stockInListDTO.setBranch(stock.getBranch().toCommonDTO());
                    stockInListDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
                    stockInListDTO.setTotalBrutto(stock.getTotalBrutto());
                    stockInListDTO.setTotalNetto(stock.getTotalNetto());
                    if (stock.getOldStockId() != null) {
                        stockInListDTO.setOldStock(stock.getOldStock().toCommonDTO());
                    }
                    if (stock.getStockTransfer() != null) {
                        stockInListDTO.setStockTransfer(stock.getStockTransfer().toCommonDTO());
                    }
                    stockInListDTO.setTotalPrice(stock.getTotalPrice());
                    stockInListDTO.setTotalForeignPrice(stock.getTotalForeignPrice());
                    stockInListDTO.setExchangeRate(stock.getExchangeRate());
                    stockInListDTO.setCurrencyType(stock.getCurrencyType());
                    stockInListDTO.setStatus(stock.getStatus());
                    if (stock.getRelationId() != null) {
                        stockInListDTO.setRelation(new CommonCodeDTO(stock.getRelationId(), stock.getRelationNumber(), stock.getRelationType().getCode()));
                    }
                    stockInListDTO.setOneCSync(stock.isOneCSync());
                    stockInListDTO.setLastModifiedDate(stock.getUpdatedDate());
                    return stockInListDTO;
                })
                .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public StockInDetailDTO get(Long id, Long customerId) {
        return stockInRepository
                .getDetail(id)
                .map(stock -> {
                    this.validateCompany(stock.getCompanyId());
                    if (StockStatus.CHECKING.equals(stock.getStatus())) {
                        transactionalExecutorService.runInNewTransaction(() -> warehouseProductService.checkStockStatus(stock));
                    }
                    StockInDetailDTO stockInDetailDTO = new StockInDetailDTO();
                    if (stock.getOrder() != null) {
                        stockInDetailDTO.setOrder(stock.getOrder().toCommonDTO());
                        stockInDetailDTO.setFrom1C(stock.getOrder().isFrom1C());
                        stockInDetailDTO.setFromHippo(stock.getOrder().isFromHippo());
                    }
                    stockInDetailDTO.setId(stock.getId());
                    stockInDetailDTO.setStockNumber(stock.getStockNumber());
                    stockInDetailDTO.setStatus(stock.getStatus());
                    stockInDetailDTO.setTransferDate(stock.getTransferDate());
                    if (stock.getSupplierId() != null) {
                        stockInDetailDTO.setSupplier(stock.getSupplier().toCommonBaseDTO());
                    }
                    if (stock.getCompanyCommittentId() != null) {
                        stockInDetailDTO.setCompanyCommittent(stock.getCompanyCommittent().toLookUpDTO());
                    }
                    stockInDetailDTO.setCustomer(stock.getCustomer().toCommonBaseDTO());
                    if (stock.getBranchId() != null) {
                        stockInDetailDTO.setBranch(stock.getBranch().toCommonDTO());
                    }
                    if (stock.getWarehouseId() != null) {
                        stockInDetailDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
                    }
                    stockInDetailDTO.setTotalQty(stock.getTotalQty());
                    stockInDetailDTO.setTotalPrice(stock.getTotalPrice());
                    stockInDetailDTO.setTotalPriceWithoutVat(stock.getTotalPriceWithoutVat());
                    stockInDetailDTO.setTotalVatPrice(stock.getTotalVatPrice());
                    stockInDetailDTO.setTotalForeignPrice(stock.getTotalForeignPrice());
                    stockInDetailDTO.setExchangeRate(stock.getExchangeRate());
                    stockInDetailDTO.setCurrencyType(stock.getCurrencyType());
                    stockInDetailDTO.setDescription(stock.getDescription());
                    stockInDetailDTO.setLastModifiedDate(stock.getUpdatedDate());
                    stockInDetailDTO.setTotalBrutto(stock.getTotalBrutto());
                    stockInDetailDTO.setTotalNetto(stock.getTotalNetto());
                    if (stock.getFacturaId() != null) {
                        stockInDetailDTO.setFactura(stock.getFactura().toLink());
                    }
                    if (stock.getPartyId() != null) {
                        stockInDetailDTO.setParty(stock.getParty().toCommonDTO());
                    }
                    if (stock.getStockTransfer() != null) {
                        stockInDetailDTO.setStockTransfer(stock.getStockTransfer().toCommonDTO());
                    }
                    if (stock.getOldStockId() != null) {
                        stockInDetailDTO.setOldStock(stock.getOldStock().toCommonDTO());
                    }
                    if (stock.getAcceptanceTransferAct() != null) {
                        stockInDetailDTO.setAcceptanceTransferAct(stock.getAcceptanceTransferAct().toLinkDTO());
                    }
                    if (stock.getAicId() != null) {
                        stockInDetailDTO.setAic(stock.getAic().toCommonDTO());
                    }
                    if (stock.getRelationId() != null) {
                        stockInDetailDTO.setRelation(new CommonCodeDTO(stock.getRelationId(), stock.getRelationNumber(), stock.getRelationType().getCode()));
                    }
                    stockOutRepository
                            .findFirstByStockInIdAndDeletedIsFalse(stock.getId())
                            .ifPresent(stockOut -> stockInDetailDTO.setStockOut(stockOut.toCommonDTO()));

                    for (StockProduct stockProduct : stock.getProducts()) {
                        StockInProductDetailDTO stockInProductDetailDTO = new StockInProductDetailDTO();
                        stockInProductDetailDTO.setStockProductId(stockProduct.getId());
                        stockInProductDetailDTO.setStockProductName(stockProduct.getName());
                        stockInProductDetailDTO.setId(stockProduct.getProduct().getId());
                        stockInProductDetailDTO.setName(stockProduct.getProduct().getName());
                        stockInProductDetailDTO.setProductType(stockProduct.getProduct().getProductType());
                        stockInProductDetailDTO.setVatBarCode(stockProduct.getProduct().getVatBarcode());
                        stockInProductDetailDTO.setBarcode(stockProduct.getProduct().getBarcode());
                        stockInProductDetailDTO.setCapacity(stockProduct.getProduct().getCapacity());
                        stockInProductDetailDTO.setQty(stockProduct.getQty());
                        stockInProductDetailDTO.setBasePrice(stockProduct.getBasePrice());

                        stockInProductDetailDTO.setBoxBarcode(stockProduct.getProduct().getBoxBarcode());
                        stockInProductDetailDTO.setBlockBarcode(stockProduct.getProduct().getBlockBarcode());
                        if (stockProduct.getProduct().getParent() != null) {
                            stockInProductDetailDTO.setParent(stockProduct.getProduct().getParent().toProductCommonDTO());
                            if (StringUtils.isEmpty(stockInProductDetailDTO.getBlockBarcode())) {
                                stockInProductDetailDTO.setBlockBarcode(stockProduct.getProduct().getParent().getBarcode());
                            }
                        }
                        stockInProductDetailDTO.setPackageCode(stockProduct.getProduct().getPackageCode());
                        stockInProductDetailDTO.setPackageName(stockProduct.getProduct().getPackageName());
                        stockInProductDetailDTO.setTuronQty(stockProduct.getTuronQty());
                        stockInProductDetailDTO.setTotalBrutto(stockProduct.getTotalBrutto());
                        stockInProductDetailDTO.setTotalNetto(stockProduct.getTotalNetto());

                        stockInProductDetailDTO.setPurchasePrice(stockProduct.getPurchasePrice());
                        stockInProductDetailDTO.setPurchaseForeignPrice(stockProduct.getPurchaseForeignPrice());
                        stockInProductDetailDTO.setPurchasePriceWithoutVat(stockProduct.getPurchasePriceWithoutVat());
                        stockInProductDetailDTO.setTotalPurchasePrice(stockProduct.getTotalPurchasePrice());
                        stockInProductDetailDTO.setTotalPurchaseForeignPrice(stockProduct.getTotalPurchaseForeignPrice());
                        stockInProductDetailDTO.setTotalPurchasePriceWithoutVat(stockProduct.getTotalPurchasePriceWithoutVat());

                        stockInProductDetailDTO.setSellingPrice(stockProduct.getSellingPrice());
                        stockInProductDetailDTO.setSellingForeignPrice(stockProduct.getSellingForeignPrice());
                        stockInProductDetailDTO.setSellingPriceWithoutVat(stockProduct.getSellingPriceWithoutVat());
                        stockInProductDetailDTO.setTotalSellingPrice(stockProduct.getTotalSellingPrice());
                        stockInProductDetailDTO.setTotalSellingForeignPrice(stockProduct.getTotalSellingForeignPrice());
                        stockInProductDetailDTO.setTotalSellingPriceWithoutVat(stockProduct.getTotalSellingPriceWithoutVat());

                        stockInProductDetailDTO.setMarkupPrice(stockProduct.getMarkupPrice());
                        stockInProductDetailDTO.setMarkupPercent(stockProduct.getMarkupPercent());
                        stockInProductDetailDTO.setFacturaProfitRate(stockProduct.getFacturaProfitRate());

                        stockInProductDetailDTO.setVatPrice(stockProduct.getVatPrice());
                        stockInProductDetailDTO.setVatRate(stockProduct.getVatRate());
                        stockInProductDetailDTO.setTotalVatPrice(stockProduct.getTotalVatPrice());
                        stockInProductDetailDTO.setHasMark(stockProduct.getProduct().isHasMark());
                        stockInProductDetailDTO.setHasMarking(stockProduct.isHasMarking());

                        stockInProductDetailDTO.setExciseRate(stockProduct.getExciseRate());
                        stockInProductDetailDTO.setExciseSum(stockProduct.getExciseSum());

                        stockInProductDetailDTO.setPackageQuantities(packageQuantityService.getByCompositeId(stockProduct));

                        if (stockProduct.getProductSerialId() != null) {
                            stockInProductDetailDTO.setProductSerial(stockProduct.getProductSerial().toCommonDTO());
                        }
                        if (stockProduct.getCompanyCommittentId() != null) {
                            stockInProductDetailDTO.setCompanyCommittent(stockProduct.getCompanyCommittent().toLookUpDTO());
                        }
                        if (stockProduct.getLgota() != null) {
                            stockInProductDetailDTO.setLgota(stockProduct.getLgota().toLookUpDTO());
                        }
                        stockInProductDetailDTO.setLgotaVatSum(stockProduct.getLgotaVatSum());

                        stockInProductDetailDTO.setUnit(stockProduct.getUnit().toDTO());
                        stockInProductDetailDTO.setQtyInPackage(stockProduct.getQtyInPackage());
                        stockInProductDetailDTO.setAggregationQty(stockProduct.getAggregationQty());
                        stockInProductDetailDTO.setMarkQty(stockProduct.getMarkQty());
                        stockInProductDetailDTO.setAggregationBlockQty(stockProduct.getAggregationBlockQty());
                        if (StockStatus.CHECKING_ERROR.equals(stock.getStatus())) {
                            stockInProductDetailDTO.setHasError(invalidAggregationRepository.existsByStockProductId(stockProduct.getId()));
                        }
                        stockInProductDetailDTO.setHasMarking(stockProduct.isHasMarking());
                        if (customerId != null && stockProduct.getProductContractId() == null) {
                            stockInProductDetailDTO.setQtyInWarehouse(warehouseProductRepository.getQty(stock.getWarehouseId(), stockProduct.getProductId(), stockProduct.getUnitId()));
                        }
                        if (stockProduct.getProductContractId() != null) {
                            stockInDetailDTO.setHasContract(true);
                            stockInProductDetailDTO.setProductContract(stockProduct.getProductContract().toCommonDTO());
                        }
                        stockInProductDetailDTO.setHasWarning(stockProduct.isHasWarning());
                        stockInProductDetailDTO.setBlockProducts(stockProduct.getProduct().getBlockProducts());
                        stockInDetailDTO.addProduct(stockInProductDetailDTO);
                    }
                    return stockInDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
    }

    public Page<StockBalanceReport> getBalanceReport(ProductFilter filter) {
        ResultList<WarehouseProduct> resultList = stockInRepository.getBalanceReport(filter);
        List<StockBalanceReport> result = resultList
                .getList()
                .stream()
                .map(warehouseProduct -> {
                    StockBalanceReport stockBalanceReport = new StockBalanceReport();
                    stockBalanceReport.setId(warehouseProduct.getId());
                    stockBalanceReport.setProductId(warehouseProduct.getProductId());
                    stockBalanceReport.setName(warehouseProduct.getProduct().getName());
                    stockBalanceReport.setBarcode(warehouseProduct.getProduct().getBarcode());
                    stockBalanceReport.setVatBarcode(warehouseProduct.getProduct().getVatBarcode());
                    stockBalanceReport.setQty(warehouseProduct.getQty());
                    stockBalanceReport.setBranch(warehouseProduct.getProduct().getBranch().toCommonDTO());
                    stockBalanceReport.setWarehouse(warehouseProduct.getWarehouse().toCommonDTO());
                    return stockBalanceReport;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<StockReportDTO> getReport(ProductFilter filter) {
        return stockInRepository.getReport(filter);
    }

    public List<StockProductLookUpDTO> lookUpProduct(StockInProductFilter filter) {
        filter.setEnabledSorting(companySettingsRepository.existsByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.SORTING_PRODUCTS));
        filter.setProductTypes(ProductType.getBlockProductTypes());
        filter.setSortOrderType(SortOrderType.LIST.getCode());
        if (StringUtils.isNotEmpty(filter.getAggregationCode())) {
            return stockProductAggregationRepository.getLookUp(filter);
        } else if (StringUtils.isNotEmpty(filter.getMarkCode())) {
            return stockProductMarkRepository.getLookUp(filter);
        } else if (StringUtils.isNotEmpty(filter.getAggregationBlockCode())) {
            return stockProductAggregationBlockRepository.getLookUp(filter);
        } else {
            return stockProductRepository.getLookUp(filter);
        }
    }

    public List<ProductCapacityDTO> lookUpProducts(StockInProductFilter filter) {
        filter.setEnabledSorting(companySettingsRepository.existsByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.SORTING_PRODUCTS));
        filter.setProductTypes(ProductType.getBlockProductTypes());
        filter.setSortOrderType(SortOrderType.LIST.getCode());
        if (StringUtils.isNotEmpty(filter.getAggregationCode())) {
            return stockProductAggregationRepository.lookUpProducts(filter);
        } else if (StringUtils.isNotEmpty(filter.getMarkCode())) {
            return stockProductMarkRepository.lookUpProducts(filter);
        } else if (StringUtils.isNotEmpty(filter.getAggregationBlockCode())) {
            return stockProductAggregationBlockRepository.lookUpProducts(filter);
        }
        if (filter.isEnabledSorting()) {
            return stockProductRepository.lookUpSortedProducts(filter);
        }
        return stockProductRepository.lookUpProducts(filter);
    }

    public StockProductLookUpDTO getProduct(Long productId, BaseFilter filter) {
        return stockProductRepository.getProduct(productId, filter);
    }

    public List<CommonNameDTO> lookUpStockProduct(StockInProductFilter filter) {
        if (filter.getProductId() == null) {
            throw validationService.badRequestExceptionThrow("Выберите продукт").get();
        }
        if (StringUtils.isNotEmpty(filter.getAggregationCode())) {
            return stockProductAggregationRepository.lookUpStockProduct(filter);
        } else if (StringUtils.isNotEmpty(filter.getMarkCode())) {
            return stockProductMarkRepository.lookUpStockProduct(filter);
        } else if (StringUtils.isNotEmpty(filter.getAggregationBlockCode())) {
            return stockProductAggregationBlockRepository.lookUpStockProduct(filter);
        } else {
            return stockProductRepository.lookUpStockProduct(filter);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Stock stock = stockInRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        if (!(StockStatus.DRAFT.equals(stock.getStatus()) || StockStatus.CHECKING_ERROR.equals(stock.getStatus()))) {
            throw new BadRequestException("Вы можете удалить только черновик и на проверке статусы");
        }
        this.validateCompany(stock.getCompanyId());

        List<Long> stockProductIds = stockProductRepository.getIdsByStockId(stock.getId());
        stockProductRepository.deleteByStockId(id);
        stockProductAggregationRepository.deleteAllByStockProductIds(stockProductIds);
        stockProductAggregationBlockRepository.deleteAllByStockProductIds(stockProductIds);
        stockProductMarkRepository.deleteAllByStockProductIds(stockProductIds);
        invalidAggregationRepository.deleteAllByStockId(stock.getId());
        stock.setDeleted(true);
        stock.setStatus(StockStatus.DELETED);
        if (stock.getFacturaId() != null) {
            Factura factura = stock.getFactura();
            factura.setStockInId(null);
            factura.setStockIn(null);
            facturaRepository.save(factura);
        }
        stockInRepository.save(stock);
    }

    @Transactional
    public CheckStatusResponseDTO updateStatus(Long id, StockStatus status, LocalDateTime lastModifiedDate) {
        Stock stock = stockInRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        if (!stock.getUpdatedDate().equals(lastModifiedDate)) {
            return new CheckStatusResponseDTO(false, 1);
        }
        this.validateCompany(stock.getCompanyId());
        if (StockStatus.COMPLETED.equals(status)) {
            if (!StockStatus.DRAFT.equals(stock.getStatus())) {
                throw validationService.badRequestExceptionThrow("Вы можете заполнить только черновик").get();
            }
            if (stock.getFactura() != null) {
                if (!Status.ACCEPTED.equals(stock.getFactura().getStatus())) {
                    throw validationService.badRequestExceptionThrow("Статус Счет-фактуры не принят", "Статус Счет-фактуры не принят").get();

                }
            }
            warehouseProductService.in(stock, false);
            stock.setStatus(StockStatus.COMPLETED);
            stock.setCompletedDate(LocalDateTime.now());
            stockInRepository.save(stock);
            CompanySettings settings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(stock.getCompanyId(), GenericSettingsType.ONE_C_SYNC_ENABLED).orElse(null);
            if (settings != null) {
                if (settings.isEnabled()) {
                    this.sendToQueue(new StockIn1CSyncDTO(List.of(stock.getId())));
                }
            } else if (stock.getCompany().isOneCEnabled()) {
                this.sendToQueue(new StockIn1CSyncDTO(List.of(stock.getId())));
            }
            List<Product> products = new ArrayList<>();
            for (StockProduct stockProduct : stock.getProducts()) {
                BigDecimal profitRate = Optional.ofNullable(stockProduct.getFacturaProfitRate()).orElse(stockProduct.getMarkupPercent());
                if (profitRate != null && BigDecimal.ZERO.compareTo(profitRate) != 0) {
                    Product product = stockProduct.getProduct();
                    product.setPrice(stockProduct.getSellingPrice());
                    products.add(product);
                }
            }
            productRepository.saveAll(products);
            notificationProducer.send(TemplateType.COMPLETE_STOCK_IN, SecurityUtils.getCurrentCompanyId(),
                    Map.of(TemplateTypeAlias.TRANSACTION_NUMBER, stock.getStockNumber(),
                            TemplateTypeAlias.CREATOR, getCurrentUserId()));
        } else if (StockStatus.CANCELLED.equals(status)) {
            if (!StockStatus.COMPLETED.equals(stock.getStatus())) {
                throw validationService.badRequestExceptionThrow("Вы можете отклонять только Завершен").get();
            }
            if (stock.getStockTransfer() != null && StockTransferStatus.APPROVED == stock.getStockTransfer().getStatus()) {
                    throw validationService.badRequestExceptionThrow("Вы не можете отменить приход, так как у него есть передача со статусом 'одобрено'").get();
                }

            warehouseProductService.out(stock);
            stock.getProducts().forEach(stockProduct -> warehouseProductService.updateCodes(stockProduct, true));
            stock.setStatus(status);
            stockInRepository.save(stock);
            CompanySettings settings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(stock.getCompanyId(), GenericSettingsType.ONE_C_SYNC_ENABLED).orElse(null);
            if (settings != null) {
                if (settings.isEnabled()) {
                    this.sendToQueue(new StockIn1CSyncDTO(List.of(stock.getId())));
                }
            } else if (stock.getCompany().isOneCEnabled()) {
                this.sendToQueue(new StockIn1CSyncDTO(List.of(stock.getId())));
            }

            invalidAggregationRepository.deleteAllByStockId(id);
            List<Product> products = new ArrayList<>();
            for (StockProduct stockProduct : stock.getProducts()) {
                BigDecimal profitRate = Optional.ofNullable(stockProduct.getFacturaProfitRate()).orElse(stockProduct.getMarkupPercent());
                if (profitRate != null && BigDecimal.ZERO.compareTo(profitRate) != 0) {
                    Product product = stockProduct.getProduct();
                    BigDecimal price = product.getPrice();
                    price = price.multiply(profitRate).divide(BigDecimal.valueOf(100).add(profitRate), RoundingMode.HALF_EVEN);
                    product.setPrice(price);
                    products.add(product);
                }
            }
            productRepository.saveAll(products);
        } else if (StockStatus.DRAFT.equals(status) && StockStatus.CHECKING.equals(stock.getStatus()) && isFromAdmin()) {
            if (!warehouseProductService.checkStockStatus(stock)) {
                stock.setStatus(StockStatus.DRAFT);
                stockInRepository.save(stock);
            }
        }
        return new CheckStatusResponseDTO(true, 0);
    }

    @Transactional
    public Long copy(Long id) {
        Stock oldStock = stockInRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        if (!StockStatus.CANCELLED.equals(oldStock.getStatus())) {
            throw validationService.badRequestExceptionThrow("Чтобы скопировать приход, необходимо ee отменить").get();
        }
        this.validateCompany(oldStock.getCompanyId());
        invalidAggregationRepository.deleteAllByStockId(id);

        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_IN, oldStock.getCompanyId());

        boolean hasMarking = false;
        Stock stock = new Stock();
        stock.setOldStockId(oldStock.getId());
        stock.setTransferType(TransferType.IN);
        stock.setTransferDate(Optional.ofNullable(oldStock.getTransferDate()).orElse(LocalDateTime.now()));
        stock.setFacturaId(oldStock.getFacturaId());
        stock.setPartyId(oldStock.getPartyId());
        stock.setAicId(oldStock.getAicId());
        stock.setAcceptanceTransferActId(oldStock.getAcceptanceTransferActId());
        stock.setWarehouseId(oldStock.getWarehouseId());
        stock.setBranchId(oldStock.getBranchId());
        stock.setDescription(oldStock.getDescription());
        stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stock.setStockNumber(numberingMap.get(NUMBER));
        stock.setCustomerId(oldStock.getCustomerId());
        stock.setSupplierId(oldStock.getSupplierId());
        stock.setCompanyCommittentId(oldStock.getCompanyCommittentId());
        stock.setCompanyId(oldStock.getCompanyId());
        stock.setContractId(oldStock.getContractId());
        if (oldStock.getOrder() != null) {
            stock.setOrderId(oldStock.getOrder().getId());
        }

        stock.setTotalQty(oldStock.getTotalQty());
        stock.setTotalPrice(oldStock.getTotalPrice());
        stock.setTotalForeignPrice(oldStock.getTotalForeignPrice());
        stock.setExchangeRate(oldStock.getExchangeRate());
        stock.setCurrencyType(oldStock.getCurrencyType());
        stock.setTotalPriceWithoutVat(oldStock.getTotalPriceWithoutVat());
        stock.setTotalVatPrice(oldStock.getTotalVatPrice());
        stock.setTotalBrutto(oldStock.getTotalBrutto());
        stock.setTotalNetto(oldStock.getTotalNetto());

        stock = stockInRepository.save(stock);

        for (StockProduct oldStockProduct : oldStock.getProducts()) {
            StockProduct stockProduct = new StockProduct();
            stockProduct.setProductId(oldStockProduct.getProductId());
            stockProduct.setName(oldStockProduct.getName());
            stockProduct.setUnitId(oldStockProduct.getUnitId());
            stockProduct.setQty(oldStockProduct.getQty());
            stockProduct.setBasePrice(oldStockProduct.getBasePrice());

            stockProduct.setPurchasePrice(oldStockProduct.getPurchasePrice());
            stockProduct.setPurchaseForeignPrice(oldStockProduct.getPurchaseForeignPrice());
            stockProduct.setPurchasePriceWithoutVat(oldStockProduct.getPurchasePriceWithoutVat());
            stockProduct.setTotalPurchasePrice(oldStockProduct.getTotalPurchasePrice());
            stockProduct.setTotalPurchaseForeignPrice(oldStockProduct.getTotalPurchaseForeignPrice());
            stockProduct.setTotalPurchasePriceWithoutVat(oldStockProduct.getTotalPurchasePriceWithoutVat());

            stockProduct.setSellingPrice(oldStockProduct.getSellingPrice());
            stockProduct.setSellingForeignPrice(oldStockProduct.getSellingForeignPrice());
            stockProduct.setSellingPriceWithoutVat(oldStockProduct.getSellingPriceWithoutVat());
            stockProduct.setTotalSellingPrice(oldStockProduct.getTotalSellingPrice());
            stockProduct.setTotalSellingForeignPrice(oldStockProduct.getTotalSellingForeignPrice());
            stockProduct.setTotalSellingPriceWithoutVat(oldStockProduct.getTotalSellingPriceWithoutVat());

            stockProduct.setMarkupPercent(oldStockProduct.getMarkupPercent());
            stockProduct.setMarkupPrice(oldStockProduct.getMarkupPrice());

            stockProduct.setVatRate(oldStockProduct.getVatRate());
            stockProduct.setVatPrice(oldStockProduct.getVatPrice());
            stockProduct.setTotalVatPrice(oldStockProduct.getTotalVatPrice());

            stockProduct.setExciseRate(oldStockProduct.getExciseRate());
            stockProduct.setExciseSum(oldStockProduct.getExciseSum());

            stockProduct.setProductSerialId(oldStockProduct.getProductSerialId());
            stockProduct.setCompanyCommittentId(oldStockProduct.getCompanyCommittentId());
            stockProduct.setProductContractId(oldStockProduct.getProductContractId());

            stockProduct.setHasMarking(oldStockProduct.isHasMarking());
            stockProduct.setAggregationQty(oldStockProduct.getAggregationQty());
            stockProduct.setMarkQty(oldStockProduct.getMarkQty());
            stockProduct.setAggregationBlockQty(oldStockProduct.getAggregationBlockQty());
            stockProduct.setTotalBrutto(oldStockProduct.getTotalBrutto());
            stockProduct.setTotalNetto(oldStockProduct.getTotalNetto());

            stockProduct.setQtyInPackage(oldStockProduct.getQtyInPackage());

            stockProduct.setLgotaVatSum(oldStockProduct.getLgotaVatSum());

            stockProduct.setStockId(stock.getId());
            stockProduct.setStock(stock);
            stockProduct = stockProductRepository.save(stockProduct);

            if (!CollectionUtils.isEmpty(packageQuantityService.getByCompositeId(oldStockProduct))) {
                packageQuantityService.create(stockProduct, packageQuantityService.getByCompositeId(oldStockProduct));
            }

            if (stockProductAggregationRepository.existsByStockProductIdAndDeletedIsFalseAndParentIsTrue(oldStockProduct.getId())) {
                hasMarking = true;
                List<StockProductAggregation> stockProductAggregations = new ArrayList<>();
                for (StockProductAggregation oldStockProductAggregation : stockProductAggregationRepository.findAllByStockProductIdAndDeletedIsFalseAndParentIsTrue(oldStockProduct.getId())) {
                    StockProductAggregation stockProductAggregation = new StockProductAggregation();
                    stockProductAggregation.setStockProductId(stockProduct.getId());
                    stockProductAggregation.setCode(oldStockProductAggregation.getCode());
                    stockProductAggregation.setOriginalCode(oldStockProductAggregation.getOriginalCode());
                    stockProductAggregation.setProductId(oldStockProduct.getProductId());
                    stockProductAggregation.setTransferType(TransferType.IN);
                    stockProductAggregation.setCompanyCommittentId(oldStockProductAggregation.getCompanyCommittentId());
                    stockProductAggregation.setUsed(false);
                    stockProductAggregations.add(stockProductAggregation);
                }
                stockProductAggregationRepository.saveAll(stockProductAggregations);
                stockProductAggregationRepository.deleteAll(oldStockProduct.getStockProductAggregations());
            }
            if (stockProductMarkRepository.existsByStockProductIdAndDeletedIsFalseAndParentIsTrue(oldStockProduct.getId())) {
                hasMarking = true;
                List<StockProductMark> stockProductMarks = new ArrayList<>();
                for (StockProductMark oldStockProductMark : stockProductMarkRepository.findAllByStockProductIdAndDeletedIsFalseAndParentIsTrue(oldStockProduct.getId())) {
                    StockProductMark stockProductMark = new StockProductMark();
                    stockProductMark.setStockProductId(stockProduct.getId());
                    stockProductMark.setCode(oldStockProductMark.getCode());
                    stockProductMark.setOriginalCode(oldStockProductMark.getOriginalCode());
                    stockProductMark.setQty(oldStockProductMark.getQty());
                    stockProductMark.setProductId(oldStockProduct.getProductId());
                    stockProductMark.setTransferType(TransferType.IN);
                    stockProductMark.setUsed(false);
                    stockProductMark.setCompanyCommittentId(oldStockProduct.getCompanyCommittentId());
                    stockProductMarks.add(stockProductMark);
                }
                stockProductMarkRepository.saveAll(stockProductMarks);
                stockProductMarkRepository.deleteAll(oldStockProduct.getStockProductMarks());
            }
            if (stockProductAggregationBlockRepository.existsByStockProductIdAndDeletedIsFalseAndParentIsTrue(oldStockProduct.getId())) {
                hasMarking = true;
                List<StockProductAggregationBlock> stockProductAggregationBlocks = new ArrayList<>();
                for (StockProductAggregationBlock oldStockProductAggregationBlock : stockProductAggregationBlockRepository.findAllByStockProductIdAndDeletedIsFalseAndParentIsTrue(oldStockProduct.getId())) {
                    StockProductAggregationBlock stockProductAggregationBlock = new StockProductAggregationBlock();
                    stockProductAggregationBlock.setStockProductId(stockProduct.getId());
                    stockProductAggregationBlock.setCode(oldStockProductAggregationBlock.getCode());
                    stockProductAggregationBlock.setOriginalCode(oldStockProductAggregationBlock.getOriginalCode());
                    stockProductAggregationBlock.setQty(oldStockProductAggregationBlock.getQty());
                    stockProductAggregationBlock.setProductId(oldStockProduct.getProductId());
                    stockProductAggregationBlock.setTransferType(TransferType.IN);
                    stockProductAggregationBlock.setUsed(false);
                    stockProductAggregationBlock.setCompanyCommittentId(oldStockProduct.getCompanyCommittentId());
                    stockProductAggregationBlocks.add(stockProductAggregationBlock);
                }
                stockProductAggregationBlockRepository.saveAll(stockProductAggregationBlocks);
                stockProductAggregationBlockRepository.deleteAll(oldStockProduct.getStockProductAggregationBlocks());
            }
        }

        stock.setTotalQty(oldStock.getTotalQty());
        stock.setTotalPrice(oldStock.getTotalPrice());
        stock.setTotalPriceWithoutVat(oldStock.getTotalPriceWithoutVat());
        stock.setTotalVatPrice(oldStock.getTotalVatPrice());
        if (hasMarking) {
            stock.setStatus(StockStatus.CHECKING);
        }
        stockInRepository.save(stock);
        if (hasMarking) {
            stock.setStatus(StockStatus.CHECKING);
            stockInRepository.save(stock);
            CompanySettings xtraceSettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.XTRACE_ENABLED).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanySettings.class.getSimpleName(), "companyId", getCurrentCompanyId()));
            CheckAslBelgiDTO checkAslBelgiDTO = new CheckAslBelgiDTO();
            checkAslBelgiDTO.setStockId(stock.getId());
            checkAslBelgiDTO.setToken(xtraceSettings.isEnabled() ? this.getXTraceInfo().getApiKey() : null);
            checkAslBelgiDTO.setXtraceEnabled(xtraceSettings.isEnabled());
            checkAslBelgiDTO.setFromFactura(stock.getFacturaId() != null);
            invalidAggregationService.check(checkAslBelgiDTO);
        }
        return stock.getId();
    }

    @Transactional
    public Long createByStockTransfer(StockTransfer stockTransfer) {
        Company company = getCurrentCompany();
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_IN, company.getId());

        Stock stock = new Stock();
        stock.setTransferType(TransferType.IN);
        stock.setTransferDate(stockTransfer.getTransferDate());
        stock.setWarehouseId(stockTransfer.getToWarehouseId());
        stock.setBranchId(stockTransfer.getToBranchId());
        stock.setDescription(stockTransfer.getDescription());
        stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stock.setStockNumber(numberingMap.get(NUMBER));
        stock.setCustomerId(company.getId());
        stock.setSupplierId(company.getId());
        stock.setCompanyId(company.getId());
        stock.setStockTransferId(stockTransfer.getId());
        stock.setTotalBrutto(stockTransfer.getTotalBrutto());
        stock.setTotalNetto(stockTransfer.getTotalNetto());
        stock = stockInRepository.save(stock);

        BigDecimal grandTotalQty = BigDecimal.ZERO;
        BigDecimal grandTotalPrice = BigDecimal.ZERO;
        BigDecimal grandTotalPriceWithoutVat = BigDecimal.ZERO;
        BigDecimal grandTotalVatPrice = BigDecimal.ZERO;

        for (StockTransferProduct stockTransferProduct : stockTransfer.getProducts()) {
            Product product = stockTransferProduct.getToProduct();

            StockProduct stockProduct = new StockProduct();
            stockProduct.setProductId(product.getId());
            stockProduct.setUnitId(stockTransferProduct.getUnitId());
            stockProduct.setQty(stockTransferProduct.getQty());

            stockProduct.setPurchasePrice(product.getPrice());
            stockProduct.setTotalPurchasePrice(product.getPrice().multiply(stockTransferProduct.getQty()));

            if (Vat.WITHOUT_VAT.equals(product.getVat())) {
                stockProduct.setPurchasePriceWithoutVat(product.getPrice());
                stockProduct.setTotalPurchasePriceWithoutVat(product.getPrice().multiply(stockTransferProduct.getQty()));
            } else {
                stockProduct.setPurchasePriceWithoutVat(CalcUtils.getPriceWithoutVat(product.getPrice(), product.getVat()));
                stockProduct.setTotalPurchasePriceWithoutVat(CalcUtils.getPriceWithoutVat(product.getPrice(), product.getVat()));
            }

            stockProduct.setSellingPrice(stockProduct.getPurchasePrice());
            stockProduct.setTotalSellingPrice(stockProduct.getTotalPurchasePrice());

            stockProduct.setSellingPriceWithoutVat(stockProduct.getPurchasePriceWithoutVat());
            stockProduct.setTotalSellingPriceWithoutVat(stockProduct.getTotalPurchasePriceWithoutVat());

            stockProduct.setMarkupPercent(BigDecimal.ZERO);
            stockProduct.setMarkupPrice(BigDecimal.ZERO);

            stockProduct.setVatRate(product.getVat().getAmount());
            stockProduct.setTotalBrutto(stockTransferProduct.getTotalBrutto());
            stockProduct.setTotalNetto(stockTransferProduct.getTotalNetto());

            stockProduct.setExciseRate(product.getExciseRate());
            stockProduct.setExciseSum(product.getExciseSum());

            if (Vat.WITHOUT_VAT.equals(product.getVat())) {
                stockProduct.setVatPrice(BigDecimal.ZERO);
                stockProduct.setTotalVatPrice(BigDecimal.ZERO);
            } else {
                BigDecimal priceWithoutVat = CalcUtils.getPriceWithoutVat(product.getPrice(), product.getVat());
                BigDecimal vatPrice = product.getPrice().subtract(priceWithoutVat);
                stockProduct.setVatPrice(vatPrice);
                stockProduct.setTotalVatPrice(vatPrice.multiply(stockTransferProduct.getQty()));
            }

            stockProduct.setStockId(stock.getId());
            if (!CollectionUtils.isEmpty(stockTransferProduct.getStockTransferProductAggregations())) {
                stockProduct.setAggregationQty(stockTransferProduct.getStockTransferProductAggregations().size());
                stockProduct.setHasMarking(true);
            }
            if (!CollectionUtils.isEmpty(stockTransferProduct.getStockTransferProductMarks())) {
                stockProduct.setMarkQty(stockTransferProduct.getStockTransferProductMarks().size());
                stockProduct.setHasMarking(true);
            }
            if (!CollectionUtils.isEmpty(stockTransferProduct.getStockTransferProductAggregationBlocks())) {
                stockProduct.setAggregationBlockQty(stockTransferProduct.getStockTransferProductAggregationBlocks().size());
                stockProduct.setHasMarking(true);
            }
            stockProduct = stockProductRepository.save(stockProduct);

            if (!CollectionUtils.isEmpty(stockTransferProduct.getStockTransferProductAggregations())) {
                List<StockProductAggregation> stockProductAggregations = new ArrayList<>();
                for (StockTransferProductAggregation transferProductAggregation : stockTransferProduct.getStockTransferProductAggregations()) {
                    StockProductAggregation stockProductAggregation = new StockProductAggregation();
                    stockProductAggregation.setStockProductId(stockProduct.getId());
                    stockProductAggregation.setCode(transferProductAggregation.getCode());
                    stockProductAggregation.setOriginalCode(transferProductAggregation.getOriginalCode());
                    stockProductAggregation.setQty(transferProductAggregation.getQty());
                    stockProductAggregation.setProductId(transferProductAggregation.getProductId());
                    stockProductAggregation.setTransferType(TransferType.IN);
                    stockProductAggregation.setUsed(false);
                    stockProductAggregation.setPackageType(transferProductAggregation.getPackageType());
                    stockProductAggregation.setStatus(transferProductAggregation.getStatus());
                    if (StringUtils.isNotEmpty(transferProductAggregation.getOwnerTin())) {
                        stockProductAggregation.setOwnerName(transferProductAggregation.getOwnerName());
                        stockProductAggregation.setOwnerTin(transferProductAggregation.getOwnerTin());
                    }
                    stockProductAggregations.add(stockProductAggregation);
                }
                stockProductAggregationRepository.saveAll(stockProductAggregations);
            }
            if (!CollectionUtils.isEmpty(stockTransferProduct.getStockTransferProductMarks())) {
                List<StockProductMark> stockProductMarks = new ArrayList<>();
                for (StockTransferProductMark stockTransferProductMark : stockTransferProduct.getStockTransferProductMarks()) {
                    StockProductMark stockProductMark = new StockProductMark();
                    stockProductMark.setStockProductId(stockProduct.getId());
                    stockProductMark.setCode(stockTransferProductMark.getCode());
                    stockProductMark.setOriginalCode(stockTransferProductMark.getOriginalCode());
                    stockProductMark.setQty(stockTransferProductMark.getQty());
                    stockProductMark.setProductId(stockTransferProductMark.getProductId());
                    stockProductMark.setUsed(false);
                    stockProductMark.setTransferType(TransferType.IN);
                    stockProductMark.setPackageType(stockTransferProductMark.getPackageType());
                    stockProductMark.setStatus(stockTransferProductMark.getStatus());
                    if (StringUtils.isNotEmpty(stockTransferProductMark.getOwnerTin())) {
                        stockProductMark.setOwnerName(stockTransferProductMark.getOwnerName());
                        stockProductMark.setOwnerTin(stockTransferProductMark.getOwnerTin());
                    }
                    stockProductMarks.add(stockProductMark);
                }
                stockProductMarkRepository.saveAll(stockProductMarks);
            }

            if (!CollectionUtils.isEmpty(stockTransferProduct.getStockTransferProductAggregationBlocks())) {
                List<StockProductAggregationBlock> stockAggregationBlocks = new ArrayList<>();
                for (StockTransferProductAggregationBlock stockTransferProductAggregationBlock : stockTransferProduct.getStockTransferProductAggregationBlocks()) {
                    StockProductAggregationBlock stockProductAggregationBlock = new StockProductAggregationBlock();
                    stockProductAggregationBlock.setStockProductId(stockProduct.getId());
                    stockProductAggregationBlock.setCode(stockTransferProductAggregationBlock.getCode());
                    stockProductAggregationBlock.setOriginalCode(stockTransferProductAggregationBlock.getOriginalCode());
                    stockProductAggregationBlock.setQty(stockTransferProductAggregationBlock.getQty());
                    stockProductAggregationBlock.setProductId(stockTransferProductAggregationBlock.getProductId());
                    stockProductAggregationBlock.setTransferType(TransferType.IN);
                    stockProductAggregationBlock.setUsed(false);
                    stockProductAggregationBlock.setPackageType(stockTransferProductAggregationBlock.getPackageType());
                    stockProductAggregationBlock.setStatus(stockTransferProductAggregationBlock.getStatus());
                    if (StringUtils.isNotEmpty(stockTransferProductAggregationBlock.getOwnerTin())) {
                        stockProductAggregationBlock.setOwnerName(stockTransferProductAggregationBlock.getOwnerName());
                        stockProductAggregationBlock.setOwnerTin(stockTransferProductAggregationBlock.getOwnerTin());
                    }
                    stockAggregationBlocks.add(stockProductAggregationBlock);
                }
                stockProductAggregationBlockRepository.saveAll(stockAggregationBlocks);
            }
            grandTotalQty = grandTotalQty.add(stockTransferProduct.getQty());
            grandTotalPrice = grandTotalPrice.add(stockProduct.getTotalPurchasePrice());
            grandTotalPriceWithoutVat = grandTotalPriceWithoutVat.add(stockProduct.getTotalPurchasePriceWithoutVat());
            grandTotalVatPrice = grandTotalVatPrice.add(stockProduct.getTotalVatPrice());
        }

        stock.setTotalQty(grandTotalQty);
        stock.setTotalPrice(grandTotalPrice);
        stock.setTotalPriceWithoutVat(grandTotalPriceWithoutVat);
        stock.setTotalVatPrice(grandTotalVatPrice);
        stock.setStatus(StockStatus.COMPLETED);
        stockInRepository.save(stock);

        return stock.getId();
    }


    public Map<CodeType, List<CheckedCodeDTO>> getAggregationCodes(Long stockProductId) {
        Map<String, String> invalidCodesMap = new HashMap<>();
        List<InvalidAggregation> invalidAggregations = invalidAggregationRepository.findAllByStockProductId(stockProductId);
        invalidAggregations.forEach(invalidAggregation -> {
            if (!invalidCodesMap.containsKey(invalidAggregation.getCode())) {
                invalidCodesMap.put(invalidAggregation.getCode(), invalidAggregation.getErrorMessage());
            }
        });

        Map<CodeType, List<CheckedCodeDTO>> result = new HashMap<>();
        List<CheckedCodeDTO> aggregationCodes = stockProductAggregationRepository
                .findAllByStockProductIdAndDeletedIsFalseAndParentIsTrue(stockProductId)
                .stream()
                .map(stockProductAggregation ->
                        new CheckedCodeDTO(stockProductAggregation.getCode(), stockProductAggregation.getOriginalCode(), invalidCodesMap.get(stockProductAggregation.getCode()), stockProductAggregation.getQty(), stockProductAggregation.getOwner())
                )
                .collect(Collectors.toList());
        result.put(CodeType.AGGREGATION, aggregationCodes);

        List<CheckedCodeDTO> markCodes = stockProductMarkRepository
                .findAllByStockProductIdAndDeletedIsFalseAndParentIsTrue(stockProductId)
                .stream()
                .map(stockProductMark -> new CheckedCodeDTO(stockProductMark.getCode(), stockProductMark.getOriginalCode(), invalidCodesMap.get(stockProductMark.getCode()), stockProductMark.getQty(), stockProductMark.getOwner()))
                .collect(Collectors.toList());
        result.put(CodeType.MARK, markCodes);

        List<CheckedCodeDTO> stockProductAggregationBlockCodes = stockProductAggregationBlockRepository
                .findAllByStockProductIdAndDeletedIsFalseAndParentIsTrue(stockProductId)
                .stream()
                .map(stockProductAggregationBlock ->
                        new CheckedCodeDTO(stockProductAggregationBlock.getCode(), stockProductAggregationBlock.getOriginalCode(), invalidCodesMap.get(stockProductAggregationBlock.getCode()), stockProductAggregationBlock.getQty(), stockProductAggregationBlock.getOwner())
                )
                .collect(Collectors.toList());
        result.put(CodeType.BLOCK, stockProductAggregationBlockCodes);

        return result;
    }

    public CommonStockStatisticsDTO getStatistics(StockFilter filter) {
        return stockInRepository.getStatistic(filter);
    }

    @Transactional
    public StockInDetailDTO convertFromAcceptanceTransferAct(Long acceptanceTransferActId, Long branchId) {
        AcceptanceTransferAct acceptanceTransferAct = acceptanceTransferActRepository.findById(acceptanceTransferActId).orElseThrow(validationService.entityNotFoundExceptionThrow(AcceptanceTransferAct.class.getSimpleName(), "id", acceptanceTransferActId));
        StockInDetailDTO stockInDetailDTO = new StockInDetailDTO();
        stockInDetailDTO.setAcceptanceTransferAct(acceptanceTransferAct.toLinkDTO());

        if (!CollectionUtils.isEmpty(acceptanceTransferAct.getAcceptanceTransferActDetail().getProducts())) {
            acceptanceTransferAct.getAcceptanceTransferActDetail().getProducts().forEach(acceptanceTransferActProduct -> {
                StockInProductDetailDTO stockProductDTO = new StockInProductDetailDTO();
                stockProductDTO.setName(acceptanceTransferActProduct.getName());
                stockProductDTO.setBarcode(acceptanceTransferActProduct.getBarcode());
                stockProductDTO.setVatBarCode(acceptanceTransferActProduct.getCatalogCode());
                stockProductDTO.setHasMarking(!(CollectionUtils.isEmpty(acceptanceTransferActProduct.getMarks()) || CollectionUtils.isEmpty(acceptanceTransferActProduct.getAggregations()) || CollectionUtils.isEmpty(acceptanceTransferActProduct.getBlocks())));
                stockProductDTO.setQty(acceptanceTransferActProduct.getQty());
                stockProductDTO.setPurchasePrice(acceptanceTransferActProduct.getPrice());
                stockProductDTO.setSerialNumber(acceptanceTransferActProduct.getSerialNumber());
                stockProductDTO.setUnit(getUnit().toDTO());

                Hibernate.initialize(acceptanceTransferActProduct.getMarks());
                Hibernate.initialize(acceptanceTransferActProduct.getAggregations());
                Hibernate.initialize(acceptanceTransferActProduct.getBlocks());

                stockProductDTO.setMarkCodes(acceptanceTransferActProduct.getMarks());
                stockProductDTO.setAggregationCodes(acceptanceTransferActProduct.getAggregations());
                stockProductDTO.setAggregationBlocks(acceptanceTransferActProduct.getBlocks());


                Product product = acceptanceTransferActProduct.getProduct();
                if (product == null) {
                    product = productService.getProductByBarcodeAndVatBarcode(acceptanceTransferActProduct.getBarcode(), acceptanceTransferActProduct.getCatalogCode(), branchId);
                }
                if (product == null) {
                    Category category = null;
                    if (StringUtils.isNotEmpty(acceptanceTransferActProduct.getCatalogCode()) && StringUtils.isNotEmpty(acceptanceTransferActProduct.getCatalogName()) && StringUtils.isNotEmpty(acceptanceTransferActProduct.getCatalogName().trim())) {
                        category = categoryService.createOrGet(CategoryDTO
                                .builder()
                                .name(acceptanceTransferActProduct.getCatalogName())
                                .catalogName(acceptanceTransferActProduct.getCatalogName())
                                .catalogCode(acceptanceTransferActProduct.getCatalogCode())
                                .branchId(branchId)
                                .environmentType(EnvironmentType.WEB)
                                .build()
                        );
                    }
                    ProductDTO productDTO = ProductDTO
                            .builder()
                            .name(acceptanceTransferActProduct.getName())
                            .barcode(acceptanceTransferActProduct.getBarcode())
                            .vatBarcode(acceptanceTransferActProduct.getCatalogCode())
                            .catalogName(acceptanceTransferActProduct.getCatalogName())
                            .packageName(acceptanceTransferActProduct.getPackageName())
                            .packageCode(acceptanceTransferActProduct.getPackageCode())
                            .vat(Vat.VAT_12)
                            .unitId(getUnit().getId())
                            .categoryIds(Optional.ofNullable(category).map(productCategory -> List.of(productCategory.getId())).orElse(null))
                            .price(acceptanceTransferActProduct.getSumma())
                            .branchId(branchId)
                            .companyId(acceptanceTransferAct.getCompanyId())
                            .hasMark(!CollectionUtils.isEmpty(acceptanceTransferActProduct.getActAggregations()) || !CollectionUtils.isEmpty(acceptanceTransferActProduct.getActBlocks()) || !CollectionUtils.isEmpty(acceptanceTransferActProduct.getActMarks()))
                            .productType(MarkUtils.getProductType(acceptanceTransferAct.getCompany().getProductGroup(), acceptanceTransferActProduct.getCatalogCode()))
                            .build();
                    product = productService.createAndReturn(productDTO);
                }
                if (product != null) {
                    stockProductDTO.setId(product.getId());
                    stockProductDTO.setName(product.getName());
                    stockProductDTO.setVatBarCode(product.getVatBarcode());
                    stockProductDTO.setQtyInPackage(product.getQtyInPackage());
                    stockProductDTO.setQtyInBox(product.getQtyInBox());
                    stockProductDTO.setQtyInPallet(product.getQtyInPallet());
                    stockProductDTO.setCapacity(product.getCapacity());
                    stockProductDTO.setProductType(product.getProductType());
                    stockProductDTO.setUnit(getUnit().toDTO());
                    if (product.getVat() != null) {
                        stockProductDTO.setVatRate(product.getVat().getAmount());
                    }
                    stockProductDTO.setHasMark(product.isHasMark());
                }
                stockInDetailDTO.addProduct(stockProductDTO);
            });
        }
        return stockInDetailDTO;
    }

    @Transactional
    public void createByMarkOrderParty(Payload payload) {
        markOrderPartyRepository.findById(payload.getId()).map(markOrderParty -> {
            Product product = markOrderParty.getMarkOrderProduct().getProduct();
            Company company = product.getCompany();
            BigDecimal purchasePrice = product.getPrice();
            BigDecimal priceWithoutVat = CalcUtils.getPriceWithoutVat(purchasePrice, product.getVat());
            BigDecimal vatPrice = Vat.WITHOUT_VAT.equals(product.getVat()) ? purchasePrice.subtract(priceWithoutVat) : BigDecimal.ZERO;

            Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_IN, company.getId());

            Stock stock = new Stock();
            stock.setTransferType(TransferType.IN);
            stock.setTransferDate(markOrderParty.getCreatedDate());
            stock.setDescription(markOrderParty.getName());
            stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
            stock.setStockNumber(numberingMap.get(NUMBER));
            stock.setCustomerId(company.getId());
            stock.setCompanyId(company.getId());
            stock.setTotalQty(BigDecimal.valueOf(markOrderParty.getQty()));
            stock.setTotalPrice(purchasePrice);
            stock.setTotalPriceWithoutVat(priceWithoutVat);
            stock.setTotalVatPrice(vatPrice);
            Long branchId = branchRepository.getMainBranchId(company.getId());
            stock.setBranchId(branchId);
            stock.setWarehouseId(warehouseRepository.getMainWarehouseId(branchId));
            stockInRepository.save(stock);

            StockProduct stockProduct = new StockProduct();
            stockProduct.setProductId(product.getId());
            stockProduct.setUnitId(getUnit().getId());
            stockProduct.setQty(BigDecimal.valueOf(markOrderParty.getQty()));
            stockProduct.setPurchasePrice(product.getPrice());
            stockProduct.setTotalPurchasePrice(purchasePrice);
            stockProduct.setPurchasePriceWithoutVat(priceWithoutVat);
            stockProduct.setTotalPurchasePriceWithoutVat(priceWithoutVat);
            stockProduct.setSellingPrice(product.getPrice());
            stockProduct.setTotalSellingPrice(product.getPrice());
            stockProduct.setSellingPriceWithoutVat(stockProduct.getPurchasePriceWithoutVat());
            stockProduct.setTotalSellingPriceWithoutVat(priceWithoutVat);
            stockProduct.setMarkupPercent(BigDecimal.ZERO);
            stockProduct.setMarkupPrice(BigDecimal.ZERO);
            stockProduct.setVatRate(product.getVat().getAmount());
            stockProduct.setVatPrice(vatPrice);
            stockProduct.setTotalVatPrice(vatPrice);
            stockProduct.setExciseRate(product.getExciseRate());
            stockProduct.setExciseSum(product.getExciseSum());
            stockProduct.setHasMarking(true);
            stockProduct.setMarkQty(markOrderParty.getQty());
            stockProduct.setStockId(stock.getId());
            stockProduct.setStock(stock);
            stockProductRepository.save(stockProduct);

            List<StockProductMark> stockProductMarks = markRepository.findAllByMarkOrderPartyId(payload.getId()).stream().map(mark -> {
                StockProductMark stockProductMark = new StockProductMark();
                stockProductMark.setOriginalCode(mark.getOriginalCode());
                stockProductMark.setCode(mark.getCode());
                stockProductMark.setQty(BigDecimal.ONE);
                stockProductMark.setProductId(product.getId());
                stockProductMark.setTransferType(TransferType.IN);
                stockProductMark.setStockProductId(stockProduct.getId());
                stockProductMark.setUsed(false);
                return stockProductMark;
            }).toList();
            if (!CollectionUtils.isEmpty(stockProductMarks)) {
                stockProductMarkRepository.saveAll(stockProductMarks);
            }
            return markOrderParty;
        });
    }

    public void sendToQueue(StockIn1CSyncDTO stockIn1CSyncDTO) {
        if (!CollectionUtils.isEmpty(stockIn1CSyncDTO.getStockInIds())) {
            OneCIntegrationPayload payload = new OneCIntegrationPayload();
            payload.setStockInIds(stockIn1CSyncDTO.getStockInIds());
            payload.setCompanyId(SecurityUtils.getCurrentCompanyId());
            rabbitMqOneCIntegrationProducer.sendToQueue(payload);
        }
    }

    @Transactional
    public void sendTo1C(OneCIntegrationPayload payload) {
        String login = null;
        String password = null;
        String url = null;
        CompanySettings one_CSyncSettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(payload.getCompanyId(), GenericSettingsType.ONE_C_SYNC_ENABLED).orElse(null);
        if (one_CSyncSettings != null && one_CSyncSettings.isEnabled()) {
            login = (String) one_CSyncSettings.getValues().get(GenericSettingsFieldType.LOGIN);
            password = (String) one_CSyncSettings.getValues().get(GenericSettingsFieldType.PASSWORD);
            url = (String) one_CSyncSettings.getValues().get(GenericSettingsFieldType.URL);
        } else {
            OneCIntegration oneCIntegration = oneCIntegrationRepository.findByCompanyId(payload.getCompanyId()).orElse(null);
            if (oneCIntegration != null) {
                login = oneCIntegration.getUsername();
                password = oneCIntegration.getPassword();
                url = oneCIntegration.getUrl();
            }
        }
        if (StringUtils.isNotEmpty(login) && StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(url)) {
            for (Long stockInId : payload.getStockInIds()) {
                Stock stock = stockInRepository
                        .findById(stockInId).orElse(null);
                if (stock != null) {
                    if (!TransferType.IN.equals(stock.getTransferType())) {
                        throw validationService.badRequestExceptionThrow("Incorrect stock chosen", "Transfer type is incorrect").get();
                    }
                    if (!(StockStatus.COMPLETED.equals(stock.getStatus()) || StockStatus.CANCELLED.equals(stock.getStatus()))) {
                        throw validationService.badRequestExceptionThrow("Incorrect stock chosen", "COMPLETED and CANCELLED status is allowed").get();
                    }
                    if (stock.getFacturaId() == null && stock.getAicId() == null) {
                        throw validationService.badRequestExceptionThrow("Incorrect stock chosen", "Only stock in with factura or aic is allowed to send").get();
                    }
                    StockIn1CDTO stockIn1CDTO = new StockIn1CDTO();
                    stockIn1CDTO.setId(stock.getId());
                    stockIn1CDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
                    stockIn1CDTO.setStockNumber(stock.getStockNumber());
                    stockIn1CDTO.setBranch(stock.getBranch().toCommonDTO());
                    if (stock.getFactura() != null) {
                        stockIn1CDTO.setFactura(stock.getFactura().toLink());
                    }
                    if (stock.getParty() != null) {
                        stockIn1CDTO.setParty(stock.getParty().toCommonDTO());
                    }
                    if (stock.getAic() != null) {
                        stockIn1CDTO.setAic(stock.getAic().toCommonDTO());
                    }
                    if (stock.getAcceptanceTransferAct() != null) {
                        stockIn1CDTO.setAcceptanceTransferAct(stock.getAcceptanceTransferAct().toLinkDTO());
                    }

                    StockCustomerDTO supplier = new StockCustomerDTO();
                    supplier.setId(stock.getSupplier().getId());
                    supplier.setTin(stock.getSupplier().getTin());
                    supplier.setName(stock.getSupplier().getName());
                    if (stock.getSupplier().getCompanyBank() != null && stock.getSupplier().getCompanyBank().getBank() != null) {
                        BankDTO bankDTO = new BankDTO();
                        bankDTO.setMfo(stock.getSupplier().getCompanyBank().getBank().getMfo());
                        bankDTO.setOked(stock.getSupplier().getCompanyBank().getOked());
                        bankDTO.setAccountNumber(stock.getSupplier().getCompanyBank().getAccountNumber());
                        supplier.setBank(bankDTO);
                    }
                    stockIn1CDTO.setSupplier(supplier);

                    if (stock.getCompanyCommittent() != null) {
                        stockIn1CDTO.setCommittent(new CompanyBaseDTO(stock.getCompanyCommittent().getCommittentId(), stock.getCompanyCommittent().getCommittent().getName(), stock.getCompanyCommittent().getCommittent().getTin()));
                    }
                    stockIn1CDTO.setDescription(stock.getDescription());
                    stockIn1CDTO.setTransferDate(stock.getTransferDate());
                    stockIn1CDTO.setStatus(stock.getStatus());
                    stockIn1CDTO.setHasContract(stock.getCompany().isHasContract());
                    stockIn1CDTO.setTotalQty(stock.getTotalQty());
                    stockIn1CDTO.setTotalPrice(stock.getTotalPrice());
                    stockIn1CDTO.setTotalPriceWithoutVat(stock.getTotalPriceWithoutVat());
                    stockIn1CDTO.setTotalVatPrice(stock.getTotalVatPrice());
                    stock.getProducts().forEach(stockProduct -> {
                        StockInProduct1CDTO stockInProductDTO = new StockInProduct1CDTO();
                        stockInProductDTO.setStockProductId(stockProduct.getId());
                        stockInProductDTO.setId(stockProduct.getProduct().getId());
                        stockInProductDTO.setName(stockProduct.getProduct().getName());
                        stockInProductDTO.setVatBarCode(stockProduct.getProduct().getVatBarcode());
                        stockInProductDTO.setBarcode(stockProduct.getProduct().getBarcode());
                        stockInProductDTO.setQtyInPackage(stockProduct.getQtyInPackage());
                        stockInProductDTO.setCapacity(stockProduct.getProduct().getCapacity());
                        stockInProductDTO.setHasMark(stockProduct.getProduct().isHasMark());
                        stockInProductDTO.setProductType(stockProduct.getProduct().getProductType());
                        stockInProductDTO.setQty(stockProduct.getQty());
                        stockInProductDTO.setSellingPrice(stockProduct.getSellingPrice());
                        stockInProductDTO.setSellingPriceWithoutVat(stockProduct.getSellingPriceWithoutVat());
                        stockInProductDTO.setTotalSellingPrice(stockProduct.getTotalSellingPrice());
                        stockInProductDTO.setTotalSellingPriceWithoutVat(stockProduct.getTotalSellingPriceWithoutVat());
                        stockInProductDTO.setPurchasePrice(stockProduct.getPurchasePrice());
                        stockInProductDTO.setPurchasePriceWithoutVat(stockProduct.getPurchasePriceWithoutVat());
                        stockInProductDTO.setTotalPurchasePrice(stockProduct.getTotalPurchasePrice());
                        stockInProductDTO.setTotalPurchasePriceWithoutVat(stockProduct.getTotalPurchasePriceWithoutVat());
                        stockInProductDTO.setMarkupPrice(stockProduct.getMarkupPrice());
                        stockInProductDTO.setMarkupPercent(stockProduct.getMarkupPercent());
                        stockInProductDTO.setVatRate(stockProduct.getVatRate());
                        stockInProductDTO.setVatPrice(stockProduct.getVatPrice());
                        stockInProductDTO.setTotalVatPrice(stockProduct.getTotalVatPrice());
                        stockInProductDTO.setBasePrice(stockProduct.getBasePrice());
                        stockInProductDTO.setExciseRate(stockProduct.getExciseRate());
                        stockInProductDTO.setExciseSum(stockProduct.getExciseSum());

                        stockInProductDTO.setStockProductName(stockProduct.getName());
                        if (stockProduct.getProduct().getCategory() != null) {
                            stockInProductDTO.setCategory(stockProduct.getProduct().getCategory().toCommonDTO());
                        }
                        stockInProductDTO.setUnit(stockProduct.getProduct().getUnit().toDTO());
                        if (stockProduct.getProductSerial() != null) {
                            stockInProductDTO.setSerial(new CommonDateDTO(stockProduct.getProductSerial().getId(), stockProduct.getProductSerial().getSerialNumber(), stockProduct.getProductSerial().getCreatedDate()));
                        }
                        if (stockProduct.getProductContract() != null) {
                            ProductContractLookupDTO productContractLookupDTO = new ProductContractLookupDTO();
                            productContractLookupDTO.setId(stockProduct.getProductContractId());
                            productContractLookupDTO.setPrice(stockProduct.getProductContract().getPrice());
                            productContractLookupDTO.setName(stockProduct.getProductContract().getName());
                            productContractLookupDTO.setProfitRate(stockProduct.getProductContract().getProfitRate());
                            productContractLookupDTO.setBasePrice(stockProduct.getProductContract().getBasePrice());
                            stockInProductDTO.setProductContract(productContractLookupDTO);
                        }
                        stockInProductDTO.setFacturaProfitRate(stockProduct.getFacturaProfitRate());
                        if (stockProduct.getCompanyCommittent() != null && stockProduct.getCompanyCommittent().getCompany() != null) {
                            stockInProductDTO.setCommittentTin(stockProduct.getCompanyCommittent().getCompany().getTin());
                        }
                        stockIn1CDTO.addProduct(stockInProductDTO);
                    });
                    log.info("ONE_C SYNC \n LOGIN: {},  PASSWORD: {}, URL: {}", login, password, url);
                    boolean isSend = oneCClient.send(url, login, password, stockIn1CDTO);
                    stock.setOneCSync(isSend);
                    stockInRepository.save(stock);
                }
            }
        } else {
            throw validationService.badRequestExceptionThrow("COMPANY ONE_C SYN DISABLED", "COMPANY ONE_C SYN DISABLED").get();
        }

    }
}
