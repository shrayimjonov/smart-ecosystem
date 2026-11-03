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
import uz.uzkassa.smartpossupply.domain.Bank;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.Partners;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregation;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregationBlock;
import uz.uzkassa.smartpossupply.domain.stock.StockProductMark;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.category.EDocsGenerateDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.dto.stockreturn.StockReturnDTO;
import uz.uzkassa.smartpossupply.dto.stockreturn.StockReturnDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockreturn.StockReturnListDTO;
import uz.uzkassa.smartpossupply.dto.stockreturn.StockReturnProductDTO;
import uz.uzkassa.smartpossupply.dto.stockreturn.StockReturnProductDetailDTO;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.repository.BankRepository;
import uz.uzkassa.smartpossupply.repository.FacturaRepository;
import uz.uzkassa.smartpossupply.repository.PartnerRepository;
import uz.uzkassa.smartpossupply.repository.ProductContractWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.repository.ProductSerialWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationRepository;
import uz.uzkassa.smartpossupply.repository.StockProductMarkRepository;
import uz.uzkassa.smartpossupply.repository.StockProductRepository;
import uz.uzkassa.smartpossupply.repository.StockReturnRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 09.03.2023 17:13
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockReturnService extends BaseService {

    StockReturnRepository stockReturnRepository;
    StockProductRepository stockProductRepository;
    ProductRepository productRepository;
    StockProductAggregationBlockRepository stockProductAggregationBlockRepository;
    StockProductMarkRepository stockProductMarkRepository;
    StockProductAggregationRepository stockProductAggregationRepository;
    FacturaRepository facturaRepository;
    WarehouseRepository warehouseRepository;
    BankRepository bankRepository;
    CustomerService customerService;
    PartnerRepository partnerRepository;
    CisInfoService cisInfoService;
    WarehouseProductService warehouseProductService;
    EDocService eDocService;
    ProductSerialWarehouseRepository productSerialWarehouseRepository;
    ProductContractWarehouseRepository productContractWarehouseRepository;
    NumberingContext numberingContext;
    PackageQuantityService packageQuantityService;
    ValidationContext validationContext;

    @Transactional
    public Long create(StockReturnDTO stockReturnDTO) {
        validationContext.validate(stockReturnDTO);
        Company company = getCurrentCompany();
        if (stockReturnDTO.getFacturaId() != null) {
            stockReturnRepository.findFirstByCompanyIdAndFacturaIdAndTransferTypeAndDeletedIsFalse(company.getId(), stockReturnDTO.getFacturaId(), TransferType.RETURN).ifPresent(stockReturn -> {
                throw validationService.conflictExceptionThrow(
                        localizationService.localize(
                                "there.was.income.by.this.invoice",
                                new Object[]{stockReturn.getStockNumber(), stockReturn.getFactura().getFacturaNo()}
                        )
                ).get();
            });
        }

        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_RETURN, company.getId());

        Stock stockReturn = new Stock();
        stockReturn.setTransferType(TransferType.RETURN);
        stockReturn.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stockReturn.setStockNumber(numberingMap.get(NUMBER));

        stockReturn.setWarehouseId(stockReturnDTO.getWarehouseId());
        stockReturn.setBranchId(stockReturnDTO.getBranchId());
        stockReturn.setFacturaId(stockReturnDTO.getFacturaId());
        stockReturn.setContractId(stockReturnDTO.getContractId());
        stockReturn.setExchangeRate(stockReturnDTO.getExchangeRate());
        stockReturn.setCurrencyType(stockReturnDTO.getCurrencyType());

        Company customer;
        if (stockReturnDTO.getCustomer().getId() != null) {
            customer = companyRepository.findById(stockReturnDTO.getCustomer().getId())
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", stockReturnDTO.getCustomer().getId()));
        } else if (StringUtils.isNotEmpty(stockReturnDTO.getCustomer().getTin())) {
            customer = companyRepository.findFirstByTinAndDeletedIsFalse(stockReturnDTO.getCustomer().getTin()).orElseGet(Company::new);
        } else {
            customer = new Company();
        }
        if (customer.getId() == null) {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setTin(stockReturnDTO.getCustomer().getTin());
            customerDTO.setName(stockReturnDTO.getCustomer().getName());
            customerDTO.setCompanyId(getCurrentCompanyId());

            if (stockReturnDTO.getCustomer().getBank() != null) {
                Bank bank = bankRepository.findByMfo(stockReturnDTO.getCustomer().getBank().getMfo()).orElse(null);
                if (bank != null) {
                    CompanyBankDTO customerBankDTO = new CompanyBankDTO();
                    customerBankDTO.setBankId(bank.getId());
                    customerBankDTO.setAccountNumber(stockReturnDTO.getCustomer().getBank().getAccountNumber());
                    customerBankDTO.setOked(stockReturnDTO.getCustomer().getBank().getOked());
                    customerBankDTO.setMain(true);
                    customerDTO.setBank(customerBankDTO);
                }
            }
            customer = customerService.create(customerDTO);
        }
        stockReturn.setCustomerId(customer.getId());
        stockReturn.setDescription(stockReturnDTO.getDescription());
        stockReturn.setTransferDate(Optional.ofNullable(stockReturnDTO.getReturnDate()).orElse(LocalDateTime.now()));

        stockReturn.setCompanyId(company.getId());
        stockReturn.setSupplierId(company.getId());
        stockReturnRepository.save(stockReturn);

        BigDecimal grandTotalQty = BigDecimal.ZERO;
        BigDecimal grandTotalPrice = BigDecimal.ZERO;
        BigDecimal grandTotalForeignPrice = BigDecimal.ZERO;
        BigDecimal grandTotalPriceWithoutVat = BigDecimal.ZERO;
        BigDecimal grandTotalVatPrice = BigDecimal.ZERO;

        Unit unit = getUnit();

        for (StockReturnProductDTO stockReturnProductDTO : stockReturnDTO.getProducts()) {

            Product product = null;
            if (stockReturnProductDTO.getId() != null && stockReturnProductDTO.getId() > 0) {
                product =
                        productRepository
                                .findById(stockReturnProductDTO.getId())
                                .orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockReturnProductDTO.getId()));
            } else {
                if (StringUtils.isNotEmpty(stockReturnProductDTO.getBarcode())) {
                    product =
                            productRepository
                                    .findFirstByBarcodeAndBranchIdAndDeletedIsFalse(stockReturnProductDTO.getBarcode().trim(), stockReturnDTO.getBranchId())
                                    .orElse(null);
                }
                if (StringUtils.isNotEmpty(stockReturnProductDTO.getVatBarCode()) && product == null) {
                    product =
                            productRepository
                                    .findFirstByVatBarcodeAndBranchIdAndDeletedIsFalse(
                                            stockReturnProductDTO.getVatBarCode().trim(),
                                            stockReturnDTO.getBranchId()
                                    )
                                    .orElse(null);

                    if (product == null) {
                        product =
                                productRepository
                                        .findFirstByVatBarcodeAndBranchIdAndDeletedIsFalse(
                                                MarkUtils.clearVatBarcode(stockReturnProductDTO.getVatBarCode()),
                                                stockReturnDTO.getBranchId()
                                        )
                                        .orElse(null);
                    }
                }
            }

            if (product == null) {
                throw validationService.badRequestExceptionThrow("Product is null").get();
            }

            StockProduct stockReturnProduct = new StockProduct();
            stockReturnProduct.setProductId(product.getId());
            stockReturnProduct.setUnitId(unit.getId());
            stockReturnProduct.setQty(stockReturnProductDTO.getQty());
            stockReturnProduct.setProductContractId(stockReturnProductDTO.getProductContractId());
            stockReturnProduct.setProductSerialId(stockReturnProductDTO.getProductSerialId());

            stockReturnProduct.setPurchasePrice(stockReturnProductDTO.getPurchasePrice());
            stockReturnProduct.setPurchaseForeignPrice(stockReturnProductDTO.getPurchaseForeignPrice());
            stockReturnProduct.setPurchasePriceWithoutVat(stockReturnProductDTO.getPurchasePriceWithoutVat());
            stockReturnProduct.setTotalPurchasePrice(stockReturnProductDTO.getTotalPurchasePrice());
            stockReturnProduct.setTotalPurchaseForeignPrice(stockReturnProductDTO.getTotalPurchaseForeignPrice());
            stockReturnProduct.setTotalPurchasePriceWithoutVat(stockReturnProductDTO.getTotalPurchasePriceWithoutVat());

            stockReturnProduct.setSellingPrice(stockReturnProductDTO.getSellingPrice());
            stockReturnProduct.setSellingForeignPrice(stockReturnProductDTO.getSellingForeignPrice());
            stockReturnProduct.setSellingPriceWithoutVat(stockReturnProductDTO.getSellingPriceWithoutVat());
            stockReturnProduct.setTotalSellingPrice(stockReturnProductDTO.getTotalSellingPrice());
            stockReturnProduct.setTotalSellingForeignPrice(stockReturnProductDTO.getTotalSellingForeignPrice());
            stockReturnProduct.setTotalSellingPriceWithoutVat(stockReturnProductDTO.getTotalSellingPriceWithoutVat());

            stockReturnProduct.setMarkupPercent(stockReturnProductDTO.getMarkupPercent());
            stockReturnProduct.setMarkupPrice(stockReturnProductDTO.getMarkupPrice());
            stockReturnProduct.setFacturaProfitRate(stockReturnProductDTO.getFacturaProfitRate());

            stockReturnProduct.setVatRate(stockReturnProductDTO.getVatRate());
            stockReturnProduct.setVatPrice(stockReturnProductDTO.getVatPrice());
            stockReturnProduct.setTotalVatPrice(stockReturnProductDTO.getTotalVatPrice());
            stockReturnProduct.setExciseRate(stockReturnProductDTO.getExciseRate());
            stockReturnProduct.setExciseSum(stockReturnProductDTO.getExciseSum());

            stockReturnProduct.setBasePrice(stockReturnProductDTO.getBasePrice());

            stockReturnProduct.setStockId(stockReturn.getId());

            int aggregationQty = 0;
            int markQty = 0;
            int aggregationBlockQty = 0;

            if (!CollectionUtils.isEmpty(stockReturnProductDTO.getAggregationCodes())) {
                aggregationQty = stockReturnProductDTO.getAggregationCodes().size();
            }
            if (!CollectionUtils.isEmpty(stockReturnProductDTO.getMarkCodes())) {
                markQty = stockReturnProductDTO.getMarkCodes().size();
            }
            if (!CollectionUtils.isEmpty(stockReturnProductDTO.getAggregationBlocks())) {
                aggregationBlockQty = stockReturnProductDTO.getAggregationBlocks().size();
            }
            stockReturnProduct.setHasMarking(aggregationQty > 0 || markQty > 0 || aggregationBlockQty > 0);
            stockReturnProduct.setAggregationQty(aggregationQty);
            stockReturnProduct.setMarkQty(markQty);
            stockReturnProduct.setAggregationBlockQty(aggregationBlockQty);
            stockReturnProduct = stockProductRepository.save(stockReturnProduct);
            stockReturn.addProduct(stockReturnProduct);

            if (!CollectionUtils.isEmpty(stockReturnProductDTO.getPackageQuantities())) {
                packageQuantityService.create(stockReturnProduct, stockReturnProductDTO.getPackageQuantities());
            }

            if (!CollectionUtils.isEmpty(stockReturnProductDTO.getAggregationCodes())) {
                for (String aggregationCode : stockReturnProductDTO.getAggregationCodes()) {
                    StockProductAggregation stockReturnProductAggregation = new StockProductAggregation();
                    stockReturnProductAggregation.setCode(MarkUtils.clearAggregationCode(aggregationCode));
                    stockReturnProductAggregation.setOriginalCode(aggregationCode);
                    stockReturnProductAggregation.setQty(stockReturnProductDTO.getQty());
                    stockReturnProductAggregation.setProductId(product.getId());
                    stockReturnProductAggregation.setTransferType(TransferType.RETURN);
                    stockReturnProduct.addStockProductAggregation(stockReturnProductAggregation);
                }
            }

            if (!CollectionUtils.isEmpty(stockReturnProductDTO.getMarkCodes())) {
                for (String markCode : stockReturnProductDTO.getMarkCodes()) {
                    StockProductMark stockReturnProductMark = new StockProductMark();
                    stockReturnProductMark.setCode(MarkUtils.clearOriginalCode(markCode, product.getProductType()));
                    stockReturnProductMark.setOriginalCode(markCode);
                    stockReturnProductMark.setQty(stockReturnProductDTO.getQty());
                    stockReturnProductMark.setProductId(product.getId());
                    stockReturnProductMark.setTransferType(TransferType.RETURN);
                    stockReturnProduct.addStockProductMark(stockReturnProductMark);
                }
            }

            if (!CollectionUtils.isEmpty(stockReturnProductDTO.getAggregationBlocks())) {
                for (String aggregationBlockCode : stockReturnProductDTO.getAggregationBlocks()) {
                    StockProductAggregationBlock stockReturnProductAggregationBlock = new StockProductAggregationBlock();
                    stockReturnProductAggregationBlock.setCode(MarkUtils.clearOriginalCode(aggregationBlockCode, product.getProductType()));
                    stockReturnProductAggregationBlock.setOriginalCode(aggregationBlockCode);
                    stockReturnProductAggregationBlock.setQty(stockReturnProductDTO.getQty());
                    stockReturnProductAggregationBlock.setProductId(product.getId());
                    stockReturnProductAggregationBlock.setTransferType(TransferType.RETURN);
                    stockReturnProduct.addStockProductAggregationBlock(stockReturnProductAggregationBlock);
                }
            }

            grandTotalQty = grandTotalQty.add(stockReturnProductDTO.getQty());
            grandTotalPrice = grandTotalPrice.add(stockReturnProductDTO.getTotalPurchasePrice());
            if (stockReturnProductDTO.getTotalPurchaseForeignPrice() != null) {
                grandTotalForeignPrice = grandTotalForeignPrice.add(stockReturnProductDTO.getTotalPurchaseForeignPrice());
            }
            grandTotalPriceWithoutVat = grandTotalPriceWithoutVat.add(stockReturnProductDTO.getTotalPurchasePriceWithoutVat());
            if (stockReturnProductDTO.getTotalVatPrice() != null) {
                grandTotalVatPrice = grandTotalVatPrice.add(stockReturnProductDTO.getTotalVatPrice());
            }
        }
        stockReturn.setTotalQty(grandTotalQty);
        stockReturn.setTotalPrice(grandTotalPrice);
        stockReturn.setTotalForeignPrice(grandTotalForeignPrice);
        stockReturn.setTotalPriceWithoutVat(grandTotalPriceWithoutVat);
        stockReturn.setTotalVatPrice(grandTotalVatPrice);

        stockReturnRepository.save(stockReturn);
        return stockReturn.getId();
    }

    @Transactional
    public Long update(Long id, StockReturnDTO stockReturnDTO) {
        Stock stockReturn = stockReturnRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));

        this.validateCompany(stockReturn.getCompanyId());
        if (!(StockStatus.DRAFT.equals(stockReturn.getStatus()))) {
            throw validationService.badRequestExceptionThrow("Вы можете изменить только черновик").get();
        }
        if (stockReturnDTO.getFacturaId() != null) {
            if (stockReturnRepository.existsByCompanyIdAndFacturaIdAndTransferTypeAndDeletedIsFalseAndIdNot(stockReturn.getCompanyId(), stockReturnDTO.getFacturaId(), TransferType.RETURN, stockReturn.getId())) {
                throw validationService.conflictExceptionThrow(
                        localizationService.localize(
                                "there.was.income.by.this.invoice",
                                new Object[]{stockReturn.getStockNumber(), stockReturn.getFactura().getFacturaNo()}
                        )
                ).get();
            }
        }
        stockReturn.setTransferDate(stockReturnDTO.getReturnDate());
        stockReturn.setDescription(stockReturnDTO.getDescription());
        stockReturn.setContractId(stockReturnDTO.getContractId());
        stockReturn.setExchangeRate(stockReturnDTO.getExchangeRate());
        stockReturn.setCurrencyType(stockReturnDTO.getCurrencyType());

        if (stockReturnDTO.getFacturaId() != null) {
            Factura factura = facturaRepository.findById(stockReturnDTO.getFacturaId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", stockReturnDTO.getFacturaId()));
            stockReturn.setFactura(factura);
            stockReturn.setFacturaId(factura.getId());
        }

        Warehouse warehouse = warehouseRepository.findById(stockReturnDTO.getWarehouseId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Warehouse.class.getSimpleName(), "id", stockReturnDTO.getWarehouseId()));
        stockReturn.setWarehouse(warehouse);
        stockReturn.setWarehouseId(warehouse.getId());

        Branch branch = branchRepository.findById(stockReturnDTO.getBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", stockReturnDTO.getBranchId()));
        stockReturn.setBranch(branch);
        stockReturn.setBranchId(branch.getId());

        if (stockReturnDTO.getStockNumber() != null && !stockReturnDTO.getStockNumber().equals(stockReturn.getStockNumber())) {
            if (
                    stockReturnRepository.existsStockByStockNumberAndCompanyIdAndTransferTypeAndIdNot(
                            stockReturnDTO.getStockNumber(),
                            stockReturn.getCompanyId(),
                            TransferType.RETURN,
                            stockReturn.getId()
                    )
            ) {
                throw validationService.conflictExceptionThrow(
                        localizationService.localize(
                                MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                                new Object[]{
                                        localizationService.localize(MessageConstants.STOCK_RETURN),
                                        localizationService.localize(MessageConstants.NUMBER),
                                        stockReturnDTO.getStockNumber(),
                                }
                        )
                ).get();
            }
            stockReturn.setStockNumber(stockReturnDTO.getStockNumber());
        }

        if (
                stockReturnDTO.getCustomer() != null &&
                        stockReturnDTO.getCustomer().getId() != null &&
                        !stockReturn.getCustomerId().equals(stockReturnDTO.getCustomer().getId())
        ) {
            if (!partnerRepository.existsBySupplierIdAndCustomerIdAndCompanyId(stockReturn.getSupplierId(), stockReturnDTO.getCustomer().getId(), getCurrentCompanyId())) {
                partnerRepository.save(new Partners(stockReturn.getSupplierId(), stockReturnDTO.getCustomer().getId(), getCurrentCompanyId()));
            }
        }

        Unit unit = getUnit();

        Map<Long, StockProduct> stockReturnProductsMap = stockReturn
                .getProducts()
                .stream()
                .collect(Collectors.toMap(StockProduct::getId, Function.identity()));

        BigDecimal grandTotalQty = BigDecimal.ZERO;
        BigDecimal grandTotalPrice = BigDecimal.ZERO;
        BigDecimal grandTotalForeignPrice = BigDecimal.ZERO;
        BigDecimal grandTotalPriceWithoutVat = BigDecimal.ZERO;
        BigDecimal grandTotalVatPrice = BigDecimal.ZERO;

        for (StockReturnProductDTO stockReturnProductDTO : stockReturnDTO.getProducts()) {

            Product product = null;
            if (stockReturnProductDTO.getId() != null && stockReturnProductDTO.getId() > 0) {
                product =
                        productRepository
                                .findById(stockReturnProductDTO.getId())
                                .orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", stockReturnProductDTO.getId()));
            } else {
                if (StringUtils.isNotEmpty(stockReturnProductDTO.getVatBarCode())) {
                    product =
                            productRepository
                                    .findFirstByVatBarcodeAndBranchIdAndDeletedIsFalse(
                                            stockReturnProductDTO.getVatBarCode().trim(),
                                            stockReturnDTO.getBranchId()
                                    )
                                    .orElse(null);
                }
                if (StringUtils.isNotEmpty(stockReturnProductDTO.getBarcode()) && product == null) {
                    product =
                            productRepository
                                    .findFirstByBarcodeAndBranchIdAndDeletedIsFalse(stockReturnProductDTO.getBarcode().trim(), stockReturnDTO.getBranchId())
                                    .orElse(null);
                }
            }

            if (product == null) {
                throw validationService.badRequestExceptionThrow("Product is null").get();
            }


            StockProduct stockReturnProduct;
            if (stockReturnProductDTO.getStockProductId() != null && stockReturnProductsMap.get(stockReturnProductDTO.getStockProductId()) != null) {
                stockReturnProduct = stockReturnProductsMap.get(stockReturnProductDTO.getStockProductId());
            } else {
                stockReturnProduct = new StockProduct();
            }

            stockReturnProduct.setProductId(product.getId());
            stockReturnProduct.setUnitId(unit.getId());
            stockReturnProduct.setQty(stockReturnProductDTO.getQty());
            stockReturnProduct.setProductContractId(stockReturnProductDTO.getProductContractId());
            stockReturnProduct.setProductSerialId(stockReturnProductDTO.getProductSerialId());

            stockReturnProduct.setPurchasePrice(stockReturnProductDTO.getPurchasePrice());
            stockReturnProduct.setPurchaseForeignPrice(stockReturnProductDTO.getPurchaseForeignPrice());
            stockReturnProduct.setPurchasePriceWithoutVat(stockReturnProductDTO.getPurchasePriceWithoutVat());
            stockReturnProduct.setTotalPurchasePrice(stockReturnProductDTO.getTotalPurchasePrice());
            stockReturnProduct.setTotalPurchaseForeignPrice(stockReturnProductDTO.getTotalPurchaseForeignPrice());
            stockReturnProduct.setTotalPurchasePriceWithoutVat(stockReturnProductDTO.getTotalPurchasePriceWithoutVat());

            stockReturnProduct.setSellingPrice(stockReturnProductDTO.getSellingPrice());
            stockReturnProduct.setSellingForeignPrice(stockReturnProductDTO.getSellingForeignPrice());
            stockReturnProduct.setSellingPriceWithoutVat(stockReturnProductDTO.getSellingPriceWithoutVat());
            stockReturnProduct.setTotalSellingPrice(stockReturnProductDTO.getTotalSellingPrice());
            stockReturnProduct.setTotalSellingForeignPrice(stockReturnProductDTO.getTotalSellingForeignPrice());
            stockReturnProduct.setTotalSellingPriceWithoutVat(stockReturnProductDTO.getTotalSellingPriceWithoutVat());

            stockReturnProduct.setMarkupPercent(stockReturnProductDTO.getMarkupPercent());
            stockReturnProduct.setMarkupPrice(stockReturnProductDTO.getMarkupPrice());
            stockReturnProduct.setFacturaProfitRate(stockReturnProductDTO.getFacturaProfitRate());

            stockReturnProduct.setVatRate(stockReturnProductDTO.getVatRate());
            stockReturnProduct.setVatPrice(stockReturnProductDTO.getVatPrice());
            stockReturnProduct.setTotalVatPrice(stockReturnProductDTO.getTotalVatPrice());
            stockReturnProduct.setExciseRate(stockReturnProductDTO.getExciseRate());
            stockReturnProduct.setExciseSum(stockReturnProductDTO.getExciseSum());

            stockReturnProduct.setBasePrice(stockReturnProductDTO.getBasePrice());

            stockReturnProduct.setStockId(stockReturn.getId());
            stockReturnProduct = stockProductRepository.save(stockReturnProduct);
            stockReturn.addProduct(stockReturnProduct);

            if (CollectionUtils.isEmpty(stockReturnProductDTO.getPackageQuantities())) {
                packageQuantityService.deleteByCompositeId(stockReturnProduct);
            } else {
                packageQuantityService.create(stockReturnProduct, stockReturnProductDTO.getPackageQuantities());
            }

            int aggregationCount = 0;
            int markCount = 0;
            int aggregationBlockQty = 0;

            if (stockReturnProductDTO.getAggregationCodes() != null) {
                stockProductAggregationRepository.deleteAllByStockProductId(stockReturnProduct.getId());

                if (!stockReturnProductDTO.getAggregationCodes().isEmpty()) {
                    aggregationCount = stockReturnProductDTO.getAggregationCodes().size();

                    for (String aggregationCode : stockReturnProductDTO.getAggregationCodes()) {
                        StockProductAggregation stockReturnProductAggregation = new StockProductAggregation();
                        stockReturnProductAggregation.setCode(MarkUtils.clearAggregationCode(aggregationCode));
                        stockReturnProductAggregation.setOriginalCode(aggregationCode);
                        stockReturnProductAggregation.setQty(stockReturnProductDTO.getQty());
                        stockReturnProductAggregation.setProductId(product.getId());
                        stockReturnProductAggregation.setTransferType(TransferType.RETURN);
                        stockReturnProduct.addStockProductAggregation(stockReturnProductAggregation);
                    }
                }
            }

            if (stockReturnProductDTO.getMarkCodes() != null) {
                stockProductMarkRepository.deleteAllByStockProductId(stockReturnProduct.getId());

                if (!stockReturnProductDTO.getMarkCodes().isEmpty()) {
                    markCount = stockReturnProductDTO.getMarkCodes().size();
                    List<StockProductMark> stockReturnProductMarks = new ArrayList<>();
                    for (String markCode : stockReturnProductDTO.getMarkCodes()) {
                        StockProductMark stockProductMark = new StockProductMark();
                        stockProductMark.setCode(MarkUtils.clearOriginalCode(markCode, product.getProductType()));
                        stockProductMark.setOriginalCode(markCode);
                        stockProductMark.setQty(stockReturnProductDTO.getQty());
                        stockProductMark.setTransferType(TransferType.RETURN);
                        stockReturnProduct.addStockProductMark(stockProductMark);
                    }

                    stockProductMarkRepository.saveAll(stockReturnProductMarks);
                }
            }

            if (stockReturnProductDTO.getAggregationBlocks() != null) {
                stockProductAggregationBlockRepository.deleteAllByStockProductId(stockReturnProduct.getId());
                if (!stockReturnProductDTO.getAggregationBlocks().isEmpty()) {
                    aggregationBlockQty = stockReturnProductDTO.getAggregationBlocks().size();
                    List<StockProductAggregationBlock> stockReturnProductAggregationBlocks = new ArrayList<>();
                    for (String aggregationBlockCode : stockReturnProductDTO.getAggregationBlocks()) {
                        StockProductAggregationBlock stockReturnProductAggregationBlock = new StockProductAggregationBlock();
                        stockReturnProductAggregationBlock.setCode(MarkUtils.clearOriginalCode(aggregationBlockCode, product.getProductType()));
                        stockReturnProductAggregationBlock.setOriginalCode(aggregationBlockCode);
                        stockReturnProductAggregationBlock.setQty(stockReturnProductDTO.getQty());
                        stockReturnProductAggregationBlock.setTransferType(TransferType.RETURN);
                        stockReturnProduct.addStockProductAggregationBlock(stockReturnProductAggregationBlock);
                    }

                    stockProductAggregationBlockRepository.saveAll(stockReturnProductAggregationBlocks);
                }
            }

            grandTotalQty = grandTotalQty.add(stockReturnProductDTO.getQty());
            grandTotalPrice = grandTotalPrice.add(Optional.ofNullable(stockReturnProductDTO.getTotalPurchasePrice()).orElse(BigDecimal.ZERO));
            if (stockReturnProductDTO.getTotalPurchaseForeignPrice() != null) {
                grandTotalForeignPrice = grandTotalForeignPrice.add(stockReturnProductDTO.getTotalPurchaseForeignPrice());
            }
            if (stockReturnProductDTO.getTotalPurchasePriceWithoutVat() != null) {
                grandTotalPriceWithoutVat = grandTotalPriceWithoutVat.add(stockReturnProductDTO.getTotalPurchasePriceWithoutVat());
            }
            if (stockReturnProductDTO.getTotalVatPrice() != null) {
                grandTotalVatPrice = grandTotalVatPrice.add(stockReturnProductDTO.getTotalVatPrice());
            }

            if (stockReturnProduct.getId() != null) {
                stockReturnProductsMap.remove(stockReturnProduct.getId());
            }

            if (aggregationCount > 0 || markCount > 0 || aggregationBlockQty > 0) {
                stockReturnProduct.setHasMarking(true);
                stockReturnProduct.setAggregationQty(aggregationCount);
                stockReturnProduct.setMarkQty(markCount);
                stockReturnProduct.setAggregationBlockQty(aggregationBlockQty);
                stockProductRepository.save(stockReturnProduct);
            }
        }

        stockReturn.setTotalQty(grandTotalQty);
        stockReturn.setTotalPrice(grandTotalPrice);
        stockReturn.setTotalForeignPrice(grandTotalForeignPrice);
        stockReturn.setTotalPriceWithoutVat(grandTotalPriceWithoutVat);
        stockReturn.setTotalVatPrice(grandTotalVatPrice);
        stockReturnRepository.save(stockReturn);

        if (!CollectionUtils.isEmpty(stockReturnProductsMap)) {
            List<Long> oldStockProductIds = new ArrayList<>(stockReturnProductsMap.keySet());
            stockProductAggregationRepository.deleteAllByStockProductIds(oldStockProductIds);
            stockProductMarkRepository.deleteAllByStockProductIds(oldStockProductIds);
            stockProductAggregationBlockRepository.deleteAllByStockProductIds(oldStockProductIds);
            stockProductRepository.deleteByIds(oldStockProductIds);
        }
        return id;
    }

    public Page<StockReturnListDTO> getList(StockFilter filter) {
        ResultList<Stock> resultList = stockReturnRepository.getResultList(filter);
        List<StockReturnListDTO> result = resultList
                .getList()
                .stream()
                .map(stockReturn -> {
                    StockReturnListDTO stockReturnListDTO = new StockReturnListDTO();
                    stockReturnListDTO.setId(stockReturn.getId());
                    stockReturnListDTO.setStockNumber(stockReturn.getStockNumber());
                    stockReturnListDTO.setStatus(stockReturn.getStatus());
                    stockReturnListDTO.setTransferDate(stockReturn.getTransferDate());
                    stockReturnListDTO.setCustomer(stockReturn.getCustomer().toCommonBaseDTO());
                    stockReturnListDTO.setBranch(stockReturn.getBranch().toCommonDTO());
                    stockReturnListDTO.setWarehouse(stockReturn.getWarehouse().toCommonDTO());
                    stockReturnListDTO.setTotalPrice(stockReturn.getTotalPrice());
                    stockReturnListDTO.setTotalForeignPrice(stockReturn.getTotalForeignPrice());
                    stockReturnListDTO.setCurrencyType(stockReturn.getCurrencyType());
                    if (stockReturn.getFactura() != null) {
                        stockReturnListDTO.setFactura(stockReturn.getFactura().toLink());
                    }
                    stockReturnListDTO.setLastModifiedDate(stockReturn.getUpdatedDate());
                    return stockReturnListDTO;
                })
                .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public CheckStatusResponseDTO updateStatus(Long id, StockStatus status, LocalDateTime lastModifiedDate) {
        Stock stockReturn = stockReturnRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        if (!stockReturn.getUpdatedDate().equals(lastModifiedDate)) {
            return new CheckStatusResponseDTO(false, 1);
        }
        this.validateCompany(stockReturn.getCompanyId());
        if (StockStatus.COMPLETED.equals(status)) {
            if (!StockStatus.DRAFT.equals(stockReturn.getStatus())) {
                throw validationService.badRequestExceptionThrow("Вы можете заполнить только черновик").get();
            }
            warehouseProductService.in(stockReturn, true);
            stockReturn.getProducts().forEach(stockProduct -> warehouseProductService.updateCodes(stockProduct, false));
            stockReturn.setStatus(status);
            stockReturn.setCompletedDate(LocalDateTime.now());
            stockReturnRepository.save(stockReturn);
            eDocService.generateDoc(new EDocsGenerateDTO(stockReturn.getId(), EntityTypeEnum.STOCK_RETURN, stockReturn.getCompanyId()));
        } else if (StockStatus.CANCELLED.equals(status)) {
            if (!StockStatus.COMPLETED.equals(stockReturn.getStatus())) {
                throw validationService.badRequestExceptionThrow("Вы можете отклонять только Завершен").get();
            }
            warehouseProductService.out(stockReturn);
            stockReturn.getProducts().forEach(stockProduct -> warehouseProductService.updateCodes(stockProduct, true));
            stockReturn.setStatus(status);
            stockReturnRepository.save(stockReturn);
        }
        return new CheckStatusResponseDTO(true, 0);
    }

    @Transactional
    public Long copy(Long id) {
        Stock oldStockReturn = stockReturnRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        if (!StockStatus.CANCELLED.equals(oldStockReturn.getStatus())) {
            throw validationService.badRequestExceptionThrow("Чтобы скопировать возврат, необходимо ee отменить").get();
        }
        this.validateCompany(oldStockReturn.getCompanyId());

        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_RETURN, oldStockReturn.getCompanyId());

        Stock stock = new Stock();
        stock.setOldStockId(oldStockReturn.getId());
        stock.setTransferType(TransferType.RETURN);
        stock.setTransferDate(Optional.ofNullable(oldStockReturn.getTransferDate()).orElse(LocalDateTime.now()));
        stock.setStockNumber(numberingMap.get(NUMBER));
        stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stock.setContractId(oldStockReturn.getContractId());
        stock.setDescription(oldStockReturn.getDescription());
        stock.setWarehouseId(oldStockReturn.getWarehouseId());
        stock.setBranchId(oldStockReturn.getBranchId());
        stock.setCustomerId(oldStockReturn.getCustomerId());
        stock.setSupplierId(oldStockReturn.getSupplierId());
        stock.setCompanyId(oldStockReturn.getCompanyId());
        stock.setPriceListId(oldStockReturn.getPriceListId());
        stock.setDeliveryBranchId(oldStockReturn.getDeliveryBranchId());
        if (oldStockReturn.getOrder() != null) {
            stock.setOrderId(oldStockReturn.getOrder().getId());
        }
        stock.setFacturaId(oldStockReturn.getFacturaId());
        stock.setExchangeRate(oldStockReturn.getExchangeRate());
        stock.setCurrencyType(oldStockReturn.getCurrencyType());
        stock = stockReturnRepository.save(stock);

        for (StockProduct oldStockProduct : oldStockReturn.getProducts()) {
            StockProduct stockProduct = new StockProduct();
            stockProduct.setProductId(oldStockProduct.getProductId());
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
            stockProduct.setTotalPurchasePriceWithoutVat(oldStockProduct.getTotalPurchasePriceWithoutVat());
            stockProduct.setTotalPurchasePrice(oldStockProduct.getTotalPurchasePrice());

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


            stockProduct.setStockId(stock.getId());
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
                    stockProductAggregation.setTransferType(TransferType.RETURN);
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
                    stockProductMark.setTransferType(TransferType.RETURN);
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
                    stockProductAggregationBlock.setTransferType(TransferType.RETURN);
                    stockProductAggregationBlocks.add(stockProductAggregationBlock);
                }
                stockProductAggregationBlockRepository.saveAll(stockProductAggregationBlocks);
                stockProductAggregationBlockRepository.deleteAll(stockProduct.getStockProductAggregationBlocks());
            }
        }
        stock.setTotalQty(oldStockReturn.getTotalQty());
        stock.setTotalPrice(oldStockReturn.getTotalPrice());
        stock.setTotalForeignPrice(oldStockReturn.getTotalForeignPrice());
        stock.setTotalPriceWithoutVat(oldStockReturn.getTotalPriceWithoutVat());
        stock.setTotalVatPrice(oldStockReturn.getTotalVatPrice());
        stockReturnRepository.save(stock);
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

    @Override
    @Transactional
    public void deleteById(Long id) {
        Stock stockReturn = stockReturnRepository.get(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
        if (!(StockStatus.DRAFT.equals(stockReturn.getStatus()) || StockStatus.CANCELLED.equals(stockReturn.getStatus()))) {
            throw validationService.badRequestExceptionThrow("Вы можете удалить черновик или отмененный").get();
        }
        this.validateCompany(stockReturn.getCompanyId());
        List<Long> stockReturnProducts = stockReturn.getProducts().stream().map(AbstractAuditingEntity::getId).toList();
        stockProductRepository.deleteByStockId(id);
        stockProductAggregationRepository.deleteAllByStockProductIds(stockReturnProducts);
        stockProductAggregationBlockRepository.deleteAllByStockProductIds(stockReturnProducts);
        stockProductMarkRepository.deleteAllByStockProductIds(stockReturnProducts);
        stockReturn.setDeleted(true);
        stockReturn.setStatus(StockStatus.DELETED);
        stockReturnRepository.save(stockReturn);
    }

    public StockReturnDetailDTO get(Long id) {
        return stockReturnRepository
                .getDetail(id)
                .map(stockReturn -> {
                    this.validateCompany(stockReturn.getCompanyId());
                    StockReturnDetailDTO stockReturnDetailDTO = new StockReturnDetailDTO();
                    stockReturnDetailDTO.setId(stockReturn.getId());
                    stockReturnDetailDTO.setStockNumber(stockReturn.getStockNumber());
                    stockReturnDetailDTO.setStatus(stockReturn.getStatus());
                    stockReturnDetailDTO.setTransferDate(stockReturn.getTransferDate());
                    stockReturnDetailDTO.setCustomer(stockReturn.getCustomer().toCommonBaseDTO());
                    stockReturnDetailDTO.setBranch(stockReturn.getBranch().toCommonDTO());
                    stockReturnDetailDTO.setWarehouse(stockReturn.getWarehouse().toCommonDTO());
                    stockReturnDetailDTO.setTotalQty(stockReturn.getTotalQty());
                    stockReturnDetailDTO.setTotalPrice(stockReturn.getTotalPrice());
                    stockReturnDetailDTO.setTotalForeignPrice(stockReturn.getTotalForeignPrice());
                    stockReturnDetailDTO.setExchangeRate(stockReturn.getExchangeRate());
                    stockReturnDetailDTO.setCurrencyType(stockReturn.getCurrencyType());
                    stockReturnDetailDTO.setTotalPriceWithoutVat(stockReturn.getTotalPriceWithoutVat());
                    stockReturnDetailDTO.setTotalVatPrice(stockReturn.getTotalVatPrice());
                    stockReturnDetailDTO.setDescription(stockReturn.getDescription());
                    if (stockReturn.getFacturaId() != null) {
                        stockReturnDetailDTO.setFactura(stockReturn.getFactura().toLink());
                    }
                    if (stockReturn.getContractId() != null) {
                        stockReturnDetailDTO.setContract(stockReturn.getContract().toLookUpDTO());
                    }

                    stockReturn
                            .getProducts()
                            .forEach(stockProduct -> {
                                StockReturnProductDetailDTO stockReturnProductDetailDTO = new StockReturnProductDetailDTO();
                                stockReturnProductDetailDTO.setStockProductId(stockProduct.getId());
                                stockReturnProductDetailDTO.setId(stockProduct.getProduct().getId());
                                stockReturnProductDetailDTO.setName(stockProduct.getProduct().getName());
                                stockReturnProductDetailDTO.setProductType(stockProduct.getProduct().getProductType());
                                stockReturnProductDetailDTO.setVatBarCode(stockProduct.getProduct().getVatBarcode());
                                stockReturnProductDetailDTO.setBarcode(stockProduct.getProduct().getBarcode());
                                stockReturnProductDetailDTO.setCapacity(stockProduct.getProduct().getCapacity());
                                stockReturnProductDetailDTO.setBoxBarcode(stockProduct.getProduct().getBoxBarcode());
                                stockReturnProductDetailDTO.setBlockBarcode(stockProduct.getProduct().getBlockBarcode());
                                if (stockProduct.getProduct().getParent() != null) {
                                    stockReturnProductDetailDTO.setParent(stockProduct.getProduct().getParent().toProductCommonDTO());
                                    if (StringUtils.isEmpty(stockReturnProductDetailDTO.getBlockBarcode())) {
                                        stockReturnProductDetailDTO.setBlockBarcode(stockProduct.getProduct().getParent().getBarcode());
                                    }
                                }
                                stockReturnProductDetailDTO.setQty(stockProduct.getQty());
                                if (stockProduct.getProductContractId() != null) {
                                    stockReturnDetailDTO.setHasContract(true);
                                    stockReturnProductDetailDTO.setProductContract(stockProduct.getProductContract().toCommonDTO());
                                }
                                if (stockProduct.getProductSerialId() != null) {
                                    stockReturnProductDetailDTO.setProductSerial(stockProduct.getProductSerial().toLookUpDTO());
                                }
                                if (stockProduct.getProductContractId() != null) {
                                    stockReturnProductDetailDTO.setQtyInWarehouseByContract(productContractWarehouseRepository.getQty(Optional.ofNullable(stockReturn.getWarehouseId()).orElseGet(stockProduct::getWarehouseId), stockProduct.getProductContractId()));
                                }
                                if (stockProduct.getProductSerialId() != null) {
                                    stockReturnProductDetailDTO.setQtyInWarehouseBySerial(productSerialWarehouseRepository.getQty(Optional.ofNullable(stockReturn.getWarehouseId()).orElseGet(stockProduct::getWarehouseId), stockProduct.getProductSerialId()));
                                }
                                stockReturnDetailDTO.setLastModifiedDate(stockReturn.getUpdatedDate());

                                stockReturnProductDetailDTO.setPurchasePrice(stockProduct.getPurchasePrice());
                                stockReturnProductDetailDTO.setPurchaseForeignPrice(stockProduct.getPurchaseForeignPrice());
                                stockReturnProductDetailDTO.setPurchasePriceWithoutVat(stockProduct.getPurchasePriceWithoutVat());
                                stockReturnProductDetailDTO.setTotalPurchasePrice(stockProduct.getTotalPurchasePrice());
                                stockReturnProductDetailDTO.setTotalPurchaseForeignPrice(stockProduct.getTotalPurchaseForeignPrice());
                                stockReturnProductDetailDTO.setTotalPurchasePriceWithoutVat(stockProduct.getTotalPurchasePriceWithoutVat());

                                stockReturnProductDetailDTO.setSellingPrice(stockProduct.getSellingPrice());
                                stockReturnProductDetailDTO.setSellingForeignPrice(stockProduct.getSellingForeignPrice());
                                stockReturnProductDetailDTO.setSellingPriceWithoutVat(stockProduct.getSellingPriceWithoutVat());
                                stockReturnProductDetailDTO.setTotalSellingPrice(stockProduct.getTotalSellingPrice());
                                stockReturnProductDetailDTO.setTotalSellingForeignPrice(stockProduct.getTotalSellingForeignPrice());
                                stockReturnProductDetailDTO.setTotalSellingPriceWithoutVat(stockProduct.getTotalSellingPriceWithoutVat());

                                stockReturnProductDetailDTO.setMarkupPrice(stockProduct.getMarkupPrice());
                                stockReturnProductDetailDTO.setMarkupPercent(stockProduct.getMarkupPercent());
                                stockReturnProductDetailDTO.setFacturaProfitRate(stockProduct.getFacturaProfitRate());

                                stockReturnProductDetailDTO.setVatPrice(stockProduct.getVatPrice());
                                stockReturnProductDetailDTO.setVatRate(stockProduct.getVatRate());
                                stockReturnProductDetailDTO.setTotalVatPrice(stockProduct.getTotalVatPrice());
                                stockReturnProductDetailDTO.setExciseRate(stockProduct.getExciseRate());
                                stockReturnProductDetailDTO.setExciseSum(stockProduct.getExciseSum());

                                stockReturnProductDetailDTO.setBasePrice(stockProduct.getBasePrice());

                                stockReturnProductDetailDTO.setUnit(stockProduct.getUnit().toDTO());
                                stockReturnProductDetailDTO.setQtyInPackage(stockProduct.getQtyInPackage());
                                stockReturnProductDetailDTO.setAggregationQty(stockProduct.getAggregationQty());
                                stockReturnProductDetailDTO.setMarkQty(stockProduct.getMarkQty());
                                stockReturnProductDetailDTO.setAggregationBlockQty(stockProduct.getAggregationBlockQty());
                                stockReturnProductDetailDTO.setHasMarking(stockProduct.isHasMarking());
                                stockReturnProductDetailDTO.setHasMark(stockProduct.getProduct().isHasMark());
                                stockReturnProductDetailDTO.setBlockProducts(stockProduct.getProduct().getBlockProducts());
                                stockReturnProductDetailDTO.setPackageQuantities(packageQuantityService.getByCompositeId(stockProduct));
                                stockReturnDetailDTO.addProduct(stockReturnProductDetailDTO);
                            });
                    return stockReturnDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
    }

    public List<CisInfoResponse> check(BatchValidationDTO validationDTO) {
        Map<String, CisInfoResponse> resultMap = new LinkedHashMap<>();
        List<String> aggregationCodes = validationDTO.getAggregationCodes();
        List<String> markCodes = validationDTO.getMarkCodes();
        List<String> aggregationBlockCodes = validationDTO.getAggregationBlockCodes();
        CompanySettings xtraceSettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.XTRACE_ENABLED).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanySettings.class.getSimpleName(), "companyId", getCurrentCompanyId()));

        //Aggregations
        if (!CollectionUtils.isEmpty(aggregationCodes)) {
            aggregationCodes.forEach(aggregationCode -> {
                CisInfo cisInfo = new CisInfo();
                cisInfo.setRequestedCis(MarkUtils.clearAggregationCode(aggregationCode));
                cisInfo.setOriginalCode(aggregationCode);
                resultMap.put(aggregationCode, new CisInfoResponse(cisInfo));
            });
            if (xtraceSettings.isEnabled()) {
                cisInfoService.batchCheckAggregation(validationDTO, resultMap, aggregationCodes);
            }
            List<String> aggregationCodesForUsedCheck = new ArrayList<>(resultMap.values().stream().filter(cisInfoResponse -> TuronMarkStatus.isSuccess(cisInfoResponse.getCisInfo().getStatus()))
                    .map(cisInfoResponse -> Optional.ofNullable(cisInfoResponse.getCisInfo().getRequestedCis()).orElseGet(() -> cisInfoResponse.getCisInfo().getCode())).toList());
            if (!aggregationCodesForUsedCheck.isEmpty()) {
                List<String> stockOutProductAggregationCodes = stockProductAggregationRepository.getCodesByTransferTypeAndCodesAndStatusNot(
                        TransferType.OUT,
                        aggregationCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockStatus.CANCELLED
                );

                stockOutProductAggregationCodes.forEach(aggregationCode -> {
                    if (!aggregationCodesForUsedCheck.contains(aggregationCode)) {
                        CisInfoResponse cisInfoResponse = resultMap.get(aggregationCode);
                        CisInfo cisInfo = cisInfoResponse.getCisInfo();
                        cisInfo.setStatus(TuronMarkStatus.ERROR);
                        cisInfo.setErrorMessage("Код не был отгружен");
                        cisInfoResponse.setCisInfo(cisInfo);
                        resultMap.remove(cisInfo.getOriginalCode());
                        resultMap.put(cisInfo.getOriginalCode(), cisInfoResponse);
                    }
                });

                List<StockProductAggregation> stockProductAggregations = stockProductAggregationRepository.getByTransferTypeAndCodesAndStatusNot(
                        TransferType.IN,
                        aggregationCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockStatus.CANCELLED
                );

                if (!stockProductAggregations.isEmpty()) {
                    Map<String, StockProductAggregation> aggregationMap = new LinkedHashMap<>();
                    stockProductAggregations.forEach(stockProductAggregation -> {
                        if (!aggregationMap.containsKey(stockProductAggregation.getCode())) {
                            aggregationMap.put(stockProductAggregation.getCode(), stockProductAggregation);
                        }
                    });

                    aggregationMap.forEach((code, stockProductAggregation) -> {
                        if (TransferType.RETURN == stockProductAggregation.getTransferType()) {
                            CisInfoResponse cisInfoResponse = resultMap.get(stockProductAggregation.getCode());
                            if (cisInfoResponse == null) {
                                cisInfoResponse = resultMap.get(stockProductAggregation.getOriginalCode());
                            }
                            if (cisInfoResponse != null) {
                                CisInfo cisInfo = cisInfoResponse.getCisInfo();
                                cisInfo.setStatus(TuronMarkStatus.EMITTED);
                                cisInfo.setErrorMessage("Код уже был возвращен");

                                cisInfo.setEntityId(stockProductAggregation.getStockProduct().getStockId());
                                cisInfo.setEntityType(EntityTypeEnum.STOCK_RETURN.name());
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

        //Marks
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
                resultMap.put(markCode, new CisInfoResponse(cisInfo));
            });

            if (xtraceSettings.isEnabled()) {
                cisInfoService.batchCheckMark(validationDTO, resultMap, markCodes);
            }
            List<String> markCodesForUsedCheck = new ArrayList<>(resultMap.values().stream().filter(cisInfoResponse -> TuronMarkStatus.isSuccess(cisInfoResponse.getCisInfo().getStatus()))
                    .map(cisInfoResponse -> Optional.ofNullable(cisInfoResponse.getCisInfo().getRequestedCis()).orElseGet(() -> cisInfoResponse.getCisInfo().getCode())).toList());
            if (!markCodesForUsedCheck.isEmpty()) {
                List<String> stockOutProductMarkCodes = stockProductMarkRepository.getCodesByTransferTypeAndCodesAndStatusNot(
                        TransferType.OUT,
                        markCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockStatus.CANCELLED
                );

                stockOutProductMarkCodes.forEach(markCode -> {
                    if (!markCodesForUsedCheck.contains(markCode)) {
                        CisInfoResponse cisInfoResponse = resultMap.get(markCode);
                        CisInfo cisInfo = cisInfoResponse.getCisInfo();
                        cisInfo.setStatus(TuronMarkStatus.ERROR);
                        cisInfo.setErrorMessage("Код не был отгружен");
                        cisInfoResponse.setCisInfo(cisInfo);
                        resultMap.remove(cisInfo.getOriginalCode());
                        resultMap.put(cisInfo.getOriginalCode(), cisInfoResponse);
                    }
                });

                //Check for duplicate
                List<StockProductMark> stockProductMarks = stockProductMarkRepository.getByTransferTypeAndCodesAndStatusNot(
                        TransferType.IN,
                        markCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockStatus.CANCELLED
                );

                if (!stockProductMarks.isEmpty()) {
                    Map<String, StockProductMark> aggregationMap = new LinkedHashMap<>();
                    stockProductMarks.forEach(stockProductMark -> {
                        if (!aggregationMap.containsKey(stockProductMark.getCode())) {
                            aggregationMap.put(stockProductMark.getCode(), stockProductMark);
                        }
                    });
                    aggregationMap.forEach((code, stockProductMark) -> {
                        CisInfoResponse cisInfoResponse = resultMap.get(stockProductMark.getOriginalCode());
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.get(stockProductMark.getCode());
                        }
                        if (cisInfoResponse != null) {
                            CisInfo cisInfo = cisInfoResponse.getCisInfo();
                            cisInfo.setStatus(TuronMarkStatus.EMITTED);
                            cisInfo.setErrorMessage("Код уже был возвращен");
                            cisInfo.setEntityId(stockProductMark.getStockProduct().getStockId());
                            cisInfo.setEntityType(EntityTypeEnum.STOCK_RETURN.name());
                            cisInfo.setEntityNumber(stockProductMark.getStockProduct().getStock().getStockNumber());

                            if (StringUtils.isNotEmpty(validationDTO.getBarcode()) && StringUtils.isEmpty(cisInfo.getGtin())) {
                                cisInfo.setGtin(validationDTO.getBarcode());
                            }
                            cisInfo.setProductName(stockProductMark.getStockProduct().getProduct().getName());
                            cisInfoResponse.setCisInfo(cisInfo);
                            resultMap.remove(cisInfo.getOriginalCode());
                            resultMap.put(cisInfo.getOriginalCode(), cisInfoResponse);
                        }
                    });
                }
            }
        }

        //Aggregation Blocks
        if (!CollectionUtils.isEmpty(aggregationBlockCodes)) {
            aggregationBlockCodes.forEach(aggregationBlockCode -> {
                CisInfo cisInfo = new CisInfo();
                cisInfo.setRequestedCis(MarkUtils.clearOriginalCode(aggregationBlockCode, validationDTO.getProductType()));
                cisInfo.setOriginalCode(aggregationBlockCode);
                resultMap.put(aggregationBlockCode, new CisInfoResponse(cisInfo));
            });
            if (xtraceSettings.isEnabled()){
                cisInfoService.batchCheckAggregationBlock(validationDTO, resultMap, aggregationBlockCodes);
            }

            List<String> aggregationBlockCodesForUsedCheck = new ArrayList<>(resultMap.values().stream().filter(cisInfoResponse -> TuronMarkStatus.isSuccess(cisInfoResponse.getCisInfo().getStatus()))
                    .map(cisInfoResponse -> Optional.ofNullable(cisInfoResponse.getCisInfo().getRequestedCis()).orElseGet(() -> cisInfoResponse.getCisInfo().getOriginalCode())).toList());
            if (!aggregationBlockCodes.isEmpty()) {
                List<String> stockOutProductAggregationBlockCodes = stockProductAggregationBlockRepository.getCodesByTransferTypeAndCodesAndStatusNot(
                        TransferType.OUT,
                        aggregationBlockCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockStatus.CANCELLED
                );

                stockOutProductAggregationBlockCodes.forEach(aggregationBlockCode -> {
                    if (!aggregationBlockCodesForUsedCheck.contains(aggregationBlockCode)) {
                        CisInfoResponse cisInfoResponse = resultMap.get(aggregationBlockCode);
                        CisInfo cisInfo = cisInfoResponse.getCisInfo();
                        cisInfo.setStatus(TuronMarkStatus.ERROR);
                        cisInfo.setErrorMessage("Код не был отгружен");
                        cisInfoResponse.setCisInfo(cisInfo);
                        resultMap.remove(cisInfo.getOriginalCode());
                        resultMap.put(cisInfo.getOriginalCode(), cisInfoResponse);
                        aggregationBlockCodesForUsedCheck.remove(aggregationBlockCode);
                    }
                });

                //Check for duplicate
                List<StockProductAggregationBlock> stockProductAggregationBlocks = stockProductAggregationBlockRepository.getByTransferTypeAndCodesAndStatusNot(
                        TransferType.IN,
                        aggregationBlockCodesForUsedCheck,
                        getCurrentCompanyId(),
                        StockStatus.CANCELLED
                );

                if (!stockProductAggregationBlocks.isEmpty()) {
                    Map<String, StockProductAggregationBlock> aggregationMap = new LinkedHashMap<>();
                    stockProductAggregationBlocks.forEach(stockProductAggregationBlock -> {
                        if (!aggregationMap.containsKey(stockProductAggregationBlock.getCode())) {
                            aggregationMap.put(stockProductAggregationBlock.getCode(), stockProductAggregationBlock);
                        }
                    });
                    aggregationMap.forEach((code, stockProductAggregationBlock) -> {
                        CisInfoResponse cisInfoResponse = resultMap.get(stockProductAggregationBlock.getOriginalCode());
                        if (cisInfoResponse == null) {
                            cisInfoResponse = resultMap.get(stockProductAggregationBlock.getCode());
                        }
                        if (cisInfoResponse != null) {
                            CisInfo cisInfo = cisInfoResponse.getCisInfo();
                            cisInfo.setStatus(TuronMarkStatus.EMITTED);
                            cisInfo.setErrorMessage("Код уже был возвращен");

                            cisInfo.setEntityId(stockProductAggregationBlock.getStockProduct().getStockId());
                            cisInfo.setEntityType(EntityTypeEnum.STOCK_RETURN.name());
                            cisInfo.setEntityNumber(stockProductAggregationBlock.getStockProduct().getStock().getStockNumber());

                            if (StringUtils.isNotEmpty(validationDTO.getBarcode()) && StringUtils.isEmpty(cisInfo.getGtin())) {
                                cisInfo.setGtin(validationDTO.getBarcode());
                            }
                            cisInfo.setProductName(stockProductAggregationBlock.getStockProduct().getProduct().getName());
                            cisInfoResponse.setCisInfo(cisInfo);

                            resultMap.remove(cisInfo.getOriginalCode());
                            resultMap.put(cisInfo.getOriginalCode(), cisInfoResponse);
                        }
                    });
                }
            }
        }
        return Arrays.asList(resultMap.values().toArray(new CisInfoResponse[0]));
    }

    @Transactional
    public void assignCashier(Long id, Long cashierId) {
        stockReturnRepository
                .findById(id)
                .map(stockReturn -> {
                    stockReturn.setCashierId(cashierId);
                    stockReturnRepository.save(stockReturn);
                    return stockReturn;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
    }
}
