package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.stock.*;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponseResult;
import uz.uzkassa.smartpossupply.dto.integration.XizmatResponseDTO;
import uz.uzkassa.smartpossupply.dto.payload.ReceiptSendPayload;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptSendDTO;
import uz.uzkassa.smartpossupply.dto.receipt.SmartposReceiptDTO;
import uz.uzkassa.smartpossupply.dto.receipt.SmartposReceiptItemDTO;
import uz.uzkassa.smartpossupply.dto.receipt.SmartposReceiptItemExtraDetailDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.integration.SmartposClient;
import uz.uzkassa.smartpossupply.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqReceiptProducer;
import uz.uzkassa.smartpossupply.repository.StockOutRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.ErrorDTO;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 31.01.2025
 */

/**
 * This class is responsible for sending receipts to smartpos and getting receipts from smartpos.
 * We have two realizations, We can create receipts and save them in our db. Or we can send them to smartpos and get them from smartpos.
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class SmartposReceiptService extends BaseService {

    StockOutRepository stockOutRepository;
    SmartposClient smartposClient;
    BillingServiceFeignClient billingServiceFeignClient;
    RabbitMqReceiptProducer rabbitMqReceiptProducer;
    AslBelgiClient aslBelgiClient;

    @Transactional
    public void send(ReceiptSendDTO sendDTO) {
        stockOutRepository.findById(sendDTO.getStockOutId())
                .map(stock -> {
                    this.validateCompany(getCurrentCompanyId());
                    if (StringUtils.isNotEmpty(stock.getReceiptUid())) {
                        throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.STOCK_OUT_ALREADY_HAS_RECEIPT)).get();
                    }
                    if (!StockStatus.COMPLETED.equals(stock.getStatus())) {
                        throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ONLY_STOCK_WITH_STATUS_COMPLETED_CAN_BE_SENT)).get();
                    }
                    PaymentType paymentType = stock.getPaymentType();
                    if (paymentType == null && stock.getOrder() != null) {
                        paymentType = stock.getOrder().getPaymentType();
                    }
                    if (paymentType == null || PaymentType.TRANSFER.equals(paymentType)) {
                        throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.PAYMENT_TYPE_NOT_SUPPORTED)).get();
                    }
                    List<XizmatResponseDTO> tariffs;
                    try {
                        tariffs = billingServiceFeignClient.getTariff(stock.getCompany().getTin());
                    } catch (Exception e) {
                        throw validationService.badRequestExceptionThrow(e.getMessage()).get();
                    }
                    if (!CollectionUtils.isEmpty(tariffs)) {
                        if (tariffs.stream().noneMatch(xizmatResponseDTO -> BillingTariffType.COMFORT.equals(xizmatResponseDTO.getTariffType()))) {
                            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_TARIFF_DOES_NOT_MATCH)).get();
                        }
                    } else {
                        throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_TARIFF_DOES_NOT_MATCH)).get();
                    }
                    CompanySettings xtraceSettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.XTRACE_ENABLED).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanySettings.class.getSimpleName(), "companyId", getCurrentCompanyId()));
                    String token = null;
                    if (stock.getProducts().stream().anyMatch(StockProduct::isHasMarking)) {
                        if (xtraceSettings.isEnabled()) {
                            token = getXTraceInfo().getApiKey();
                        } else {
                            token = validateAslBelgiAndGetAggregationInfo().getAslBelgiToken();
                        }
                    }
                    for (StockProduct stockProduct : stock.getProducts()) {
                        if (ProductType.Alcohol.equals(stockProduct.getProduct().getProductType()) || ProductType.AlcoholBlock.equals(stockProduct.getProduct().getProductType())) {
                            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ALCOHOL_PRODUCT_NOT_ALLOWED_TO_SELL)).get();
                        }
                    }
                    rabbitMqReceiptProducer.sendReceiptToQueue(new ReceiptSendPayload(stock.getId(), sendDTO.getBranchId(), token, xtraceSettings.isEnabled(), sendDTO.isCommittent()));
                    return stock;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", sendDTO.getStockOutId()));
    }

    @Transactional
    public void sendToSmartpos(ReceiptSendPayload payload) {
        stockOutRepository.findById(payload.getStockOutId())
                .map(stock -> {
                    SmartposReceiptDTO receipt = new SmartposReceiptDTO();
                    receipt.setUid(String.valueOf(System.currentTimeMillis()).concat(Optional.of(stock.getCreatorId().toString()).orElse("")));
                    receipt.setCompanyINN(stock.getCompany().getTin());
                    receipt.setStockOutId(stock.getId().toString());
                    receipt.setTotalCost(stock.getTotalPrice());
                    receipt.setTotalPaid(stock.getTotalPrice());
                    receipt.setTotalNds(stock.getTotalVatPrice());
                    receipt.setStatus(ReceiptStatus.DRAFT);
                    receipt.setBranchId(payload.getBranchId());
                    BigDecimal markCount = BigDecimal.ZERO;
                    for (StockProduct stockProduct : stock.getProducts()) {
                        if (!stockProduct.isHasMarking()) {
                            SmartposReceiptItemDTO receiptItem = new SmartposReceiptItemDTO();
                            receiptItem.setProductBarcode(stockProduct.getProduct().getBarcode());
                            receiptItem.setProductName(stockProduct.getProduct().getName());
                            receiptItem.setVatBarcode(stockProduct.getProduct().getVatBarcode());
                            if (stockProduct.getProduct().getCategory() != null) {
                                receiptItem.setCategoryName(stockProduct.getProduct().getCategory().getCatalogName());
                            }
                            if (stockProduct.getCompanyCommittent() != null && stockProduct.getCompanyCommittent().getCommittent() != null) {
                                receiptItem.setCommittentTin(stockProduct.getCompanyCommittent().getCommittent().getTin());
                            }
                            receiptItem.setUnitName(stockProduct.getUnit().getName());
                            receiptItem.setPrice(stockProduct.getSellingPrice());
                            receiptItem.setQty(stockProduct.getQty());
                            receiptItem.setAmount(stockProduct.getTotalSellingPrice());
                            receiptItem.setSalesPrice(stockProduct.getSellingPrice());
                            receiptItem.setNds(stockProduct.getTotalVatPrice());
                            receiptItem.setVatPrice(stockProduct.getVatPrice());
                            receiptItem.setBranchId(payload.getBranchId());
                            if (StringUtils.isNotEmpty(stockProduct.getPackageCode())) {
                                receiptItem.setExtraDetail(new SmartposReceiptItemExtraDetailDTO(stockProduct));
                            } else if (!StringUtils.isEmpty(stockProduct.getProduct().getPackageCode())) {
                                receiptItem.setExtraDetail(new SmartposReceiptItemExtraDetailDTO(stockProduct.getProduct()));
                            }
                            receiptItem.setNdsPercent(stockProduct.getVatRate());
                            receipt.addItem(receiptItem);
                        } else {
                            List<String> marks = new ArrayList<>();
                            if (!CollectionUtils.isEmpty(stockProduct.getStockProductAggregations())) {
                                List<String> codes = stockProduct.getStockProductAggregations().stream().map(StockProductAggregation::getCode).collect(Collectors.toList());
                                this.getMarks(marks, codes, stockProduct.getProduct().getProductType(), CodeType.AGGREGATION, payload.getToken(), payload.isXtraceEnabled());
                                if (marks.isEmpty()) {
                                    throw new BadRequestException("Внимание! При проверке найдены коды маркировки не принадлежащие вашей организации.");
                                }
                            }
                            if (!CollectionUtils.isEmpty(stockProduct.getStockProductAggregationBlocks())) {
                                List<String> codes = stockProduct.getStockProductAggregationBlocks().stream().map(StockProductAggregationBlock::getCode).collect(Collectors.toList());
                                this.getMarks(marks, codes, stockProduct.getProduct().getProductType(), CodeType.BLOCK, payload.getToken(), payload.isXtraceEnabled());
                                if (marks.isEmpty()) {
                                    throw new BadRequestException("Внимание! При проверке найдены коды маркировки не принадлежащие вашей организации.");
                                }
                            }
                            if (!CollectionUtils.isEmpty(stockProduct.getStockProductMarks())) {
                                marks.addAll(stockProduct.getStockProductMarks().stream().map(StockProductMark::getCode).toList());
                            }
                            SmartposReceiptItemDTO receiptItem = new SmartposReceiptItemDTO();
                            receiptItem.setProductBarcode(stockProduct.getProduct().getBarcode());
                            receiptItem.setProductName(stockProduct.getProduct().getName());
                            receiptItem.setVatBarcode(stockProduct.getProduct().getVatBarcode());
                            if (stockProduct.getProduct().getCategory() != null) {
                                receiptItem.setCategoryName(stockProduct.getProduct().getCategory().getCatalogName());
                            }
                            if (stockProduct.getCompanyCommittent() != null && stockProduct.getCompanyCommittent().getCommittent() != null) {
                                receiptItem.setCommittentTin(stockProduct.getCompanyCommittent().getCommittent().getTin());
                            }
                            receiptItem.setUnitName(stockProduct.getUnit().getName());
                            receiptItem.setPrice(stockProduct.getSellingPrice());
                            receiptItem.setQty(stockProduct.getQty());
                            receiptItem.setAmount(stockProduct.getTotalSellingPrice());
                            receiptItem.setSalesPrice(stockProduct.getSellingPrice());
                            receiptItem.setNds(stockProduct.getTotalVatPrice());
                            receiptItem.setVatPrice(stockProduct.getVatPrice());
                            receiptItem.setBranchId(payload.getBranchId());
                            if (StringUtils.isNotEmpty(stockProduct.getPackageCode())) {
                                receiptItem.setExtraDetail(new SmartposReceiptItemExtraDetailDTO(stockProduct));
                            } else if (!StringUtils.isEmpty(stockProduct.getProduct().getPackageCode())) {
                                receiptItem.setExtraDetail(new SmartposReceiptItemExtraDetailDTO(stockProduct.getProduct()));
                            }
                            if (!CollectionUtils.isEmpty(marks)) {
                                receiptItem.setMarks(new HashSet<>(marks));
                            } else {
                                throw new BadRequestException("Внимание! При проверке найдены коды маркировки не принадлежащие вашей организации.");
                            }
                            receiptItem.setNdsPercent(stockProduct.getVatRate());
                            receiptItem.setMarkQty(BigDecimal.valueOf(marks.size()));
                            markCount = markCount.add(BigDecimal.valueOf(marks.size()));
                            receipt.addItem(receiptItem);
                        }
                    }
                    receipt.setMarkCount(markCount);
                    List<String> receiptIds = new ArrayList<>();
                    int delay = 1;
                    for (SmartposReceiptDTO receiptDTO : divideReceipt(receipt)) {
                        receiptDTO.setStockOutId(receiptDTO.getStockOutId().concat("-").concat(String.valueOf(delay)));
                        rabbitMqReceiptProducer.sendDividedReceiptToQueue(receiptDTO, delay);
                        delay += 3;
                        receiptIds.add(receiptDTO.getUid());
                    }
                    stock.setReceiptUid(String.join(",", receiptIds));
                    stockOutRepository.save(stock);
                    return stock;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", payload.getStockOutId()));
    }

    @Transactional
    public void sendReceipt(SmartposReceiptDTO payload) {
        smartposClient.sendReceipt(payload);
    }

    public SmartposReceiptDTO getByUID(String uid) {
        try {
            ResponseEntity<String> response = smartposClient.getReceiptByUid(uid);
            try {
                return objectMapper.readValue(response.getBody(), SmartposReceiptDTO.class);
            } catch (JsonProcessingException e) {
                log.error("Writing object error: {}", e.getMessage());
                throw new BadRequestException(e.getMessage());
            }
        } catch (Exception e) {
            ErrorDTO errorDTO;
            try {
                log.info("Response: {}", e.getMessage());
                errorDTO = objectMapper.readValue(e.getMessage(), ErrorDTO.class);
            } catch (JsonProcessingException ex) {
                log.error("Reading object error: {}", ex.getMessage());
                throw new BadRequestException(ex.getMessage());
            }
            if (errorDTO != null && errorDTO.getStatus() == HttpStatus.NOT_FOUND.value()) {
                throw new NotFoundException(e.getMessage());
            }
            throw new BadRequestException(e.getMessage());
        }
    }

    public void getByStockOutId(Long stockOutId) {
        stockOutRepository.findById(stockOutId).ifPresentOrElse(stock -> {
            List<String> receiptUids = new ArrayList<>(Arrays.stream(stock.getReceiptUid().split(",")).toList());
            List<String> deletedReceiptUids = new ArrayList<>();
            if (!CollectionUtils.isEmpty(receiptUids)) {
                for (String receiptUid : receiptUids) {
                    try {
                        this.getByUID(receiptUid);
                    } catch (Exception e) {
                        if (e instanceof NotFoundException) {
                            deletedReceiptUids.add(receiptUid);
                        } else {
                            log.error("Error getting receipt by uid: {}", e.getMessage());
                            throw new BadRequestException(e.getMessage());
                        }
                    }
                }
                if (receiptUids.removeAll(deletedReceiptUids)) {
                    stock.setReceiptUid(String.join(",", receiptUids));
                }
            }
        }, () -> validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", stockOutId));
    }

    @Transactional
    public void delete(String uid) {
        stockOutRepository.findFirstByReceiptUidAndDeletedIsFalse(uid)
                .ifPresent(stock -> {
                    List<String> receiptUids = new ArrayList<>(Arrays.stream(stock.getReceiptUid().split(",")).toList());
                    receiptUids.remove(uid);
                    stock.setReceiptUid(String.join(",", receiptUids));
                    stockOutRepository.save(stock);
                });
    }

    private void getMarks(List<String> marks, List<String> codes, ProductType productType, CodeType codeType, String token, boolean isXtraceEnabled) {
        log.info("CODES: {}, codeType: {}, marks: {}", codes, codeType, marks);
        CisInfoResponseResult cisInfoResponseResult = isXtraceEnabled ? aslBelgiClient.checkPrivateInfo(token, codes, codeType, productType, true) : aslBelgiClient.checkAggregatedPublicList(token, codes, codeType, productType, true);
        if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
            for (CisInfoResponse cisInfoResponse : cisInfoResponseResult.getCisInfoResponses()) {
                if (cisInfoResponse != null && cisInfoResponse.getCisInfo() != null && TuronMarkStatus.isSuccess(cisInfoResponse.getCisInfo().getStatus())) {
                    CisInfo cisInfo = cisInfoResponse.getCisInfo();
                    if (PackageType.isPallet(PackageType.valueOf(cisInfo.getPackageType()))) {
                        List<String> children = null;
                        if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getChild())) {
                            children = cisInfoResponse.getCisInfo().getChild();
                        } else if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getCodes())) {
                            children = new ArrayList<>(cisInfoResponse.getCisInfo().getCodes().keySet());
                        }
                        if (!CollectionUtils.isEmpty(children)) {
                            CodeType childCodeType;
                            ProductType childProductType = null;
                            if (MarkUtils.isAggregation(children.getFirst())) {
                                childCodeType = CodeType.AGGREGATION;
                            } else if (MarkUtils.blockMatcher(children.getFirst())) {
                                childCodeType = CodeType.BLOCK;
                                if (ProductType.Alcohol.equals(productType)) {
                                    childProductType = ProductType.AlcoholBlock;
                                } else if (ProductType.Cigarette.equals(productType)) {
                                    childProductType = ProductType.CigaretteBlock;
                                } else if (ProductType.Beer.equals(productType)) {
                                    childProductType = ProductType.BeerBlock;
                                }
                            } else {
                                childCodeType = CodeType.MARK;
                            }
                            if (CodeType.MARK.equals(childCodeType)) {
                                marks.addAll(children);
                            } else {
                                this.getMarks(marks, children, childProductType != null ? childProductType : productType, childCodeType, token, isXtraceEnabled);
                            }
                        }
                    } else if (PackageType.isBox(PackageType.valueOf(cisInfo.getPackageType()))) {
                        List<String> children = null;
                        if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getChild())) {
                            children = cisInfoResponse.getCisInfo().getChild();
                        } else if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getCodes())) {
                            children = new ArrayList<>(cisInfoResponse.getCisInfo().getCodes().keySet());
                        }
                        if (!CollectionUtils.isEmpty(children)) {
                            CodeType childCodeType;
                            ProductType childProductType = null;
                            if (MarkUtils.blockMatcher(children.getFirst())) {
                                childCodeType = CodeType.BLOCK;
                                if (ProductType.Alcohol.equals(productType)) {
                                    childProductType = ProductType.AlcoholBlock;
                                } else if (ProductType.Cigarette.equals(productType)) {
                                    childProductType = ProductType.CigaretteBlock;
                                } else if (ProductType.Beer.equals(productType)) {
                                    childProductType = ProductType.BeerBlock;
                                }
                            } else {
                                childCodeType = CodeType.MARK;
                            }
                            if (CodeType.MARK.equals(childCodeType)) {
                                marks.addAll(children);
                            } else {
                                this.getMarks(marks, children, childProductType != null ? childProductType : productType, childCodeType, token, isXtraceEnabled);
                            }
                        }
                    } else if (PackageType.isBlock(PackageType.valueOf(cisInfo.getPackageType()))) {
                        List<String> children = null;
                        if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getChild())) {
                            children = cisInfoResponse.getCisInfo().getChild();
                        } else if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getCodes())) {
                            children = new ArrayList<>(cisInfoResponse.getCisInfo().getCodes().keySet());
                        }
                        if (!CollectionUtils.isEmpty(children)) {
                            marks.addAll(children);
                        }
                    } else {
                        marks.add(cisInfo.getRequestedCis());
                    }
                }
            }
        }
    }

    public List<SmartposReceiptDTO> divideReceipt(SmartposReceiptDTO smartposReceiptDTO) {
        long currentTimeInMillis = System.currentTimeMillis();
        List<SmartposReceiptDTO> receipts = new ArrayList<>();
        BigDecimal maxMarkQty = BigDecimal.valueOf(Constants.MAX_MARK_QTY_FOR_RECEIPT);
        BigDecimal markQty = smartposReceiptDTO.getMarkCount();
        SmartposReceiptDTO additionalReceipt = new SmartposReceiptDTO();
        if (markQty.compareTo(maxMarkQty) > 0) {
            for (SmartposReceiptItemDTO receiptDetailDTO : smartposReceiptDTO.getReceiptDetails()) {
//                if (maxMarkQty.compareTo(BigDecimal.valueOf(receiptDetailDTO.getMarks().size())) < 0 && receiptDetailDTO.getQty().compareTo(BigDecimal.valueOf(receiptDetailDTO.getMarks().size())) > 0) {
                if (receiptDetailDTO.getQty().compareTo(BigDecimal.valueOf(receiptDetailDTO.getMarks().size())) > 0) {
                    BigDecimal qty = Optional.of(receiptDetailDTO.getQty()).orElse(BigDecimal.ZERO).subtract(BigDecimal.valueOf(receiptDetailDTO.getMarks().size()));
                    SmartposReceiptItemDTO receiptItemDTO = new SmartposReceiptItemDTO();
                    receiptItemDTO.setUid(UUID.randomUUID());
                    receiptItemDTO.setProductBarcode(receiptDetailDTO.getProductBarcode());
                    receiptItemDTO.setProductName(receiptDetailDTO.getProductName());
                    receiptItemDTO.setVatBarcode(receiptDetailDTO.getVatBarcode());
                    receiptItemDTO.setCategoryName(receiptDetailDTO.getCategoryName());
                    receiptItemDTO.setCommittentTin(receiptDetailDTO.getCommittentTin());
                    receiptItemDTO.setUnitName(receiptDetailDTO.getUnitName());
                    receiptItemDTO.setPrice(receiptDetailDTO.getPrice());
                    receiptItemDTO.setSalesPrice(receiptDetailDTO.getSalesPrice());
                    receiptItemDTO.setVatPrice(receiptDetailDTO.getVatPrice());
                    receiptItemDTO.setBranchId(receiptDetailDTO.getBranchId());
                    receiptItemDTO.setExtraDetail(receiptDetailDTO.getExtraDetail());
                    receiptItemDTO.setQty(qty);
                    receiptItemDTO.setNdsPercent(receiptDetailDTO.getNdsPercent());
                    receiptItemDTO.setAmount(Optional.ofNullable(receiptDetailDTO.getSalesPrice()).orElse(BigDecimal.ZERO).multiply(Optional.of(qty).orElse(BigDecimal.ZERO)));
                    receiptItemDTO.setNds(Optional.ofNullable(receiptDetailDTO.getVatPrice()).orElse(BigDecimal.ZERO).multiply(qty));
                    additionalReceipt.addItem(receiptItemDTO);
                }
            }
            Map<UUID, SmartposReceiptItemDTO> map = splitReceiptItems(smartposReceiptDTO);

            List<Map.Entry<UUID, SmartposReceiptItemDTO>> sortedItems = new ArrayList<>(map.entrySet());
            sortedItems.sort(Comparator.comparing(e -> e.getValue().getQty(), Comparator.reverseOrder()));

            BigDecimal markCount = BigDecimal.ZERO;
            Set<UUID> processedKeys = new HashSet<>();

            for (Map.Entry<UUID, SmartposReceiptItemDTO> entry : sortedItems) {
                SmartposReceiptItemDTO currentItem = entry.getValue();

                if (processedKeys.contains(entry.getKey())) {
                    continue;
                }

                SmartposReceiptDTO receiptDTO = new SmartposReceiptDTO();
                receiptDTO.setUid(String.valueOf(System.currentTimeMillis()).concat(RandomStringUtils.randomNumeric(4, 4)));
                markCount = addItemToReceipt(receiptDTO, currentItem, markCount);
                processedKeys.add(entry.getKey());
                for (UUID key : map.keySet()) {
                    if (processedKeys.contains(key)) {
                        continue;
                    }

                    SmartposReceiptItemDTO item = map.get(key);
                    BigDecimal newCount = markCount.add(Optional.ofNullable(item.getMarkQty()).orElse(BigDecimal.ZERO));

                    if (newCount.compareTo(maxMarkQty) >= 0) {
                        break;
                    }

                    markCount = addItemToReceipt(receiptDTO, item, markCount);
                    processedKeys.add(key);
                }

                if (!receiptDTO.getReceiptDetails().isEmpty()) {
                    receipts.add(receiptDTO);
                }
                markCount = BigDecimal.ZERO;
            }
        } else {
            receipts.add(smartposReceiptDTO);
        }

        if (!CollectionUtils.isEmpty(additionalReceipt.getReceiptDetails())) {
            receipts.add(additionalReceipt);
        }
        receipts.forEach(receiptDTO -> {
            finalizeReceipt(smartposReceiptDTO, receiptDTO);
        });
        return receipts;
    }

    private Map<UUID, SmartposReceiptItemDTO> splitReceiptItems(SmartposReceiptDTO smartposReceiptDTO) {
        Map<UUID, SmartposReceiptItemDTO> map = new TreeMap<>();
        for (SmartposReceiptItemDTO item : smartposReceiptDTO.getReceiptDetails()) {
            if (item.getMarks().size() > Constants.MAX_MARK_QTY_FOR_RECEIPT) {
                List<List<String>> partitions = Lists.partition(new ArrayList<>(item.getMarks()), Constants.MAX_MARK_QTY_FOR_RECEIPT);
                for (List<String> set : partitions) {
                    SmartposReceiptItemDTO newItem = cloneReceiptItem(item, new HashSet<>(set));
                    map.put(newItem.getUid(), newItem);
                }
            } else {
                SmartposReceiptItemDTO newItem = cloneReceiptItem(item, new HashSet<>(item.getMarks()));
                map.put(newItem.getUid(), newItem);
            }
        }
        return map;
    }

    private SmartposReceiptItemDTO cloneReceiptItem(SmartposReceiptItemDTO original, Set<String> marks) {
        SmartposReceiptItemDTO clone = new SmartposReceiptItemDTO();
        clone.setUid(UUID.randomUUID());
        clone.setProductBarcode(original.getProductBarcode());
        clone.setProductName(original.getProductName());
        clone.setVatBarcode(original.getVatBarcode());
        clone.setCategoryName(original.getCategoryName());
        clone.setCommittentTin(original.getCommittentTin());
        clone.setUnitName(original.getUnitName());
        clone.setPrice(original.getPrice());
        clone.setSalesPrice(original.getSalesPrice());
        clone.setVatPrice(original.getVatPrice());
        clone.setBranchId(original.getBranchId());
        clone.setExtraDetail(original.getExtraDetail());
        clone.setMarks(marks);
        clone.setQty(BigDecimal.valueOf(marks.size()));
        clone.setNdsPercent(original.getNdsPercent());
        clone.setMarkQty(BigDecimal.valueOf(marks.size()));
        clone.setAmount(Optional.ofNullable(original.getSalesPrice()).orElse(BigDecimal.ZERO).multiply(clone.getQty()));
        clone.setNds(Optional.ofNullable(original.getVatPrice()).orElse(BigDecimal.ZERO).multiply(clone.getQty()));
        return clone;
    }

    private BigDecimal addItemToReceipt(SmartposReceiptDTO receipt, SmartposReceiptItemDTO item, BigDecimal currentCount) {
        receipt.addItem(item);
        return currentCount.add(Optional.ofNullable(item.getMarkQty()).orElse(BigDecimal.ZERO));
    }

    private void finalizeReceipt(SmartposReceiptDTO source, SmartposReceiptDTO receipt) {
        source.clone(receipt);
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalVatPrice = BigDecimal.ZERO;
        BigDecimal totalMarks = BigDecimal.ZERO;

        for (SmartposReceiptItemDTO item : receipt.getReceiptDetails()) {
            totalCost = totalCost.add(item.getAmount());
            totalVatPrice = totalVatPrice.add(item.getNds());
            totalMarks = totalMarks.add(Optional.ofNullable(item.getMarkQty()).orElse(BigDecimal.ZERO));
        }
        receipt.setTotalCost(totalCost);
        receipt.setTotalPaid(totalCost);
        receipt.setTotalNds(totalVatPrice);
        receipt.setMarkCount(totalMarks);
    }
}