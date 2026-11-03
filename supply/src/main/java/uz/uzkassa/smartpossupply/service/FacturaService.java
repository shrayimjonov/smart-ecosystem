package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.billing.UsagePlan;
import uz.uzkassa.smartpossupply.domain.branch.BranchBank;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyVatRegistration;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.domain.factura.FacturaDetail;
import uz.uzkassa.smartpossupply.domain.factura.FacturaGUID;
import uz.uzkassa.smartpossupply.domain.factura.FacturaProduct;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregation;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregationBlock;
import uz.uzkassa.smartpossupply.domain.stock.StockProductMark;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponseResult;
import uz.uzkassa.smartpossupply.dto.auth.SignDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpossupply.dto.eimzo.SignatureCertificateDto;
import uz.uzkassa.smartpossupply.dto.factura.CheckFacturaCodeDTO;
import uz.uzkassa.smartpossupply.dto.factura.CompanyCheckDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaCompanyCommittentDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaCompanyDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaContractDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaEmpowermentDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaForeignCompanyDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaItemReleasedDocDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaLookUpDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaProductDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaProductListDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaSendDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaShortDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaStatisticsByStatusDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.factura.InvestmentObjectBuyerDTO;
import uz.uzkassa.smartpossupply.dto.factura.InvestmentObjectDTO;
import uz.uzkassa.smartpossupply.dto.factura.MarkDTO;
import uz.uzkassa.smartpossupply.dto.factura.OldFacturaDoc;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.dto.factura.supply.SupplyFacturaDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.SupplyFacturaListDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.SupplyFacturaUpdateDTO;
import uz.uzkassa.smartpossupply.dto.factura.sync.FacturaSyncDTO;
import uz.uzkassa.smartpossupply.dto.payload.GenerateFacturaGUIDPayload;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.dto.payload.SyncFacturaByIdPayload;
import uz.uzkassa.smartpossupply.dto.payload.SyncTasnifCatalogByProductPayload;
import uz.uzkassa.smartpossupply.dto.soliq.CompanyVatDTO;
import uz.uzkassa.smartpossupply.dto.soliq.ProviderBindingDTO;
import uz.uzkassa.smartpossupply.dto.soliq.VATCompanyDateDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.filters.FacturaFilter;
import uz.uzkassa.smartpossupply.filters.FacturaStatisticsFilter;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.integration.EimzoClient;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.integration.SoliqClient;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqFacturaProducer;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqTasnifCatalogProducer;
import uz.uzkassa.smartpossupply.repository.BranchBankRepository;
import uz.uzkassa.smartpossupply.repository.CompanyVatRegistrationRepository;
import uz.uzkassa.smartpossupply.repository.FacturaDetailRepository;
import uz.uzkassa.smartpossupply.repository.FacturaGUIDRepository;
import uz.uzkassa.smartpossupply.repository.FacturaProductRepository;
import uz.uzkassa.smartpossupply.repository.FacturaRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.repository.ReceiptRepository;
import uz.uzkassa.smartpossupply.repository.StockOutRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class FacturaService extends BaseService {

    private static final ConcurrentHashMap<Long, Long> UNITS = new ConcurrentHashMap<>();
    FacturaRepository facturaRepository;
    FacturaDetailRepository facturaDetailRepository;
    RabbitMqFacturaProducer rabbitMqFacturaProducer;
    StockOutRepository stockOutRepository;
    FacturaRestClient facturaRestClient;
    SoliqClient soliqClient;
    ProductRepository productRepository;
    RabbitMqTasnifCatalogProducer rabbitMqTasnifCatalogProducer;
    AslBelgiClient aslBelgiClient;
    FacturaProductRepository facturaProductRepository;
    FacturaWebhookService facturaWebhookService;
    FacturaGUIDRepository facturaGUIDRepository;
    CompanyVatRegistrationRepository companyVatRegistrationRepository;
    EimzoClient eimzoClient;
    TelegramBotApplicationLogService telegramApplicationLogService;
    PartnerService partnerService;
    ReceiptRepository receiptRepository;
    BranchBankRepository branchBankRepository;
    NumberingContext numberingContext;
    UsagePlanService usagePlanService;
    FacturaValidationService facturaValidationService;

    public CheckFacturaCodeDTO checkCode(CheckFacturaCodeDTO checkFacturaCodeDTO) {
        SupplyFacturaDTO supplyFacturaDTO = getWithProductType(checkFacturaCodeDTO.getId());
        if (supplyFacturaDTO.getFacturaDTO() != null) {
            if (supplyFacturaDTO.getFacturaDTO().getProductList() != null) {
                for (FacturaProductDTO facturaProduct : supplyFacturaDTO.getFacturaDTO().getProductList().getProducts()) {

                    String code = MarkUtils.clearOriginalCode(checkFacturaCodeDTO.getCode(), facturaProduct.getProductType());
                    String printCode = code;
                    if (printCode.startsWith("00") && printCode.length() == 20) {
                        printCode = printCode.substring("00".length());
                    }

                    if (
                            facturaProduct.getMarks() != null && !CollectionUtils.isEmpty(facturaProduct.getMarks().getKiz()) &&
                                    (facturaProduct.getMarks().getKiz().contains(code) || facturaProduct.getMarks().getKiz().contains(checkFacturaCodeDTO.getCode()) || facturaProduct.getMarks().getKiz().contains(printCode))
                    ) {
                        return checkFacturaCodeDTO;
                    }
                    if (
                            facturaProduct.getMarks() != null && !CollectionUtils.isEmpty(facturaProduct.getMarks().getNomUpak()) &&
                                    (facturaProduct.getMarks().getNomUpak().contains(code) || facturaProduct.getMarks().getNomUpak().contains(checkFacturaCodeDTO.getCode()) || facturaProduct.getMarks().getNomUpak().contains(printCode))
                    ) {
                        return checkFacturaCodeDTO;
                    }
                    if (
                            facturaProduct.getMarks() != null && !CollectionUtils.isEmpty(facturaProduct.getMarks().getIdentTransUpak()) &&
                                    (
                                            facturaProduct.getMarks().getIdentTransUpak().contains(code) ||
                                                    facturaProduct.getMarks().getIdentTransUpak().contains(checkFacturaCodeDTO.getCode()) || facturaProduct.getMarks().getIdentTransUpak().contains(printCode)
                                    )
                    ) {
                        return checkFacturaCodeDTO;
                    }
                }
            }
        }
        checkFacturaCodeDTO.setErrorMessage("Код не найден");
        return checkFacturaCodeDTO;
    }

    public List<CisInfoResponse> check(@Valid BatchValidationDTO validationDTO) {
        Map<String, CisInfoResponse> resultMap = new LinkedHashMap<>();
        List<String> aggregationCodes = validationDTO.getAggregationCodes();
        List<String> markCodes = validationDTO.getMarkCodes();
        List<String> aggregationBlockCodes = validationDTO.getAggregationBlockCodes();

        if (!CollectionUtils.isEmpty(validationDTO.getAggregationCodes())) {
            validationDTO
                    .getAggregationCodes()
                    .forEach(aggregationCode -> {
                        CisInfo cisInfo = new CisInfo();
                        cisInfo.setRequestedCis(MarkUtils.clearAggregationCode(aggregationCode));
                        cisInfo.setOriginalCode(aggregationCode);
                        resultMap.put(aggregationCode, new CisInfoResponse(cisInfo));
                    });

            validationDTO
                    .getAggregationCodes()
                    .forEach(originalCode -> {
                        List<FacturaProduct> facturaProducts = facturaProductRepository.findAllByCodeAndCodeTypeAndProductTypeStatusNot(
                                originalCode,
                                CodeType.AGGREGATION,
                                Status.CANCELLED,
                                validationDTO.getProductType()
                        );
                        if (!CollectionUtils.isEmpty(facturaProducts)) {
                            facturaProducts.forEach(facturaProduct -> {
                                CisInfoResponse cisInfoResponse = wrap(facturaProduct, originalCode);
                                resultMap.remove(originalCode);
                                resultMap.put(originalCode, cisInfoResponse);
                                aggregationCodes.remove(originalCode);
                            });
                        }
                    });
        }

        if (!CollectionUtils.isEmpty(validationDTO.getMarkCodes())) {
            validationDTO
                    .getMarkCodes()
                    .forEach(originalCode -> {
                        CisInfo cisInfo = new CisInfo();
                        cisInfo.setRequestedCis(MarkUtils.clearOriginalCode(originalCode, validationDTO.getProductType()));
                        cisInfo.setOriginalCode(originalCode);
                        resultMap.put(originalCode, new CisInfoResponse(cisInfo));
                    });

            validationDTO
                    .getMarkCodes()
                    .forEach(originalCode -> {
                        List<FacturaProduct> facturaProducts = facturaProductRepository.findAllByCodeAndCodeTypeAndProductTypeStatusNot(
                                originalCode,
                                CodeType.MARK,
                                Status.CANCELLED,
                                validationDTO.getProductType()
                        );
                        if (!CollectionUtils.isEmpty(facturaProducts)) {
                            facturaProducts.forEach(facturaProduct -> {
                                CisInfoResponse cisInfoResponse = wrap(facturaProduct, originalCode);
                                resultMap.remove(originalCode);
                                resultMap.put(originalCode, cisInfoResponse);
                                markCodes.remove(originalCode);
                            });
                        }
                    });
        }

        if (!CollectionUtils.isEmpty(validationDTO.getAggregationBlockCodes())) {
            validationDTO
                    .getAggregationBlockCodes()
                    .forEach(originalCode -> {
                        CisInfo cisInfo = new CisInfo();
                        cisInfo.setRequestedCis(MarkUtils.clearOriginalCode(originalCode, validationDTO.getProductType()));
                        cisInfo.setOriginalCode(originalCode);
                        resultMap.put(originalCode, new CisInfoResponse(cisInfo));
                    });
            validationDTO
                    .getAggregationBlockCodes()
                    .forEach(originalCode -> {
                        List<FacturaProduct> facturaProducts = facturaProductRepository.findAllByCodeAndCodeTypeAndProductTypeStatusNot(
                                originalCode,
                                CodeType.BLOCK,
                                Status.CANCELLED,
                                validationDTO.getProductType()
                        );
                        if (!CollectionUtils.isEmpty(facturaProducts)) {
                            facturaProducts.forEach(facturaProduct -> {
                                CisInfoResponse cisInfoResponse = wrap(facturaProduct, originalCode);
                                resultMap.remove(originalCode);
                                resultMap.put(originalCode, cisInfoResponse);
                                aggregationBlockCodes.remove(originalCode);
                            });
                        }
                    });
        }

        if (
                !CollectionUtils.isEmpty(aggregationCodes) ||
                        !CollectionUtils.isEmpty(markCodes) ||
                        !CollectionUtils.isEmpty(aggregationBlockCodes)
        ) {
            CompanySettings xtraceSettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.XTRACE_ENABLED).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanySettings.class.getSimpleName(), "companyId", getCurrentCompanyId()));
            String token = Optional
                    .ofNullable(validationDTO.getToken())
                    .orElse(xtraceSettings.isEnabled() ? getXTraceInfo().getApiKey() : validateAslBelgiAndGetAggregationInfo().getAslBelgiToken());

            if (xtraceSettings.isEnabled()) {
                try {
                    CisInfoResponseResult cisInfoResponseResult = aslBelgiClient.checkPrivateInfo(
                            token,
                            aggregationCodes,
                            markCodes,
                            aggregationBlockCodes,
                            validationDTO.getProductType()
                    );
                    if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
                        for (CisInfoResponse cisInfoResponse : cisInfoResponseResult.getCisInfoResponses()) {
                            resultMap.remove(cisInfoResponse.getCisInfo().getOriginalCode());
                            resultMap.put(cisInfoResponse.getCisInfo().getOriginalCode(), cisInfoResponse);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                try {
                    CisInfoResponseResult cisInfoResponseResult = aslBelgiClient.checkCisesInfo(
                            token,
                            aggregationCodes,
                            markCodes,
                            aggregationBlockCodes,
                            validationDTO.getProductType()
                    );
                    if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
                        for (CisInfoResponse cisInfoResponse : cisInfoResponseResult.getCisInfoResponses()) {
                            String originalCode = cisInfoResponse.getCisInfo().getOriginalCode();
                            resultMap.remove(originalCode);
                            resultMap.put(originalCode, cisInfoResponse);

                            if (!CollectionUtils.isEmpty(aggregationCodes)) {
                                aggregationCodes.remove(originalCode);
                            }
                            if (!CollectionUtils.isEmpty(markCodes)) {
                                markCodes.remove(originalCode);
                            }
                            if (!CollectionUtils.isEmpty(aggregationBlockCodes)) {
                                aggregationBlockCodes.remove(originalCode);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                if (
                        !CollectionUtils.isEmpty(aggregationCodes) ||
                                !CollectionUtils.isEmpty(markCodes) ||
                                !CollectionUtils.isEmpty(aggregationBlockCodes)
                ) {
                    try {
                        CisInfoResponseResult cisInfoResponseResult = aslBelgiClient.checkAggregatedPublicList(
                                token,
                                aggregationCodes,
                                markCodes,
                                aggregationBlockCodes,
                                validationDTO.getProductType()
                        );
                        if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
                            for (CisInfoResponse cisInfoResponse : cisInfoResponseResult.getCisInfoResponses()) {
                                resultMap.remove(cisInfoResponse.getCisInfo().getOriginalCode());
                                resultMap.put(cisInfoResponse.getCisInfo().getOriginalCode(), cisInfoResponse);
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
        return Arrays.asList(resultMap.values().toArray(new CisInfoResponse[0]));
    }

    private CisInfoResponse wrap(FacturaProduct facturaProduct, String code) {
        CisInfo cisInfo = new CisInfo();
        cisInfo.setRequestedCis(code);
        cisInfo.setOriginalCode(code);
        cisInfo.setStatus(TuronMarkStatus.EMITTED);
        cisInfo.setErrorMessage("Код уже использован");

        cisInfo.setEntityId(facturaProduct.getFacturaDetail().getFactura().getId());
        cisInfo.setEntityType(EntityTypeEnum.FACTURA.name());
        cisInfo.setEntityNumber(facturaProduct.getFacturaDetail().getFactura().getFacturaNo());

        cisInfo.setGtin(facturaProduct.getBarcode());
        cisInfo.setProductName(facturaProduct.getName());

        return new CisInfoResponse(cisInfo);
    }

    public Page<SupplyFacturaListDTO> getList(FacturaFilter filter) {
        if (filter.isIncoming()) {
            filter.setBuyerId(Optional.ofNullable(getCurrentCompanyId()).orElse(filter.getCompanyId()));
        } else {
            filter.setSellerId(Optional.ofNullable(getCurrentCompanyId()).orElse(filter.getCompanyId()));

        }
        boolean isAdmin = SecurityUtils.isFromAdmin();
        filter.setAdmin(isAdmin);
        if (!isAdmin && filter.isIncoming()) {
            filter.setExcludeStatus(Status.DRAFT.name());
        }
        ResultList<Factura> resultList = facturaRepository.getResultList(filter);
        List<SupplyFacturaListDTO> result = resultList
                .getList()
                .stream()
                .map(factura -> {
                    SupplyFacturaListDTO supplyFacturaListDTO = new SupplyFacturaListDTO();
                    supplyFacturaListDTO.setId(factura.getId());
                    supplyFacturaListDTO.setFacturaId(factura.getFacturaId());
                    supplyFacturaListDTO.setFacturaNo(factura.getFacturaNo());
                    supplyFacturaListDTO.setFacturaDate(factura.getFacturaDate());
                    supplyFacturaListDTO.setStatus(factura.getStatus());
                    supplyFacturaListDTO.setStatusName(factura.getStatus().getName());
                    supplyFacturaListDTO.setTotal(factura.getTotal());
                    supplyFacturaListDTO.setFacturaType(FacturaType.buildById(factura.getFacturaType()));
                    supplyFacturaListDTO.setContractNo(factura.getContractNo());
                    supplyFacturaListDTO.setContractDate(factura.getContractDate());
                    supplyFacturaListDTO.setSendDate(factura.getSendDate());
                    if (factura.getBranch() != null) {
                        supplyFacturaListDTO.setBranch(factura.getBranch().toCommonDTO());
                    }
                    if (isAdmin) {
                        if (factura.getBuyer() != null) {
                            supplyFacturaListDTO.setBuyer(factura.getBuyer().toCommonBaseDTO());
                        }
                        if (factura.getSeller() != null) {
                            supplyFacturaListDTO.setSeller(factura.getSeller().toCommonBaseDTO());
                        }
                    } else {
                        if (filter.isIncoming()) {
                            supplyFacturaListDTO.setSeller(factura.getSeller().toCommonBaseDTO());
                        } else {
                            supplyFacturaListDTO.setBuyer(factura.getBuyer().toCommonBaseDTO());
                        }
                    }
                    if (filter.isIncoming()) {
                        if (factura.getStockIn() != null) {
                            supplyFacturaListDTO.setStock(factura.getStockIn().toStockCommonDTO());
                            supplyFacturaListDTO.setStockStatus(factura.getStockIn().getStatus());
                        } else if (factura.getStock() != null && TransferType.IN == factura.getStock().getTransferType()) {
                            supplyFacturaListDTO.setStock(factura.getStock().toStockCommonDTO());
                            supplyFacturaListDTO.setStockStatus(factura.getStock().getStatus());
                        }
                    } else {
                        if (factura.getStock() != null) {
                            supplyFacturaListDTO.setStock(factura.getStock().toStockCommonDTO());
                            supplyFacturaListDTO.setStockStatus(factura.getStock().getStatus());
                        }
                    }
                    return supplyFacturaListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<Long> getDraftIds() {
        return facturaRepository.findAllIdsByStatusAndSellerId(Status.DRAFT, getCurrentCompanyId());
    }

    @Transactional
    public void syncById(Long id, boolean isIncoming) {
        facturaRepository.findById(id).filter(factura -> factura.getFacturaId() != null).ifPresent(factura -> this.syncExisting(factura, isIncoming));
    }

    private void syncNew(String facturaId, String sellerTin, String buyerTin, Status status, boolean isIncoming) {
        log.info("Sync base. Buyer TIN: {}", buyerTin);
        log.info("Sync base. Seller TIN: {}", sellerTin);
        String sign = facturaRestClient.getSign(facturaId, isIncoming ? buyerTin : sellerTin, status, isIncoming);
        log.info("Sync base. Sign: {}", sign);
        if (StringUtils.hasText(sign)) {
            if (Status.PENDING.equals(status)) {
                facturaWebhookService.onReceive(sign);
            } else {
                facturaWebhookService.onStatusChange(sign, status);
            }
        }
    }

    private void syncExisting(Factura factura, boolean isIncoming) {
        log.info("Sync. Buyer TIN: {}", factura.getBuyer().getTin());
        log.info("Sync. Seller TIN: {}", factura.getSeller().getTin());
        FacturaShortDTO facturaDTO = facturaRestClient.getFactura(factura.getFacturaId(), isIncoming ? factura.getBuyer().getTin() : factura.getSeller().getTin(), isIncoming);
        if (facturaDTO != null && !factura.getStatus().equals(facturaDTO.getState())) {
            if (facturaDTO.getState() != Status.INVALID) {
                String sign = facturaRestClient.getSign(factura.getFacturaId(), isIncoming ? facturaDTO.getBuyerTin() : facturaDTO.getSellerTin(), facturaDTO.getState(), isIncoming);
                if (StringUtils.hasText(factura.getSign())) {
                    factura.setSign(sign);
                }
                facturaWebhookService.updateSignatureData(sign, factura);
            }
            factura.setStatus(facturaDTO.getState());
            facturaRepository.save(factura);
        }
    }

    @Transactional
    public void sync(FacturaSyncDTO facturaSyncDTO) {
        boolean isIncoming = InvoiceType.INCOMING.equals(facturaSyncDTO.getInvoiceType());
        log.info("Sync. FacturaId: {}", facturaSyncDTO.getFacturaId());
        if (StringUtils.hasText(facturaSyncDTO.getFacturaId())) {
            Factura factura = facturaRepository.findFirstByFacturaIdAndDeletedIsFalse(facturaSyncDTO.getFacturaId()).orElse(null);
            if (factura != null) {
                this.syncExisting(factura, isIncoming);
            } else {
                FacturaShortDTO facturaDTO = facturaRestClient.getFactura(facturaSyncDTO.getFacturaId(), getCurrentCompanyTin(), isIncoming);
                if (facturaDTO != null) {
                    this.syncNew(facturaSyncDTO.getFacturaId(), facturaDTO.getSellerTin(), facturaDTO.getBuyerTin(), facturaDTO.getState(), isIncoming);
                }
            }
        } else {
            List<Long> facturaIds = InvoiceType.INCOMING.equals(facturaSyncDTO.getInvoiceType()) ?
                    facturaRepository.findAllIdsByStatusAndBuyerId(Status.PENDING, getCurrentCompanyId()) :
                    facturaRepository.findAllIdsByStatusAndSellerId(Status.PENDING, getCurrentCompanyId());
            facturaIds.forEach(id -> rabbitMqFacturaProducer.syncQueue(new SyncFacturaByIdPayload(id, InvoiceType.INCOMING.equals(facturaSyncDTO.getInvoiceType()))));
        }
    }

    public void syncQueue(SyncFacturaByIdPayload payload) {
        syncById(payload.getId(), payload.isIncoming());
    }

    @Transactional
    public Long convertFromStock(Stock stock, Long oldFacturaId, Integer facturaType, Long companyId) {
        if (!(PaymentType.TRANSFER == stock.getPaymentType()) && receiptRepository.existsByStockOutIdAndDeletedIsFalse(stock.getId())) {
            throw validationService.badRequestExceptionThrow("Вы не можете Выписать счет-фактуру, поскольку уже имеется чек.").get();
        }
        if (!stock.isHasDiscount() && stock.getProducts().stream()
                .anyMatch(sp -> sp.getDiscountId() != null)) {
            throw validationService.badRequestExceptionThrow("Factura can not be created with discount").get();
        }
        Company company;
        Company seller;
        Company buyer;
        if (companyId == null) {
            company = getCurrentCompany();
            seller = stock.getSupplier();
            buyer = stock.getCustomer();

        } else {
            company = companyRepository.findById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
            seller = companyRepository.findById(stock.getSupplierId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", stock.getSupplierId()));
            buyer = companyRepository.findById(stock.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", stock.getCustomerId()));
        }
        Factura oldFactura = null;
        if (TransferType.RETURN.equals(stock.getTransferType())) {
            if (oldFacturaId == null) {
                oldFacturaId = stock.getFacturaId();
            }
            if (oldFacturaId == null) {
                throw validationService.notFoundExceptionThrow("Нужно выбрать предыдущую фактуру").get();
            }
            oldFactura = facturaRepository.findById(oldFacturaId).orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", oldFacturaId));
        }
        if (!StringUtils.hasText(seller.getTin())) {
            throw validationService.notFoundExceptionThrow(
                    localizationService.localize(
                            "entity.not.found.with",
                            new Object[]{localizationService.localize(MessageConstants.SUPPLIER), localizationService.localize(MessageConstants.INN)}
                    )).get();
        }
        if (!StringUtils.hasText(buyer.getTin())) {
            throw validationService.notFoundExceptionThrow(localizationService.localize(
                    "entity.not.found.with",
                    new Object[]{localizationService.localize(MessageConstants.CUSTOMER), localizationService.localize(MessageConstants.INN)}
            )).get();
        }
        if (seller.getCompanyBank() == null) {
            throw validationService.notFoundExceptionThrow(localizationService.localize(MessageConstants.ERROR), "seller.bank.not.found").get();
        }
        if (buyer.getCompanyBank() == null) {
            throw validationService.notFoundExceptionThrow(localizationService.localize(MessageConstants.ERROR), "buyer.bank.not.found").get();
        }
        enrichCompaniesFromSoliq(seller, buyer);

         List<StockProduct> stockProducts = stock.getProducts().stream().filter(stockProduct -> stockProduct.getDiscountId() == null).toList();

        Factura factura = new Factura();

        if (stock.getOrder() != null && (stock.getOrder().isFrom1C() || stock.getOrder().isFromHippo())) {
            factura.setFacturaNo(stock.getOrder().getOrderNumber());
        } else {
            Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.INVOICE, company.getId());
            factura.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
            factura.setFacturaNo(numberingMap.get(NUMBER));
        }

        factura.setVersion(1);
        if (facturaType != null) {
            factura.setFacturaType(facturaType);
        } else {
            factura.setFacturaType(TransferType.RETURN.equals(stock.getTransferType()) ? FacturaType.ADDITIONAL.getId() : FacturaType.STANDART.getId());
        }
        factura.setBranchId(stock.getBranchId());
        factura.setSingleSidedType(0);
        factura.setFacturaDate(LocalDate.now());
        factura.setBuyerId(buyer.getId());
        factura.setSellerId(seller.getId());
        factura.setCompanyId(company.getId());
        factura.setStockId(stock.getId());
        factura.setTotal(stock.getTotalPrice().subtract(Optional.ofNullable(stock.getTotalDiscountPrice()).orElse(BigDecimal.ZERO)));
        factura.setAppType(stock.getAppType());
        if (oldFactura != null) {
            factura.setContractNo(oldFactura.getContractNo());
            factura.setContractDate(oldFactura.getContractDate());
            if (!StringUtils.hasText(oldFactura.getContractNo()) && oldFactura.getContractDate() == null && oldFactura.getFacturaDetail() != null) {
                factura.setContractNo(oldFactura.getFacturaDetail().getContractNo());
                factura.setContractDate(oldFactura.getFacturaDetail().getContractDate());
            }
        } else if (stock.getContractId() != null) {
            factura.setContractNo(stock.getContract().getContractNumber());
            factura.setContractDate(stock.getContract().getContractDate());
        }
        factura = facturaRepository.save(factura);

        stock.setFacturaId(factura.getId());
        stock.getFacturas().add(factura);
        stockOutRepository.save(stock);

        FacturaDetail facturaDetail = new FacturaDetail();
        facturaDetail.setTin(seller.getTin());

        if (oldFactura != null) {
            facturaDetail.setOldFacturaId(oldFactura.getFacturaId());
            facturaDetail.setOldFacturaNo(oldFactura.getFacturaNo());
            facturaDetail.setOldFacturaDate(oldFactura.getFacturaDate());
        }
        facturaDetail.setFactura(factura);
        facturaDetail = facturaDetailRepository.save(facturaDetail);
        factura.getFacturaDetails().clear();
        factura.getFacturaDetails().add(facturaDetail);

        facturaDetail.setVersion(1);

        //Seller
        setSellerDetails(stock, seller, facturaDetail);

        //Buyer
        facturaDetail.setBuyerAccount(buyer.getCompanyBank().getAccountNumber());
        facturaDetail.setBuyerBankId(buyer.getCompanyBank().getBank().getMfo());
        facturaDetail.setBuyerBankName(buyer.getCompanyBank().getBank().getName());
        if (buyer.getAddress() != null) {
            facturaDetail.setBuyerAddress(buyer.getAddress().getFullAddress());
        }
        if (buyer.getOwner() != null) {
            facturaDetail.setBuyerMobile(buyer.getOwner().getPhone());
            facturaDetail.setBuyerWorkPhone(buyer.getOwner().getPhone());
        }
        facturaDetail.setBuyerOked(buyer.getCompanyBank().getOked());
        facturaDetail.setBuyerDirector(buyer.getDirector());
        facturaDetail.setBuyerAccountant(buyer.getAccountant());
        facturaDetail.setBuyerVatRegCode(null);
        facturaDetail.setBuyerVatRegStatus(null);
        if (stock.getDeliveryBranch() != null && StringUtils.hasText(stock.getDeliveryBranch().getCustomerBranchNumber())) {
            facturaDetail.setDeliveryBranchId(stock.getDeliveryBranchId());
            facturaDetail.setBuyerBranchCode(stock.getDeliveryBranch().getCustomerBranchNumber());
            facturaDetail.setBuyerBranchName(stock.getDeliveryBranch().getName());
        }

        // set investment
        setInvestment(facturaDetail, buyer);

        facturaDetail.setNicFacturaId(factura.getFacturaId());
        facturaDetail.setFacturaNo(factura.getFacturaNo());
        facturaDetail.setFacturaDate(LocalDate.now());

        facturaDetail.setHasCommittent(Boolean.FALSE);
        facturaDetail.setHasLgota(Boolean.FALSE);
        facturaDetail.setHasMedical(stockProducts.stream()
                .anyMatch(sp -> ProductType.Pharma == sp.getProduct().getProductType()));
        facturaDetail.setHasVat(Boolean.FALSE);
        facturaDetail.setHasExcise(Boolean.FALSE);
        facturaDetail.setHasMarking(Boolean.FALSE);

        Unit unit = getUnit();
        int ordNo = 1;
        BigDecimal totalVat = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        int sOrder = 0;

        for (StockProduct stockProduct : stockProducts) {
            if (TransferType.RETURN.equals(stock.getTransferType())) {
                if (stockProduct.getPurchasePriceWithoutVat() == null || stockProduct.getTotalPurchasePriceWithoutVat() == null) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR),
                            localizationService.localize(MessageConstants.PURCHASE_PRICE_WITHOUT_VAT_IS_NULL)).get();
                }
            } else {
                if (stockProduct.getSellingPriceWithoutVat() == null || stockProduct.getTotalSellingPriceWithoutVat() == null) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR),
                            localizationService.localize(MessageConstants.SELLING_PRICE_WITHOUT_VAT_IS_NULL)).get();
                }
            }

            Product product = stockProduct.getProduct();
            FacturaProduct facturaProduct = new FacturaProduct();

            if (stockProduct.getCompanyCommittentId() != null) {
                facturaDetail.setHasCommittent(Boolean.TRUE);
                facturaProduct.setCommittentTin(stockProduct.getCompanyCommittent().getCommittent().getTin());
                facturaProduct.setCommittentName(stockProduct.getCompanyCommittent().getCommittent().getName());
                VATCompanyDateDTO vatCompanyDateDTO = this.getCompanyVatByDate(stockProduct.getCompanyCommittent().getCommittent());
                facturaProduct.setCommittentVatRegCode(vatCompanyDateDTO.getData().getVatRegCode());
                facturaProduct.setCommittentVatRegStatus(vatCompanyDateDTO.getData().getStatus());
            }

            facturaProduct.setProductId(product.getId());
            facturaProduct.setProduct(product);

            facturaProduct.setOrdNo(String.format("%d", ordNo++));
            facturaProduct.setCatalogCode(product.getCatalogCode());
            facturaProduct.setCatalogName(product.getCatalogName());
            facturaProduct.setBarcode(product.getBarcode());
            facturaProduct.setWarehouseId(stock.getWarehouseId());
            facturaProduct.setBranchId(stock.getBranchId());

            if (stockProduct.getProductContractId() != null) {
                facturaProduct.setName(stockProduct.getProductContract().getName());
            } else {
                facturaProduct.setName(product.getName());
            }
            facturaProduct.setMeasureId(unit.getId());
            if (StringUtils.hasText(stockProduct.getPackageCode())) {
                facturaProduct.setPackageCode(stockProduct.getPackageCode());
                facturaProduct.setPackageName(stockProduct.getPackageName());
                if (!stockProduct.getPackageCode().equals(facturaProduct.getProduct().getPackageCode())) {
                    Product globalProduct = facturaProduct.getProduct();
                    globalProduct.setPackageCode(stockProduct.getPackageCode());
                    globalProduct.setPackageName(stockProduct.getPackageName());
                    productRepository.save(globalProduct);
                }
            } else {
                facturaProduct.setPackageCode(product.getPackageCode());
                facturaProduct.setPackageName(product.getPackageName());
            }
            if (facturaDetail.getHasMedical() != null && facturaDetail.getHasMedical()) {
                facturaProduct.setBaseSumma(stockProduct.getBasePrice());
                BigDecimal profitRate = Optional.ofNullable(stockProduct.getFacturaProfitRate()).orElse(stockProduct.getMarkupPercent());
                facturaProduct.setProfitRate(Optional.ofNullable(profitRate).orElse(BigDecimal.ZERO));

                if (ProductType.Pharma == facturaProduct.getProduct().getProductType()) {
                    if (facturaProduct.getProduct().getDispenseType() == null) {
                        throw validationService.badRequestExceptionThrow("Этот тип товара " + facturaProduct.getProduct().getName() + " является медицинским. Поэтому необходимо выбрать один тип отпуска в каталоге для данного товара: «По рецепту» или «Без рецепта».").get();
                    }
                    facturaProduct.setDispenseType(product.getDispenseType().getId());
                }
            }
            facturaProduct.setQty(stockProduct.getQty());

            BigDecimal summa;
            BigDecimal deliverySum;
            BigDecimal deliverySumWithVat;

            if (TransferType.RETURN.equals(stock.getTransferType())) {
                summa = stockProduct.getPurchasePriceWithoutVat().negate();
                deliverySum = stockProduct.getTotalPurchasePriceWithoutVat().negate();
                deliverySumWithVat = stockProduct.getTotalPurchasePrice().negate();
            } else {
                summa = stockProduct.getSellingPriceWithoutVat();
                deliverySum = stockProduct.getTotalSellingPriceWithoutVat();
                deliverySumWithVat = stockProduct.getTotalSellingPrice();
            }
            facturaProduct.setSumma(summa);
            facturaProduct.setExciseRate(stockProduct.getExciseRate());
            facturaProduct.setExciseSum(stockProduct.getExciseSum());
            facturaProduct.setDeliverySum(deliverySum);

            if (facturaProduct.getExciseRate() != null && facturaProduct.getExciseRate().compareTo(BigDecimal.ZERO) > 0) {
                facturaDetail.setHasExcise(Boolean.TRUE);
            }
            if (product.getOrigin() != null) {
                facturaProduct.setOrigin(product.getOrigin().getId());
            }

            if (stockProduct.getVatRate() == null) {
                facturaProduct.setVatRate(BigDecimal.ZERO);
                facturaProduct.setVatSum(BigDecimal.ZERO);
                facturaProduct.setWithoutVat(Boolean.TRUE);
            } else if (stockProduct.getVatRate().compareTo(BigDecimal.ZERO) == 0) {
                facturaProduct.setVatRate(BigDecimal.ZERO);
                facturaProduct.setVatSum(BigDecimal.ZERO);
                facturaProduct.setWithoutVat(Boolean.FALSE);
            } else {
                facturaProduct.setVatRate(stockProduct.getVatRate());
                facturaProduct.setVatSum(stockProduct.getTotalVatPrice());
                facturaProduct.setWithoutVat(Boolean.FALSE);
                if (TransferType.RETURN.equals(stock.getTransferType())) {
                    totalVat = totalVat.add(stockProduct.getTotalVatPrice().negate());
                } else {
                    totalVat = totalVat.add(stockProduct.getTotalVatPrice());
                }
                facturaDetail.setHasVat(Boolean.TRUE);
            }
            if (TransferType.RETURN.equals(stock.getTransferType())) {
                totalPrice = totalPrice.add(Optional.ofNullable(stockProduct.getTotalPurchasePrice()).orElse(BigDecimal.ZERO));
            } else {
                totalPrice = totalPrice.add(Optional.ofNullable(stockProduct.getTotalSellingPrice()).orElse(BigDecimal.ZERO));
            }
            facturaProduct.setDeliverySumWithVat(deliverySumWithVat);
            if (product.getProductType() != null) {
                facturaProduct.setProductType(product.getProductType().getProductGroup().getMarkingCode());
            }
            if (stockProduct.getProductSerialId() != null) {
                facturaProduct.setSerial(stockProduct.getProductSerial().getSerialNumber());
                facturaProduct.setProductSerialId(stockProduct.getProductSerialId());
            }
            facturaProduct.setExpiryDate(stockProduct.getExpiryDate());
            facturaProduct.setCode1C(stockProduct.getCode1C());
            facturaProduct.setSOrder(++sOrder);

            Set<String> kiz = stockProduct.getStockProductMarks()
                    .stream()
                    .filter(StockProductMark::isParent)
                    .map(StockProductMark::getCode).collect(Collectors.toSet());

            if (!CollectionUtils.isEmpty(kiz)) {
                facturaProduct.setMarks(kiz);
            }

            Set<String> identTransUpaks = stockProduct
                    .getStockProductAggregations()
                    .stream().filter(StockProductAggregation::isParent)
                    .map(StockProductAggregation::getOriginalCode).collect(Collectors.toSet());

            if (!CollectionUtils.isEmpty(identTransUpaks)) {
                facturaProduct.setAggregations(identTransUpaks);
            }

            Set<String> nomUpaks = stockProduct.getStockProductAggregationBlocks()
                    .stream()
                    .filter(StockProductAggregationBlock::isParent)
                    .map(StockProductAggregationBlock::getCode)
                    .collect(Collectors.toSet());

            if (!CollectionUtils.isEmpty(nomUpaks)) {
                facturaProduct.setAggregationBlocks(nomUpaks);
            }

            facturaDetail.setHasMarking(
                    !CollectionUtils.isEmpty(kiz) || !CollectionUtils.isEmpty(identTransUpaks) || !CollectionUtils.isEmpty(nomUpaks)
            );

            facturaDetail.addProduct(facturaProduct);
        }

        facturaDetailRepository.save(facturaDetail);
        facturaRepository.save(factura);


        return factura.getId();
    }

    private void convertDiscountFactura(Stock stock, Long companyId) {
        Company company;
        Company seller;
        Company buyer;
        if (companyId == null) {
            company = getCurrentCompany();
            seller = stock.getSupplier();
            buyer = stock.getCustomer();

        } else {
            company = companyRepository.findById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
            seller = companyRepository.findById(stock.getSupplierId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", stock.getSupplierId()));
            buyer = companyRepository.findById(stock.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", stock.getCustomerId()));
        }

        if (!StringUtils.hasText(seller.getTin())) {
            throw validationService.notFoundExceptionThrow(
                    localizationService.localize(
                            "entity.not.found.with",
                            new Object[]{localizationService.localize(MessageConstants.SUPPLIER), localizationService.localize(MessageConstants.INN)}
                    )).get();
        }
        if (!StringUtils.hasText(buyer.getTin())) {
            throw validationService.notFoundExceptionThrow(localizationService.localize(
                    "entity.not.found.with",
                    new Object[]{localizationService.localize(MessageConstants.CUSTOMER), localizationService.localize(MessageConstants.INN)}
            )).get();
        }
        if (seller.getCompanyBank() == null) {
            throw validationService.notFoundExceptionThrow(localizationService.localize(MessageConstants.ERROR), "seller.bank.not.found").get();
        }
        if (buyer.getCompanyBank() == null) {
            throw validationService.notFoundExceptionThrow(localizationService.localize(MessageConstants.ERROR), "buyer.bank.not.found").get();
        }
        enrichCompaniesFromSoliq(seller, buyer);

        List<StockProduct> stockProducts = stock.getProducts().stream().filter(stockProduct -> stockProduct.getDiscountId() != null).toList();

        Factura factura = new Factura();

        if (stock.getOrder() != null && (stock.getOrder().isFrom1C() || stock.getOrder().isFromHippo())) {
            factura.setFacturaNo(stock.getOrder().getOrderNumber());
        } else {
            Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.INVOICE, company.getId());
            factura.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
            factura.setFacturaNo(numberingMap.get(NUMBER));
        }

        factura.setVersion(1);
        factura.setBranchId(stock.getBranchId());
        factura.setSingleSidedType(0);
        factura.setFacturaDate(LocalDate.now());
        factura.setBuyerId(buyer.getId());
        factura.setSellerId(seller.getId());
        factura.setCompanyId(company.getId());
        factura.setStockId(stock.getId());
        factura.setAppType(stock.getAppType());
        factura.setFacturaType(FacturaType.WITHOUT_PAYMENT.getId());
        if (stock.getContractId() != null) {
            factura.setContractNo(stock.getContract().getContractNumber());
            factura.setContractDate(stock.getContract().getContractDate());
        }
        factura = facturaRepository.save(factura);

        stock.getFacturas().add(factura);
        stockOutRepository.save(stock);

        FacturaDetail facturaDetail = new FacturaDetail();
        facturaDetail.setTin(seller.getTin());

        facturaDetail.setFactura(factura);
        facturaDetail = facturaDetailRepository.save(facturaDetail);
        factura.getFacturaDetails().clear();
        factura.getFacturaDetails().add(facturaDetail);

        facturaDetail.setVersion(1);

        //Seller
        setSellerDetails(stock, seller, facturaDetail);

        //Buyer
        facturaDetail.setBuyerAccount(buyer.getCompanyBank().getAccountNumber());
        facturaDetail.setBuyerBankId(buyer.getCompanyBank().getBank().getMfo());
        facturaDetail.setBuyerBankName(buyer.getCompanyBank().getBank().getName());
        if (buyer.getAddress() != null) {
            facturaDetail.setBuyerAddress(buyer.getAddress().getFullAddress());
        }
        if (buyer.getOwner() != null) {
            facturaDetail.setBuyerMobile(buyer.getOwner().getPhone());
            facturaDetail.setBuyerWorkPhone(buyer.getOwner().getPhone());
        }
        facturaDetail.setBuyerOked(buyer.getCompanyBank().getOked());
        facturaDetail.setBuyerDirector(buyer.getDirector());
        facturaDetail.setBuyerAccountant(buyer.getAccountant());
        facturaDetail.setBuyerVatRegCode(null);
        facturaDetail.setBuyerVatRegStatus(null);
        if (stock.getDeliveryBranch() != null && StringUtils.hasText(stock.getDeliveryBranch().getCustomerBranchNumber())) {
            facturaDetail.setDeliveryBranchId(stock.getDeliveryBranchId());
            facturaDetail.setBuyerBranchCode(stock.getDeliveryBranch().getCustomerBranchNumber());
            facturaDetail.setBuyerBranchName(stock.getDeliveryBranch().getName());
        }

        // set investment
        setInvestment(facturaDetail, buyer);

        facturaDetail.setNicFacturaId(factura.getFacturaId());
        facturaDetail.setFacturaNo(factura.getFacturaNo());
        facturaDetail.setFacturaDate(LocalDate.now());

        facturaDetail.setHasCommittent(Boolean.FALSE);
        facturaDetail.setHasLgota(Boolean.FALSE);
        facturaDetail.setHasMedical(stockProducts.stream()
                .anyMatch(sp -> ProductType.Pharma == sp.getProduct().getProductType()));
        facturaDetail.setHasVat(Boolean.FALSE);
        facturaDetail.setHasExcise(Boolean.FALSE);
        facturaDetail.setHasMarking(Boolean.FALSE);

        Unit unit = getUnit();
        int ordNo = 1;
        BigDecimal totalVat = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        int sOrder = 0;
        for (StockProduct stockProduct : stockProducts) {
            if (TransferType.RETURN.equals(stock.getTransferType())) {
                if (stockProduct.getPurchasePriceWithoutVat() == null || stockProduct.getTotalPurchasePriceWithoutVat() == null) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR),
                            localizationService.localize(MessageConstants.PURCHASE_PRICE_WITHOUT_VAT_IS_NULL)).get();
                }
            } else {
                if (stockProduct.getSellingPriceWithoutVat() == null || stockProduct.getTotalSellingPriceWithoutVat() == null) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR),
                            localizationService.localize(MessageConstants.SELLING_PRICE_WITHOUT_VAT_IS_NULL)).get();
                }
            }

            Product product = stockProduct.getProduct();
            FacturaProduct facturaProduct = new FacturaProduct();

            if (stockProduct.getCompanyCommittentId() != null) {
                facturaDetail.setHasCommittent(Boolean.TRUE);
                facturaProduct.setCommittentTin(stockProduct.getCompanyCommittent().getCommittent().getTin());
                facturaProduct.setCommittentName(stockProduct.getCompanyCommittent().getCommittent().getName());
                VATCompanyDateDTO vatCompanyDateDTO = this.getCompanyVatByDate(stockProduct.getCompanyCommittent().getCommittent());
                facturaProduct.setCommittentVatRegCode(vatCompanyDateDTO.getData().getVatRegCode());
                facturaProduct.setCommittentVatRegStatus(vatCompanyDateDTO.getData().getStatus());
            }

            facturaProduct.setProductId(product.getId());
            facturaProduct.setProduct(product);

            facturaProduct.setOrdNo(String.format("%d", ordNo++));
            facturaProduct.setCatalogCode(product.getCatalogCode());
            facturaProduct.setCatalogName(product.getCatalogName());
            facturaProduct.setBarcode(product.getBarcode());
            facturaProduct.setWarehouseId(stock.getWarehouseId());
            facturaProduct.setBranchId(stock.getBranchId());

            if (stockProduct.getProductContractId() != null) {
                facturaProduct.setName(stockProduct.getProductContract().getName());
            } else {
                facturaProduct.setName(product.getName());
            }
            facturaProduct.setMeasureId(unit.getId());
            if (StringUtils.hasText(stockProduct.getPackageCode())) {
                facturaProduct.setPackageCode(stockProduct.getPackageCode());
                facturaProduct.setPackageName(stockProduct.getPackageName());
                if (!stockProduct.getPackageCode().equals(facturaProduct.getProduct().getPackageCode())) {
                    Product globalProduct = facturaProduct.getProduct();
                    globalProduct.setPackageCode(stockProduct.getPackageCode());
                    globalProduct.setPackageName(stockProduct.getPackageName());
                    productRepository.save(globalProduct);
                }
            } else {
                facturaProduct.setPackageCode(product.getPackageCode());
                facturaProduct.setPackageName(product.getPackageName());
            }
            if (facturaDetail.getHasMedical() != null && facturaDetail.getHasMedical()) {
                facturaProduct.setBaseSumma(stockProduct.getBasePrice());
                BigDecimal profitRate = Optional.ofNullable(stockProduct.getFacturaProfitRate()).orElse(stockProduct.getMarkupPercent());
                facturaProduct.setProfitRate(Optional.ofNullable(profitRate).orElse(BigDecimal.ZERO));

                if (ProductType.Pharma == facturaProduct.getProduct().getProductType()) {
                    if (facturaProduct.getProduct().getDispenseType() != null) {
                        facturaProduct.setDispenseType(product.getDispenseType().getId());
                    } else {
                        throw validationService.badRequestExceptionThrow("Этот тип товара " + facturaProduct.getProduct().getName() + " является медицинским. Поэтому необходимо выбрать один тип отпуска в каталоге для данного товара: «По рецепту» или «Без рецепта».").get();
                    }
                }
            }
            facturaProduct.setQty(stockProduct.getQty());

            BigDecimal summa;
            BigDecimal deliverySum;
            BigDecimal deliverySumWithVat;

            summa = stockProduct.getSellingPriceWithoutVat();
            deliverySum = stockProduct.getTotalSellingPriceWithoutVat();
            deliverySumWithVat = stockProduct.getTotalSellingPrice();

            facturaProduct.setSumma(summa);
            facturaProduct.setExciseRate(BigDecimal.ZERO);
            facturaProduct.setExciseSum(BigDecimal.ZERO);
            facturaProduct.setDeliverySum(deliverySum);
            if (product.getOrigin() != null) {
                facturaProduct.setOrigin(product.getOrigin().getId());
            }

            if (stockProduct.getVatRate() == null) {
                facturaProduct.setVatRate(BigDecimal.ZERO);
                facturaProduct.setVatSum(BigDecimal.ZERO);
                facturaProduct.setWithoutVat(Boolean.TRUE);
            } else if (stockProduct.getVatRate().compareTo(BigDecimal.ZERO) == 0) {
                facturaProduct.setVatRate(BigDecimal.ZERO);
                facturaProduct.setVatSum(BigDecimal.ZERO);
                facturaProduct.setWithoutVat(Boolean.FALSE);
            } else {
                facturaProduct.setVatRate(stockProduct.getVatRate());
                facturaProduct.setVatSum(stockProduct.getTotalVatPrice());
                facturaProduct.setWithoutVat(Boolean.FALSE);
                totalVat = totalVat.add(stockProduct.getTotalVatPrice());
                facturaDetail.setHasVat(Boolean.TRUE);
            }
            totalPrice = totalPrice.add(stockProduct.getTotalSellingPrice());
            facturaProduct.setDeliverySumWithVat(deliverySumWithVat);
            if (product.getProductType() != null) {
                facturaProduct.setProductType(product.getProductType().getProductGroup().getMarkingCode());
            }
            if (stockProduct.getProductSerialId() != null) {
                facturaProduct.setSerial(stockProduct.getProductSerial().getSerialNumber());
                facturaProduct.setProductSerialId(stockProduct.getProductSerialId());
            }
            facturaProduct.setExpiryDate(stockProduct.getExpiryDate());
            facturaProduct.setCode1C(stockProduct.getCode1C());
            facturaProduct.setSOrder(++sOrder);

            Set<String> kiz = stockProduct.getStockProductMarks()
                    .stream()
                    .filter(StockProductMark::isParent)
                    .map(StockProductMark::getCode).collect(Collectors.toSet());

            if (!CollectionUtils.isEmpty(kiz)) {
                facturaProduct.setMarks(kiz);
            }

            Set<String> identTransUpaks = stockProduct
                    .getStockProductAggregations()
                    .stream().filter(StockProductAggregation::isParent)
                    .map(StockProductAggregation::getOriginalCode).collect(Collectors.toSet());

            if (!CollectionUtils.isEmpty(identTransUpaks)) {
                facturaProduct.setAggregations(identTransUpaks);
            }

            Set<String> nomUpaks = stockProduct.getStockProductAggregationBlocks()
                    .stream()
                    .filter(StockProductAggregationBlock::isParent)
                    .map(StockProductAggregationBlock::getCode)
                    .collect(Collectors.toSet());

            if (!CollectionUtils.isEmpty(nomUpaks)) {
                facturaProduct.setAggregationBlocks(nomUpaks);
            }

            facturaDetail.setHasMarking(
                    !CollectionUtils.isEmpty(kiz) || !CollectionUtils.isEmpty(identTransUpaks) || !CollectionUtils.isEmpty(nomUpaks)
            );

            facturaDetail.addProduct(facturaProduct);
        }

        factura.setTotal(totalPrice);
        facturaDetailRepository.save(facturaDetail);
        facturaRepository.save(factura);
    }

    @Transactional
    public Long convertFromStockV2(Long stockId, Long oldFacturaId, Integer facturaType) {
        return this.convertFromStockV2(stockId, oldFacturaId, facturaType, null);
    }

    @Transactional
    public Long convertFromStockV2(Long stockId, Long oldFacturaId, Integer facturaType, Long companyId) {
        Stock stock = stockOutRepository.findById(stockId).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", stockId));
        Long facturaId = this.convertFromStock(stock, oldFacturaId, facturaType, companyId);
        if (stock.isHasDiscount()) {
            this.convertDiscountFactura(stock, companyId);
        }
        return facturaId;
    }

    @Transactional
    public SupplyFacturaUpdateDTO updateData(Long id) {
        boolean isFacturaChanged = false;
        Factura factura = facturaRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", id));
        FacturaDetail facturaDetail = factura.getFacturaDetail();
        if (!StringUtils.hasText(factura.getFacturaId())) {
            factura.setFacturaId(this.getGuid(GuidType.FACTURA));
            log.info("Factura update data facturaId:{}", factura.getFacturaId());
            isFacturaChanged = true;
        }
        if (!StringUtils.hasText(factura.getFacturaProductId())) {
            factura.setFacturaProductId(this.getGuid(GuidType.FACTURA_PRODUCT));
            log.info("Factura update data facturaProductId:{}", factura.getFacturaProductId());
            isFacturaChanged = true;
        }
        if (StringUtils.hasText(facturaDetail.getEmpowermentNo()) && !StringUtils.hasText(facturaDetail.getAgentFacturaId())) {
            facturaDetail.setAgentFacturaId(this.getGuid(GuidType.AGENT_FACTURA));
        }
        if (!StringUtils.hasText(facturaDetail.getSellerVatRegCode())) {
            VATCompanyDateDTO vatCompanyDateDTO = this.getCompanyVatByDate(factura.getSeller());
            if (vatCompanyDateDTO != null) {
                facturaDetail.setSellerVatRegStatus(vatCompanyDateDTO.getData().getStatus());
                facturaDetail.setSellerVatRegCode(vatCompanyDateDTO.getData().getVatRegCode());
                log.info("Factura update data sellerVatRegStatus :{}", facturaDetail.getSellerVatRegStatus());
                log.info("Factura update data sellerVatRegCode :{}", facturaDetail.getSellerVatRegCode());
            }

        }
        if (!StringUtils.hasText(facturaDetail.getBuyerVatRegCode())) {
            VATCompanyDateDTO vatCompanyDateDTO = this.getCompanyVatByDate(factura.getBuyer());
            if (vatCompanyDateDTO != null) {
                facturaDetail.setBuyerVatRegStatus(vatCompanyDateDTO.getData().getStatus());
                facturaDetail.setBuyerVatRegCode(vatCompanyDateDTO.getData().getVatRegCode());
                log.info("Factura update data buyerVatRegStatus :{}", facturaDetail.getBuyerVatRegStatus());
                log.info("Factura update data buyerVatRegCode :{}", facturaDetail.getBuyerVatRegCode());
            }
        }
        facturaDetailRepository.save(facturaDetail);
        if (isFacturaChanged) {
            facturaRepository.save(factura);
        }

        SupplyFacturaUpdateDTO result = new SupplyFacturaUpdateDTO();
        result.setFacturaId(factura.getFacturaId());
        result.setFacturaProductId(factura.getFacturaProductId());
        result.setSellerVatRegCode(facturaDetail.getSellerVatRegCode());
        result.setSellerVatRegStatus(facturaDetail.getSellerVatRegStatus());
        result.setBuyerVatRegCode(facturaDetail.getBuyerVatRegCode());
        result.setBuyerVatRegStatus(facturaDetail.getBuyerVatRegStatus());
        result.setAgentFacturaId(facturaDetail.getAgentFacturaId());
        return result;
    }

    @Transactional
    public Long create(FacturaDTO facturaDTO) {
        return this.saveFactura(null, facturaDTO).getId();
    }

    @Transactional
    public Long update(Long id, FacturaDTO facturaDTO) {
        Factura factura = facturaRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", id));
        if (
                !isFromAdmin() &&
                        !(
                                factura.getSellerId().equals(getCurrentCompanyId()) ||
                                        factura.getBuyerId().equals(getCurrentCompanyId())
                        )
        ) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ACCESS_DENIED), localizationService.localize(MessageConstants.CANNOT_UPDATE_OTHER_COMPANY_DATA)).get();
        }
        this.saveFactura(factura, facturaDTO);
        return factura.getId();
    }

    public FacturaProductListDTO getProducts(Long facturaId) {
        return facturaDetailRepository.findFirstByFactura_Id(facturaId)
                .map(FacturaDetail::getProductListDTO)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", facturaId));
    }

    public SupplyFacturaDTO get(Long id) {
        Factura factura = facturaRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", id));
        if (!SecurityUtils.isFromAdmin() &&
                !(
                        factura.getSellerId().equals(getCurrentCompanyId()) ||
                                factura.getBuyerId().equals(getCurrentCompanyId())
                )
        ) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ACCESS_DENIED), localizationService.localize(MessageConstants.CANNOT_UPDATE_OTHER_COMPANY_DATA)).get();
        }
        SupplyFacturaDTO supplyFacturaDTO = factura.toDTO();
        if (Status.DRAFT.equals(factura.getStatus())) {
            supplyFacturaDTO.setFacturaDTO(factura.toFacturaDTO(false));
        } else if (StringUtils.hasText(factura.getSign())) {
            FacturaDTO facturaDTO;
            try {
                facturaDTO = convertFromSign(factura.getSign());
            } catch (Exception e) {
                log.error("ERROR: {}", e.getMessage());
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), e.getMessage()).get();
            }
            supplyFacturaDTO.setFacturaDTO(facturaDTO);
        }
        if (supplyFacturaDTO.getFacturaDTO() == null || supplyFacturaDTO.getFacturaDTO().getFacturaDoc() == null) {
            supplyFacturaDTO.setFacturaDTO(factura.toFacturaDTO());
        }
        supplyFacturaDTO.setSign(factura.getSign());

        return supplyFacturaDTO;
    }

    public SupplyFacturaDTO getWithProductType(Long id) {
        Factura factura = facturaRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", id));
        if (
                !(
                        factura.getSellerId().equals(getCurrentCompanyId()) ||
                                factura.getBuyerId().equals(getCurrentCompanyId())
                )
        ) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ACCESS_DENIED), localizationService.localize(MessageConstants.CANNOT_UPDATE_OTHER_COMPANY_DATA)).get();
        }
        SupplyFacturaDTO supplyFacturaDTO = factura.toDTO();
        supplyFacturaDTO.setBuyerId(factura.getBuyerId());
        supplyFacturaDTO.setSellerId(factura.getSellerId());
        if (factura.getBuyer() != null) {
            supplyFacturaDTO.setHasFinancialDiscount(factura.getBuyer().isHasFinancialDiscount());
        }
        if (Status.DRAFT.equals(factura.getStatus())) {
            supplyFacturaDTO.setFacturaDTO(factura.toFacturaDTO());
        } else if (StringUtils.hasText(factura.getSign())) {
            supplyFacturaDTO.setFacturaDTO(convertFromSign(factura.getSign()));
        }
        if (supplyFacturaDTO.getFacturaDTO() == null || supplyFacturaDTO.getFacturaDTO().getFacturaDoc() == null) {
            supplyFacturaDTO.setFacturaDTO(factura.toFacturaDTO());
        }
        if (factura.getStock() != null && !factura.getStock().isDeleted()) {
            supplyFacturaDTO.setStock(factura.getStock().toStockCommonDTO());
        }
        if (factura.getStockIn() != null && !factura.getStockIn().isDeleted()) {
            supplyFacturaDTO.setStockIn(factura.getStockIn().toStockCommonDTO());
        }

        FacturaDTO facturaDTO = supplyFacturaDTO.getFacturaDTO();
        List<FacturaProductDTO> facturaProductList = new ArrayList<>();
        facturaDTO
                .getProductList()
                .getProducts()
                .forEach(facturaProductDTO -> {
                    Product product = null;
                    if (StringUtils.hasText(facturaProductDTO.getBarcode())) {
                        product =
                                productRepository
                                        .findFirstByBarcodeAndCompanyIdAndProductTypeIsNotNullAndDeletedIsFalse(
                                                facturaProductDTO.getBarcode(),
                                                getCurrentCompanyId()
                                        )
                                        .orElse(null);
                    }
                    if (product == null && StringUtils.hasText(facturaProductDTO.getCatalogCode())) {
                        product =
                                productRepository
                                        .findFirstByVatBarcodeAndCompanyIdAndProductTypeIsNotNullAndDeletedIsFalse(
                                                facturaProductDTO.getCatalogCode(),
                                                getCurrentCompanyId()
                                        )
                                        .orElse(null);
                    }
                    if (product != null) {
                        facturaProductDTO.setProductType(product.getProductType());
                    }
                    facturaProductList.add(facturaProductDTO);
                });
        facturaDTO.getProductList().setProducts(facturaProductList);
        supplyFacturaDTO.setFacturaDTO(facturaDTO);
        return supplyFacturaDTO;
    }

    public FacturaDTO convertFromSign(String sign) {
        log.info("=========================== Start convert sign ===========================");
        try {
            Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
            if (pkcs7Dto.isSuccess()) {
                return facturaWebhookService.parseBase64(pkcs7Dto.getPkcs7Info().getDocumentBase64());
            } else {
                log.error("Parse sign error: {}", pkcs7Dto.getMessage());
            }
        } catch (Exception e) {
            log.error("Parse sign error: {}", e.getMessage());
            throw validationService.badRequestExceptionThrow(e.getMessage()).get();
        }
        return null;
    }

    public List<FacturaLookUpDTO> lookUp(FacturaFilter filter) {
        if (!SecurityUtils.isFromAdmin()) {
            if (filter.isIncoming()) {
                filter.setBuyerId(getCurrentCompanyId());
            } else {
                filter.setSellerId(getCurrentCompanyId());
            }
        }
        return facturaRepository.lookUp(filter)
            .stream()
            .map(factura -> {
                FacturaLookUpDTO facturaLookUpDTO = new FacturaLookUpDTO();
                facturaLookUpDTO.setId(factura.getId());
                facturaLookUpDTO.setFacturaId(factura.getFacturaId());
                facturaLookUpDTO.setFacturaNo(factura.getFacturaNo());
                facturaLookUpDTO.setFacturaDate(factura.getFacturaDate());
                return facturaLookUpDTO;
            }).toList();
    }

    @Transactional
    public Long send(FacturaSendDTO facturaSendDTO) {
        Company company = getCurrentCompany();
        UsagePlan usagePlan = usagePlanService.getUsagePlan(CountableServiceTypes.INVOICE.getCode());
        if (!company.isFreeTrial()) {
            if (usagePlan.getQty() <= 0) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.INSUFFICIENT_BALANCE_ERROR)).get();
            }
        }

        Factura factura = facturaRepository
                .findFirstByFacturaIdAndDeletedIsFalse(facturaSendDTO.getFacturaId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", facturaSendDTO.getFacturaId()));

        log.info("======================= Factura send started ........... ======================");
        Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(facturaSendDTO.getSign());
        if (pkcs7Dto.isSuccess() && !CollectionUtils.isEmpty(pkcs7Dto.getTimestampedSignerList())) {
            String sign = pkcs7Dto.getPkcs7b64();
            SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().getFirst();
            facturaSendDTO.setSign(sign);
/*            try {
                facturaValidationService.create(factura.getId(), company.getTin(), facturaSendDTO);
            } catch (Exception ex) {
                log.error("Factura validation failed (ignored) facturaId={} : {}", facturaSendDTO.getFacturaId(), ex.getMessage(), ex);
            }*/
            facturaRestClient.sendFactura(company.getTin(), facturaSendDTO);

            factura.setSign(sign);
            factura.setStatus(Status.PENDING);
            factura.setSendNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
            factura.setSendBy(certificateDto.getSubjectName().getFullName());
            factura.setSendDate(LocalDateTime.now());
            factura.setOutgoing(true);
            facturaRepository.save(factura);

            usagePlan.setQty(usagePlan.getQty() - 1);
            usagePlanRepository.save(usagePlan);
            return factura.getId();
        } else {
            log.info("JSON: {}", pkcs7Dto);
            throw new IntegrationException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), pkcs7Dto.getMessage());
        }
    }

    @Transactional
    public Long changeStatus(FacturaSendDTO facturaSendDTO, Status status) {
        Factura factura = facturaRepository
                .findFirstByFacturaIdAndDeletedIsFalse(facturaSendDTO.getFacturaId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "facturaId", facturaSendDTO.getFacturaId()));

        if (Status.CANCELLED.equals(status) && !Status.PENDING.equals(factura.getStatus())) {
            throw validationService.badRequestExceptionThrow(
                    localizationService.localize(MessageConstants.CAN_NOT_CANCEL_INVOICE_WITH_STATUS, new Object[]{factura.getStatus().getName()})
                    , "Cancel not allowed"
            ).get();
        }
        StringBuilder message = new StringBuilder();
        message.append("SEND JSON: ");
        try {
            log.info("\n FacturaSendDTO: {}", objectMapper.writeValueAsString(facturaSendDTO));
            message.append(objectMapper.writeValueAsString(facturaSendDTO));
            message.append("\n");
        } catch (JsonProcessingException e) {
            log.error("Writing object error");
        }

        Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(facturaSendDTO.getSign());
        try {
            log.info("\n Pkcs7Dto object: {}", objectMapper.writeValueAsString(facturaSendDTO));
            message.append("\nPkcs7Dto object: ").append(objectMapper.writeValueAsString(pkcs7Dto));
            message.append("\n");
        } catch (JsonProcessingException e) {
            log.error("Writing object error");
        }
        if (pkcs7Dto.isSuccess() && !CollectionUtils.isEmpty(pkcs7Dto.getTimestampedSignerList())) {

            SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().size() > 1 ?
                    pkcs7Dto.getTimestampedSignerList().get(1) : pkcs7Dto.getTimestampedSignerList().get(0);

            Company company = getCurrentCompany();
            facturaSendDTO.setSign(pkcs7Dto.getPkcs7b64());
            if (Status.CANCELLED.equals(status)) {
                facturaRestClient.cancelFactura(company.getTin(), facturaSendDTO);
            } else {
                facturaRestClient.acceptOrReject(company.getTin(), facturaSendDTO);
            }

            factura.setStatus(status);
            factura.setNotes(facturaSendDTO.getNotes());
            factura.setApprovedNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
            factura.setApprovedBy(certificateDto.getSubjectName().getFullName());
            factura.setApprovedDate(LocalDateTime.now());
            facturaRepository.save(factura);
            return factura.getId();
        } else {
            log.info("\nJSON: {}", pkcs7Dto);
            message.append("\nException message: ").append(pkcs7Dto.getMessage());
            telegramApplicationLogService.send(message.toString());
            throw new IntegrationException("Ошибка от ГНК", pkcs7Dto.getMessage());
        }
    }

    @Transactional
    public Long cancelDraft(FacturaSendDTO facturaSendDTO) {
        Factura factura = facturaRepository
                .findFirstByFacturaIdAndDeletedIsFalse(facturaSendDTO.getFacturaId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "facturaId", facturaSendDTO.getFacturaId()));

        factura.setStatus(Status.CANCELLED);
        if (StringUtils.hasText(facturaSendDTO.getNotes())) {
            factura.setNotes(facturaSendDTO.getNotes());
        }
        facturaRepository.save(factura);
        return factura.getId();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Factura factura = facturaRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", id));
        if (!(Status.DRAFT.equals(factura.getStatus()) || Status.CANCELLED.equals(factura.getStatus()))) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ACTION_UNAVAILABLE, new Object[]{localizationService.localize(MessageConstants.DELETE), localizationService.localize(MessageConstants.INVOICE), localizationService.localize(MessageConstants.DRAFT) + " " + localizationService.localize(MessageConstants.CANCELLED)})).get();
        }
        Stock stock = factura.getStock();
        if (stock != null) {
            stock.setFacturaId(null);
            stock.getFacturas().remove(factura);
            stockOutRepository.save(stock);
        }
        factura.setStockId(null);
        factura.setDeleted(true);
        factura.setFacturaId(null);
        facturaRepository.save(factura);
    }

    private Long getUnitId(Long measureId) {
        if (UNITS.isEmpty()) {
            List<Unit> units = unitRepository.findAllByDeletedIsFalseOrderBySorder();
            //Key =MeasureId, Value = unitId
            units.forEach(unit -> {
                if (unit.getMeasureId() != null) {
                    UNITS.put(unit.getMeasureId(), unit.getId());
                }
            });
        }
        return UNITS.get(measureId);
    }

    private Factura saveFactura(Factura factura, FacturaDTO facturaDTO) {
        if (facturaDTO.getContractDoc() == null || !StringUtils.hasText(facturaDTO.getContractDoc().getContractNo()) && facturaDTO.getContractDoc().getContractDate() == null) {
            throw validationService.badRequestExceptionThrow("Требуется договор").get();
        }
        if (factura == null) {
            factura = new Factura();
            factura.setOutgoing(true);
        }
        FacturaDetail facturaDetail;
        boolean isNewFacturaDetail = false;
        if (factura.getFacturaDetail() != null && factura.getFacturaDetail().getId() != null) {
            facturaDetail = factura.getFacturaDetail();
        } else {
            facturaDetail = new FacturaDetail();
            isNewFacturaDetail = true;
        }
        if (factura.getBranchId() == null) {
            factura.setBranchId(getCurrentUser().getBranchId());
        }
        factura.setStatus(Status.DRAFT);
        factura.setHasFinancialDiscount(facturaDTO.getAppliedFinancialDiscount() != null && facturaDTO.getAppliedFinancialDiscount());
        factura.setVersion(Optional.ofNullable(facturaDTO.getVersion()).orElse(1));
        factura.setFacturaType(Optional.ofNullable(facturaDTO.getFacturaType()).orElse(FacturaType.STANDART.getId()));
        factura.setSingleSidedType(facturaDTO.getSingleSidedType());

        factura.setFacturaId(facturaDTO.getFacturaId());
        facturaDTO.setFacturaId(factura.getFacturaId());

        if (facturaDTO.getFacturaDoc() != null) {
            factura.fromFacturaDocDTO(facturaDTO.getFacturaDoc());
            facturaDetail.fromFacturaDocDTO(facturaDTO.getFacturaDoc());
        }
        if (StringUtils.hasText(facturaDTO.getSellerTin())) {
            Company seller = companyRepository.findByTinAndDeletedIsFalse(facturaDTO.getSellerTin()).orElse(companyRepository.findFirstByTinAndDeletedIsFalse(facturaDTO.getSellerTin()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", facturaDTO.getSellerTin())));
            factura.setSellerId(seller.getId());
        }
        if (StringUtils.hasText(facturaDTO.getBuyerTin())) {
            Company buyer = companyRepository.findByTinAndDeletedIsFalse(facturaDTO.getBuyerTin()).orElse(companyRepository.findFirstByTinAndDeletedIsFalse(facturaDTO.getBuyerTin()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", facturaDTO.getBuyerTin())));
            factura.setBuyerId(buyer.getId());
        }
        if (factura.getCompanyId() == null) {
            factura.setCompanyId(factura.getSellerId());
        }
        factura.setUpdatedDate(LocalDateTime.now());
        factura.fromContractDTO(Optional.ofNullable(facturaDTO.getContractDoc()).orElseGet(FacturaContractDTO::new));

        FacturaCompanyDTO sellerDTO = Optional.ofNullable(facturaDTO.getSeller()).orElse(new FacturaCompanyDTO());
        //Getting Seller info from OFD
        log.info("Factura {} - SELLER: frontend tin={}, director before Soliq={}",facturaDTO.getFacturaId(),facturaDTO.getSellerTin(),sellerDTO.getDirector());
        if (!enrichCompanyFromSoliq(sellerDTO, facturaDTO.getSellerTin())) {
            log.error("Factura {} - SELLER enrichment FAILED: tin={}, director={}",facturaDTO.getFacturaId(),facturaDTO.getSellerTin(),sellerDTO.getDirector());
            throw validationService.badRequestExceptionThrow("Не удалось получить директора продавца").get();
        }
        log.info("Factura {} - SELLER after Soliq: tin={}, director={}",facturaDTO.getFacturaId(),facturaDTO.getSellerTin(),sellerDTO.getDirector());

        FacturaCompanyDTO buyerDTO = Optional.ofNullable(facturaDTO.getBuyer()).orElse(new FacturaCompanyDTO());
        //Getting Buyer info from OFD
        log.info("Factura {} - BUYER: frontend tin={}, director before Soliq={}", facturaDTO.getFacturaId(), facturaDTO.getBuyerTin(), buyerDTO.getDirector());

        if (!enrichCompanyFromSoliq(buyerDTO, facturaDTO.getBuyerTin())) {
            log.error("Factura {} - BUYER enrichment FAILED: tin={}, director={}", facturaDTO.getFacturaId(), facturaDTO.getBuyerTin(), buyerDTO.getDirector());
            throw validationService.badRequestExceptionThrow("Не удалось получить директора покупателя").get();
        }
        log.info("Factura {} - BUYER after Soliq: tin={}, director={}", facturaDTO.getFacturaId(), facturaDTO.getBuyerTin(), buyerDTO.getDirector());
        factura = facturaRepository.save(factura);

        facturaDetail.setNicFacturaId(facturaDTO.getFacturaId());
        facturaDetail.setFactura(factura);
        facturaDetail.fromSellerDTO(sellerDTO);
        facturaDetail.fromBuyerDTO(buyerDTO);
        facturaDetail = facturaDetailRepository.save(facturaDetail);
        log.info("Factura {} - AFTER detail save: detailId={}, sellerDirector={}, buyerDirector={}", facturaDTO.getFacturaId(), facturaDetail.getId(), facturaDetail.getSellerDirector(), facturaDetail.getBuyerDirector());
        factura.getFacturaDetails().clear();
        factura.getFacturaDetails().add(facturaDetail);
        facturaDetail.setLotId(facturaDTO.getLotId());
        facturaDetail.setHasMarking(Optional.ofNullable(facturaDTO.getHasMarking()).orElse(Boolean.FALSE));
        facturaDetail.fromOldFacturaDTO(Optional.ofNullable(facturaDTO.getOldFacturaDoc()).orElse(new OldFacturaDoc()));
        facturaDetail.setEmpowermentId(facturaDTO.getEmpowermentId());
        facturaDetail.fromEmpowermentDTO(Optional.ofNullable(facturaDTO.getFacturaEmpowermentDoc()).orElse(new FacturaEmpowermentDTO()));
        facturaDetail.fromItemReleasedDocDTO(Optional.ofNullable(facturaDTO.getItemReleasedDoc()).orElse(new FacturaItemReleasedDocDTO()));
        facturaDetail.fromForeignCompanyDTO(Optional.ofNullable(facturaDTO.getForeignCompany()).orElse(new FacturaForeignCompanyDTO()));

        // set investment
        facturaDetail.fromFacturaInvestmentObjectDocDTO(facturaDTO.getFacturaInvestmentObjectDoc());

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalLgotaVatSum = BigDecimal.ZERO;
        if (facturaDTO.getProductList() != null) {
            FacturaProductListDTO productList = facturaDTO.getProductList();
            if (!StringUtils.hasText(productList.getTin())) {
                productList.setTin(Optional.ofNullable(facturaDetail.getTin()).orElse(facturaDTO.getSellerTin()));
            }
            facturaDetail.fromProductListDTO(productList);
            factura.setFacturaProductId(productList.getFacturaProductId());

            if (!CollectionUtils.isEmpty(productList.getProducts())) {
                BigDecimal totalVat = BigDecimal.ZERO;
                if (!isNewFacturaDetail) {
                    facturaProductRepository.deleteAllByFacturaDetailId(facturaDetail.getId());
                }
                int sOrder = 0;
                for (FacturaProductDTO facturaProductDTO : productList.getProducts()) {
                    FacturaProduct facturaProduct = new FacturaProduct();
                    if (facturaProductDTO.getBranch() != null) {
                        facturaProduct.setBranchId(facturaProductDTO.getBranch().getId());
                    }
                    facturaProduct.setSOrder(++sOrder);
                    if (facturaProductDTO.getWarehouse() != null) {
                        facturaProduct.setWarehouseId(facturaProductDTO.getWarehouse().getId());
                    }
                    if (facturaProductDTO.getProductId() != null) {
                        Product product = productRepository.findById(facturaProductDTO.getProductId()).orElse(null);
                        if (product != null) {
                            facturaProduct.setProductId(product.getId());
                            facturaProduct.setProduct(product);
                            if (ProductType.Pharma == product.getProductType()) {
                                if (product.getDispenseType() != null) {
                                    facturaProductDTO.setDispenseType(product.getDispenseType().getId());
                                } else {
                                    throw validationService.badRequestExceptionThrow("Этот тип товара " + facturaProduct.getProduct().getName() + " является медицинским. Поэтому необходимо выбрать один тип отпуска в каталоге для данного товара: «По рецепту» или «Без рецепта».").get();
                                }
                            }
                            //factura.setHasFinancialDiscount(facturaDTO.getAppliedFinancialDiscount());
                            if (facturaProductDTO.getProductType() != null && product.getProductType() == null) {
                                product.setProductType(facturaProductDTO.getProductType());
                                product.setHasMark(true);
                                productRepository.save(product);
                            }
                        }
                    }

                    facturaProduct.setOrdNo(facturaProductDTO.getOrdNo());
                    facturaProduct.setCode(facturaProductDTO.getCode());

                    if (StringUtils.hasText(facturaProductDTO.getCommittentTin())) {
                        facturaDetail.setHasCommittent(Boolean.TRUE);
                        facturaProduct.setCommittentName(facturaProductDTO.getCommittentName());
                        facturaProduct.setCommittentTin(facturaProductDTO.getCommittentTin());
                        facturaProduct.setCommittentVatRegCode(facturaProductDTO.getCommittentVatRegCode());
                        facturaProduct.setCommittentVatRegStatus(facturaProductDTO.getCommittentVatRegStatus());
                    } else {
                        facturaDetail.setHasCommittent(Boolean.FALSE);
                        facturaProduct.setCommittentName(null);
                        facturaProduct.setCommittentTin(null);
                        facturaProduct.setCommittentVatRegCode(null);
                        facturaProduct.setCommittentVatRegStatus(null);
                    }

                    if (facturaProductDTO.getLgotaId() != null) {
//                        facturaDetail.setHasLgota(Boolean.TRUE);
//                        facturaProduct.setLgotaId(facturaProductDTO.getLgotaId());
//                        facturaProduct.setLgotaName(facturaProductDTO.getLgotaName());
//                        facturaProduct.setLgotaType(facturaProductDTO.getLgotaType());
//                        facturaProduct.setLgotaVatSum(facturaProductDTO.getLgotaVatSum());
//                        if (facturaProductDTO.getLgotaVatSum() != null) {
//                            totalLgotaVatSum = totalLgotaVatSum.add(facturaProductDTO.getLgotaVatSum());
//                        }
                    } else {
                        facturaDetail.setHasLgota(Boolean.FALSE);
                        facturaProduct.setLgotaId(null);
                        facturaProduct.setLgotaName(null);
                        facturaProduct.setLgotaType(null);
                        facturaProduct.setLgotaVatSum(BigDecimal.ZERO);
                    }

                    facturaProduct.setCatalogCode(facturaProductDTO.getCatalogCode());
                    facturaProduct.setCatalogName(facturaProductDTO.getCatalogName());
                    facturaProduct.setBarcode(facturaProductDTO.getBarcode());

                    facturaProduct.setName(facturaProductDTO.getName());
                    if (StringUtils.hasText(facturaProductDTO.getPackageCode())) {
                        facturaProduct.setPackageCode(facturaProductDTO.getPackageCode());
                        facturaProduct.setPackageName(facturaProductDTO.getPackageName());
                        if (facturaProduct.getProduct() != null && !facturaProductDTO.getPackageCode().equals(facturaProduct.getProduct().getPackageCode())) {
                            Product product = facturaProduct.getProduct();
                            product.setPackageCode(facturaProductDTO.getPackageCode());
                            product.setPackageName(facturaProductDTO.getPackageName());
                            productRepository.save(product);
                        }
                    }
                    facturaProduct.setSerial(facturaProductDTO.getSerial());
                    facturaProduct.setProductSerialId(facturaProductDTO.getProductSerialId());
                    if (
                            StringUtils.hasText(facturaProductDTO.getSerial()) &&
                                    facturaProductDTO.getBaseSumma() != null &&
                                    facturaProductDTO.getProfitRate() != null
                    ) {
                        facturaDetail.setHasMedical(Boolean.TRUE);
                    }
                    if (!StringUtils.hasText(facturaProductDTO.getPackageCode())) {
                        if (facturaProductDTO.getMeasureId() != null) {
                            facturaProduct.setMeasureId(getUnitId(facturaProductDTO.getMeasureId()));
                        }
                    }
                    facturaProduct.setBaseSumma(facturaProductDTO.getBaseSumma());
                    if (productList.getHasMedical() != null && productList.getHasMedical()) {
                        facturaProduct.setProfitRate(facturaProductDTO.getProfitRate());
                    } else {
                        facturaProduct.setProfitRate(BigDecimal.ZERO);
                    }
                    facturaProduct.setQty(facturaProductDTO.getCount());
                    facturaProduct.setSumma(facturaProductDTO.getSumma());
                    facturaProduct.setExciseRate(Optional.ofNullable(facturaProductDTO.getExciseRate()).orElse(BigDecimal.ZERO));
                    facturaProduct.setExciseSum(Optional.ofNullable(facturaProductDTO.getExciseSum()).orElse(BigDecimal.ZERO));
//                    facturaDetail.setHasExcise(
//                            facturaProductDTO.getExciseRate() != null && facturaProductDTO.getExciseRate().compareTo(BigDecimal.ZERO) != 0
//                    );
                    facturaProduct.setDeliverySum(facturaProductDTO.getDeliverySum());
                    facturaProduct.setDeliverySumWithVat(facturaProductDTO.getDeliverySumWithVat());
                    facturaProduct.setOrigin(facturaProductDTO.getOrigin());
                    facturaProduct.setDispenseType(facturaProductDTO.getDispenseType());

                    if (StringUtils.hasText(facturaDetail.getLotId()) && facturaProductDTO.getExchangeInfo() != null) {
                        facturaProduct.fromExchangeInfo(facturaProductDTO.getExchangeInfo());
                    }
                    if (facturaProductDTO.getVatRate() == null) {
                        facturaProduct.setVatRate(BigDecimal.ZERO);
                        facturaProduct.setVatSum(BigDecimal.ZERO);
                        facturaProduct.setWithoutVat(Boolean.TRUE);
                    } else if (facturaProductDTO.getVatRate().compareTo(BigDecimal.ZERO) == 0) {
                        facturaProduct.setVatRate(BigDecimal.ZERO);
                        facturaProduct.setVatSum(BigDecimal.ZERO);
                        facturaProduct.setWithoutVat(Boolean.FALSE);
                    } else {
                        facturaProduct.setVatRate(facturaProductDTO.getVatRate());
                        facturaProduct.setVatSum(facturaProductDTO.getVatSum());
                        facturaProduct.setWithoutVat(Boolean.FALSE);
                        totalVat = totalVat.add(facturaProductDTO.getVatSum());
                        facturaDetail.setHasVat(Boolean.TRUE);
                    }
                    if (facturaProductDTO.getMarks() != null && (!CollectionUtils.isEmpty(facturaProductDTO.getMarks().getKiz()) || !CollectionUtils.isEmpty(facturaProductDTO.getMarks().getIdentTransUpak()) || !CollectionUtils.isEmpty(facturaProductDTO.getMarks().getNomUpak()))) {
                        MarkDTO markDTO = facturaProductDTO.getMarks();
                        facturaDetail.setHasMarking(Boolean.TRUE);

                        if (markDTO.getProductType() == null) {
                            if (facturaProduct.getProduct() != null && facturaProduct.getProduct().getProductType() != null) {
                                markDTO.setProductType(facturaProduct.getProduct().getProductType().getProductGroup().getMarkingCode());
                            } else if (factura.getSeller() != null && factura.getSeller().getProductGroup() != null) {
                                markDTO.setProductType(factura.getSeller().getProductGroup().getMarkingCode());
                            } else if (factura.getBuyer() != null && factura.getBuyer().getProductGroup() != null) {
                                markDTO.setProductType(factura.getBuyer().getProductGroup().getMarkingCode());
                            } else {
                                if (getCurrentCompany() != null) {
                                    markDTO.setProductType(getCurrentCompany().getProductGroup().getMarkingCode());
                                } else {
                                    throw new IntegrationException("Укажите тип продукции");
                                }
                            }
                        }
                        facturaProduct.fromMarkDTO(markDTO);
                    } else {
                        facturaProduct.fromMarkDTO(new MarkDTO());
                        facturaDetail.setHasMarking(Boolean.FALSE);
                    }

                    facturaDetail.addProduct(facturaProduct);
                    total = total.add(facturaProductDTO.getDeliverySumWithVat());
                }
            }
        }
        if (facturaDTO.getProductList() != null && facturaDTO.getProductList().getDeliverySumWithVat() != null) {
            factura.setTotal(facturaDTO.getProductList().getDeliverySumWithVat());
        } else if (total.compareTo(BigDecimal.ZERO) != 0) {
            factura.setTotal(total);
        }
        if (BigDecimal.ZERO.compareTo(totalLgotaVatSum) != 0) {
            factura.setTotalLgotaVatSum(totalLgotaVatSum);
        }
        facturaDetailRepository.save(facturaDetail);
        facturaRepository.save(factura);
        return factura;
    }

    public List<FacturaStatisticsDTO> getStatistics(FacturaStatisticsFilter filter) {
        return facturaRepository.getFacturaStatistics(filter);
    }

    public String decodeSign(SignDTO signDTO) {
        return eimzoClient.backendVerifyAttachedString(signDTO.getSign());
    }

    public VATCompanyDateDTO getCompanyVatByDate(Company company) {
        return this.getCompanyVatByDate(company.getTin(), LocalDate.now());
    }

    public VATCompanyDateDTO getCompanyVatByDate(String tin, LocalDate date) {
        VATCompanyDateDTO vatCompanyDateDTO = soliqClient.getCompanyVatByDate(tin, date, false);
        if (vatCompanyDateDTO == null) {
            vatCompanyDateDTO = new VATCompanyDateDTO();
            CompanyVatRegistration companyVatRegistration = companyVatRegistrationRepository.findFirstByCompany_Tin(tin).orElse(null);
            if (companyVatRegistration != null) {
                CompanyVatDTO data = new CompanyVatDTO();
                data.setVatRegCode(companyVatRegistration.getVatRegCode());
                data.setActive(companyVatRegistration.getVatRegStatus() != null && companyVatRegistration.getVatRegStatus() == 20);
                data.setSuspended(companyVatRegistration.getVatRegStatus() != null && companyVatRegistration.getVatRegStatus() == 22);
                vatCompanyDateDTO.setData(data);
                vatCompanyDateDTO.setSuccess(true);
                return vatCompanyDateDTO;
            }
        }
        return vatCompanyDateDTO;
    }

    public List<ProviderBindingDTO> getProviderBinding(String tin) {
        return facturaRestClient.getProviderBinding(tin);
    }

    public void syncProducts(Long id) {
        facturaRepository.findById(id).map(factura -> {
            String tin = getCurrentCompanyTin();
            Long userId = getCurrentUserId();
            factura
                    .getFacturaDetail()
                    .getProducts()
                    .forEach(facturaProduct -> {
                        if (facturaProduct.getProductId() != null) {
                            rabbitMqTasnifCatalogProducer.syncTasnifCatalogByProductQueue(
                                    new SyncTasnifCatalogByProductPayload(tin, userId, facturaProduct.getProductId(), facturaProduct.getId())
                            );
                        }
                    });
            return factura;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", id));
    }

    public Page<FacturaDTO> getListTo1C(FacturaFilter filter) {
        ResultList<Factura> resultList = facturaRepository.getListTo1C(filter);
        List<FacturaDTO> result = new ArrayList<>();
        int count = 0;
        for (Factura factura : resultList.getList()) {
            if (Status.DRAFT == factura.getStatus()) {
                result.add(factura.toFacturaDTO());
            } else if (StringUtils.hasText(factura.getSign())) {
                FacturaDTO facturaDTO = convertFromSign(factura.getSign());
                if (facturaDTO != null) {
                    result.add(facturaDTO);
                } else {
                    count++;
                }
            }
        }
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount() - count);
    }

    @Transactional
    public void updateStatus(Long id, Status status) {
        facturaRepository
                .findById(id)
                .map(factura -> {
                    factura.setStatus(status);
                    facturaRepository.save(factura);
                    return factura;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", id));
    }

    public FacturaStatisticsByStatusDTO getStatistics(FacturaFilter filter) {
        return facturaRepository.getStatistics(filter);
    }

    @Transactional
    public void syncVatRegistrationCode(Payload payload) {
        companyRepository.findById(payload.getId()).ifPresent(company -> {
            log.info("Sync Company Vat Reg Codes. Company Id :{}, Company TIN: {}, Date :{}", company.getId(), company.getTin(), LocalDate.now());
            try {
                VATCompanyDateDTO vatCompanyDateDTO = soliqClient.getCompanyVatByDate(company.getTin(), LocalDate.now(), false);
                if (vatCompanyDateDTO != null && vatCompanyDateDTO.getData() != null && StringUtils.hasText(vatCompanyDateDTO.getData().getVatRegCode())) {
                    CompanyVatRegistration companyVatRegistration = companyVatRegistrationRepository.findFirstByCompanyId(company.getId()).orElse(new CompanyVatRegistration());
                    companyVatRegistration.setCompanyId(company.getId());
                    companyVatRegistration.setVatRegCode(vatCompanyDateDTO.getData().getVatRegCode());
                    companyVatRegistration.setVatRegStatus(vatCompanyDateDTO.getData().getStatus());
                    companyVatRegistrationRepository.save(companyVatRegistration);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Transactional
    public void syncFacturaGUID(GenerateFacturaGUIDPayload payload) {
        List<FacturaGUID> facturaGUIDList = new ArrayList<>();
        for (int i = 0; i < payload.getCount(); i++) {
            try {
                FacturaGUID facturaGUID = new FacturaGUID();
                facturaGUID.setFacturaId(facturaRestClient.getGuid());
                facturaGUIDList.add(facturaGUID);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        if (!facturaGUIDList.isEmpty()) {
            facturaGUIDRepository.saveAll(facturaGUIDList);
        }
    }

    public String getGuid(GuidType guidType) {
        FacturaGUID facturaGUID = facturaGUIDRepository.findFirstByFacturaIdIsNotNullAndUsedIsFalse().orElse(null);
        if (facturaGUID != null) {
            String guid = null;
            if (GuidType.FACTURA == guidType && !facturaRepository.existsByFacturaId(facturaGUID.getFacturaId())) {
                guid = facturaGUID.getFacturaId();
            } else if (GuidType.FACTURA_PRODUCT == guidType && !facturaDetailRepository.existsByFacturaProductId(facturaGUID.getFacturaId())) {
                guid = facturaGUID.getFacturaId();
            } else if (GuidType.AGENT_FACTURA == guidType && !facturaDetailRepository.existsByAgentFacturaId(facturaGUID.getFacturaId())) {
                guid = facturaGUID.getFacturaId();
            }
            if (guid != null) {
                facturaGUID.setUsed(true);
                facturaGUIDRepository.saveAndFlush(facturaGUID);
                return guid;
            }
        }
        return facturaRestClient.getGuid();
    }

    public FacturaProductListDTO getFacturaProductList(String tin, String facturaProductId) {
        return facturaRestClient.getFacturaProductList(tin, facturaProductId);
    }

    public FacturaCompanyCommittentDTO getCommittent(String tin) {
        Company company = companyRepository.findByTinAndDeletedIsFalse(tin).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", tin));
        VATCompanyDateDTO vatCompanyDateDTO = this.getCompanyVatByDate(tin, LocalDate.now());
        FacturaCompanyCommittentDTO facturaCompanyCommittentDTO = new FacturaCompanyCommittentDTO();
        facturaCompanyCommittentDTO.setTin(tin);
        facturaCompanyCommittentDTO.setName(company.getName());
        facturaCompanyCommittentDTO.setResult(vatCompanyDateDTO);
        return facturaCompanyCommittentDTO;
    }

    @Transactional
    public VATCompanyDateDTO updateCompanyVatRegistration(Long id, Long sellerId, Long buyerId) {
        if (sellerId == null && buyerId == null) {
            throw validationService.badRequestExceptionThrow("Seller or buyer should be provided").get();
        }
        Long companyId = Optional.ofNullable(sellerId).orElse(buyerId);
        Company company = companyRepository.findById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
        VATCompanyDateDTO vatCompanyDateDTO = this.getCompanyVatByDate(company);
        if (vatCompanyDateDTO.isSuccess()) {
            String vatRegCode = vatCompanyDateDTO.getData() != null ? vatCompanyDateDTO.getData().getVatRegCode() : null;
            Integer vatRegStatus = vatCompanyDateDTO.getData() != null ? vatCompanyDateDTO.getData().getStatus() : null;
            companyVatRegistrationRepository.update(companyId, vatRegCode, vatRegStatus);
            if (sellerId != null) {
                facturaDetailRepository.updateSellerVatRegistration(id, vatRegCode, vatRegStatus);
            } else {
                facturaDetailRepository.updateBuyerVatRegistration(id, vatRegCode, vatRegStatus);
            }
        }
        return vatCompanyDateDTO;
    }

    public void syncVatRegistrationCodes() {
        log.info("=============== Sync Vat Registration Code Cron started at :{}", LocalDateTime.now() + " ============================");
        partnerService
                .getCompanyList()
                .forEach(companyId -> rabbitMqFacturaProducer
                        .syncVatRegistrationCode(new Payload(companyId)));
    }

    public void syncFacturaGUID() {
        log.info("=============== Sync Factura GUID Cron started at :{}", LocalDateTime.now() + " ============================");
        for (int i = 0; i < COMMON_BATCH_SIZE; i++) {
            rabbitMqFacturaProducer.generateFacturaGUID(new GenerateFacturaGUIDPayload(1));
        }
    }

    public InvestmentObjectDTO getInvestmentObject(String tin) {
        String currentCompanyTin = Optional.ofNullable(tin).orElse(SecurityUtils.getCurrentCompanyTin());
        InvestmentObjectBuyerDTO investmentObjectBuyerDTO = facturaRestClient.checkIsInvestmentObjectBuyer(currentCompanyTin);
        if (Boolean.TRUE.equals(investmentObjectBuyerDTO.getIsInvestmentObjectBuyer())) {
            return facturaRestClient.getInvestmentObject(currentCompanyTin);
        }
        return null;
    }

    private void setSellerDetails(Stock stock, Company seller, FacturaDetail facturaDetail) {
        Optional<BranchBank> optionalBranchBank = branchBankRepository.findByBranchId(stock.getBranchId());
        if (optionalBranchBank.isPresent()) {
            BranchBank branchBank = optionalBranchBank.get();
            facturaDetail.setSellerAccount(branchBank.getAccountNumber());
            facturaDetail.setSellerBankId(branchBank.getBank().getMfo());
            facturaDetail.setSellerBankName(branchBank.getBank().getName());
            facturaDetail.setSellerOked(branchBank.getOked());
        } else {
            facturaDetail.setSellerAccount(seller.getCompanyBank().getAccountNumber());
            facturaDetail.setSellerBankId(seller.getCompanyBank().getBank().getMfo());
            facturaDetail.setSellerBankName(seller.getCompanyBank().getBank().getName());
            facturaDetail.setSellerOked(seller.getCompanyBank().getOked());
        }

        if (stock.getBranch().getBranchNumber() != null) {
            facturaDetail.setSellerAddress(
                    Optional.ofNullable(stock.getBranch().getAddress())
                            .map(Address::getFullAddress)
                            .orElse(seller.getAddress().getFullAddress())
            );
        } else {
            facturaDetail.setSellerAddress(seller.getAddress().getFullAddress());
        }
        if (seller.getOwner() != null) {
            facturaDetail.setSellerMobile(seller.getOwner().getPhone());
            facturaDetail.setSellerWorkPhone(seller.getOwner().getPhone());
        }
        facturaDetail.setSellerDirector(seller.getDirector());
        facturaDetail.setSellerAccountant(seller.getAccountant());
        facturaDetail.setSellerVatRegCode(null);
        facturaDetail.setSellerVatRegStatus(null);
        facturaDetail.setSellerBranchCode(stock.getBranch().getBranchNumber());
        facturaDetail.setSellerBranchName(stock.getBranch().getOfficialName());
    }

    private void setInvestment(FacturaDetail facturaDetail, Company buyer) {
        InvestmentObjectDTO investmentObject = getInvestmentObject(buyer.getTin());
        if (investmentObject != null) {
            facturaDetail.setInvestId(investmentObject.getObjectId());
            facturaDetail.setInvestName(investmentObject.getObjectName());
        }
    }

    private boolean enrichCompanyFromSoliq(FacturaCompanyDTO companyDTO, String tin) {
        if (companyDTO == null || !StringUtils.hasText(tin)) {
            return false;
        }

        try {
            CompanyCheckDTO companyCheckDTO = soliqClient.checkTin(tin);
            log.info("Soliq response: tin={}, director={}, accountant={}", tin,
                    companyCheckDTO != null ? companyCheckDTO.getDirector() : null,
                    companyCheckDTO != null ? companyCheckDTO.getAccountant() : null);

            if (companyCheckDTO == null) {
                return false;
            }

            if (StringUtils.hasText(companyCheckDTO.getDirector())) {
                companyDTO.setDirector(companyCheckDTO.getDirector());
            }

            if (StringUtils.hasText(companyCheckDTO.getAccountant())) {
                companyDTO.setAccountant(companyCheckDTO.getAccountant());
            }

            return StringUtils.hasText(companyDTO.getDirector());

        } catch (Exception e) {
            log.warn("Unable to get director/accountant from Soliq for TIN: {}",tin,e);
            return false;
        }
    }

    private void enrichCompaniesFromSoliq(Company seller, Company buyer) {
        enrichCompanyFromSoliq(seller, "продавца");
        enrichCompanyFromSoliq(buyer, "покупателя");
    }

    private void enrichCompanyFromSoliq(Company company, String type) {
        if (company == null || !StringUtils.hasText(company.getTin())) {
            throw validationService.badRequestExceptionThrow("Не удалось получить ИНН " + type).get();
        }

        try {
            CompanyCheckDTO companyCheckDTO = soliqClient.checkTin(company.getTin());

            if (companyCheckDTO == null || !StringUtils.hasText(companyCheckDTO.getDirector())) {
                throw validationService.badRequestExceptionThrow("Не удалось получить директора " + type).get();
            }
            company.setDirector(companyCheckDTO.getDirector());

        } catch (Exception e) {
            throw validationService.badRequestExceptionThrow("Не удалось получить данные " + type + " из Soliq").get();
        }
    }
}