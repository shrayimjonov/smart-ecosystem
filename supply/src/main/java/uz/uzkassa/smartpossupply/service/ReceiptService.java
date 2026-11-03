package uz.uzkassa.smartpossupply.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.receipt.Receipt;
import uz.uzkassa.smartpossupply.domain.receipt.ReceiptItem;
import uz.uzkassa.smartpossupply.domain.receipt.ReceiptPayment;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.stock.*;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponseResult;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.branch.BranchAddressLookupDTO;
import uz.uzkassa.smartpossupply.dto.product.TerminalProductListDTO;
import uz.uzkassa.smartpossupply.dto.receipt.*;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.filters.ReceiptFilter;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqReceiptProducer;
import uz.uzkassa.smartpossupply.repository.ReceiptItemRepository;
import uz.uzkassa.smartpossupply.repository.ReceiptPaymentRepository;
import uz.uzkassa.smartpossupply.repository.ReceiptRepository;
import uz.uzkassa.smartpossupply.repository.StockOutRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.20.2022 15:36
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class ReceiptService extends BaseService {

    ReceiptRepository receiptRepository;
    StockOutRepository stockOutRepository;
    AslBelgiClient aslBelgiClient;
    ReceiptPaymentRepository receiptPaymentRepository;
    ReceiptItemRepository receiptItemRepository;
    RabbitMqReceiptProducer rabbitMqReceiptProducer;

    @Transactional
    public Long create(TerminalReceiptDTO receiptDTO) {
        /*try {
            log.info("TERMINAL RECIEPT: {}", objectMapper.writeValueAsString(receiptDTO));
        } catch (JsonProcessingException e) {
            log.error("Object writing error: {}", e.getMessage());
        }*/
        this.validate(receiptDTO);
        return this.saveEntity(receiptDTO).getId();
    }

    private void validate(TerminalReceiptDTO receiptDTO) {
        if (receiptDTO.getFiscalDTO() == null) {
            receiptDTO.setFiscalDTO(new TerminalReceiptFiscalDTO());
        }
        if (StringUtils.isEmpty(receiptDTO.getFiscalDTO().getTerminalSerialNumber())) {
            receiptDTO.getFiscalDTO().setTerminalSerialNumber(SecurityUtils.getTerminalSerialNumber());
        }
        if (receiptDTO.getBranch() == null) {
            throw validationService.badRequestExceptionThrow("Branch is required").get();
        }
        if (ReceiptStatus.PAID == receiptDTO.getStatus() || ReceiptStatus.RETURNED == receiptDTO.getStatus()) {
            if (receiptDTO.getFiscalDTO() == null) {
                throw validationService.badRequestExceptionThrow("Empty fiscal info").get();
            }
//            if (StringUtils.isEmpty(receiptDTO.getFiscalDTO().getFiscalUrl())) {
//                throw validationService.badRequestExceptionThrow("Empty fiscal url").get();
//            }
            if (StringUtils.isEmpty(receiptDTO.getUid())) {
                throw validationService.badRequestExceptionThrow("Empty uid").get();
            }
        }
        if (StringUtils.isEmpty(receiptDTO.getFiscalDTO().getTerminalSerialNumber())) {
            throw validationService.badRequestExceptionThrow("Terminal serial number is required").get();
        }
        receiptDTO.getItems().forEach(receiptItemDTO -> {
            if (receiptItemDTO.getProduct() == null || receiptItemDTO.getProduct().getId() == null) {
                throw validationService.badRequestExceptionThrow("Product is required").get();
            }
        });
    }

    @Transactional
    public void batchCreate(List<TerminalReceiptDTO> receipts) {
        receipts.forEach(this::validate);
        for (TerminalReceiptDTO receipt : receipts) {
            rabbitMqReceiptProducer.sendToQueue(receipt);
        }
    }

    @Transactional
    public Receipt saveEntity(TerminalReceiptDTO receiptDTO) {
        if (receiptDTO.getStockOutId() == null && receiptDTO.getStockReturnId() == null) {
            receiptRepository.findFirstByUid(receiptDTO.getUid())
                    .ifPresent(receipt -> {
                        receiptDTO.setStockOutId(receipt.getStockOutId());
                        receiptDTO.setStockReturnId(receipt.getStockReturnId());
                    });
        }
        if (receiptDTO.getStatus() == null) {
            receiptDTO.setStatus(ReceiptStatus.DRAFT);
            receiptDTO.setUid(Optional.ofNullable(receiptDTO.getUid()).orElse(String.valueOf(System.currentTimeMillis())));
        }
        Company currentCompany = getCurrentCompany();
        Receipt receipt = receiptRepository.findFirstByUid(receiptDTO.getUid()).orElseGet(Receipt::new);

        receipt.setUid(receiptDTO.getUid());
        if (receiptDTO.getFiscalDTO() != null) {
            receipt.setFiscalSign(receiptDTO.getFiscalDTO().getFiscalSign());
            receipt.setFiscalUrl(receiptDTO.getFiscalDTO().getFiscalUrl());
            receipt.setTerminalSerialNumber(receiptDTO.getFiscalDTO().getTerminalSerialNumber());
            receipt.setTerminalFiscalNumber(receiptDTO.getFiscalDTO().getFiscalSerialNumber());
            receipt.setShiftNumber(receiptDTO.getFiscalDTO().getShiftNumber());
            receipt.setReceiptSeq(receiptDTO.getFiscalDTO().getReceiptSeq());
            receipt.setCreatedDate(Optional.ofNullable(receiptDTO.getFiscalDTO().getFiscalReceiptCreatedDate()).orElse(LocalDateTime.now()));
        }
        if (!CollectionUtils.isEmpty(receiptDTO.getItems())) {
            receipt.setPositionCount(receiptDTO.getItems().size());
        }
        receipt.setCompanyId(currentCompany.getId());
        receipt.setBranchId(receiptDTO.getBranch().getId());
        receipt.setStockOutId(receiptDTO.getStockOutId());
        receipt.setStockReturnId(receiptDTO.getStockReturnId());
        receipt.setCustomerId(receiptDTO.getCustomer().getId());
        receipt.setTotalCash(receiptDTO.getTotalCash());
        receipt.setTotalCard(receiptDTO.getTotalCard());
        receipt.setTotalPrice(receiptDTO.getTotalPrice());
        receipt.setTotalDiscount(receiptDTO.getTotalDiscount());
        receipt.setTotalPaid(receiptDTO.getTotalPaid());
        receipt.setStatus(Optional.ofNullable(receiptDTO.getStatus()).orElse(ReceiptStatus.DRAFT));
        receipt.setTotalVat(receiptDTO.getTotalVat());
        receipt.setAppVersion(receiptDTO.getAppVersion());
        receipt.setTotalQty(receiptDTO.getTotalQty());
        receiptRepository.save(receipt);
        BigDecimal totalCash = BigDecimal.ZERO;
        BigDecimal totalCard = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(receiptDTO.getPayments())) {
            List<Long> oldReceiptPaymentIds = new ArrayList<>(receipt.getPayments().stream().map(SimpleEntity::getId).toList());
            for (Map.Entry<String, BigDecimal> receiptPaymentMap : receiptDTO.getPayments().entrySet()) {
                ReceiptPaymentType receiptPaymentType = ReceiptPaymentType.valueOf(receiptPaymentMap.getKey());
                BigDecimal amount = receiptPaymentMap.getValue();
                ReceiptPayment receiptPayment;
                if (receipt.getId() == null) {
                    receiptPayment = new ReceiptPayment();
                } else {
                    receiptPayment = receiptPaymentRepository.findFirstByReceiptIdAndPaymentType(receipt.getId(), receiptPaymentType).orElseGet(ReceiptPayment::new);
                    if (receiptPayment.getId() != null) {
                        oldReceiptPaymentIds.remove(receiptPayment.getId());
                    }
                }
                receiptPayment.setPaymentType(receiptPaymentType);
                receiptPayment.setPrice(amount);
                receipt.addPayment(receiptPayment);
                if (ReceiptPaymentType.CASH == receiptPaymentType) {
                    totalCash = totalCash.add(amount);
                } else if (ReceiptPaymentType.CARD == receiptPaymentType) {
                    totalCard = totalCard.add(amount);
                } else if (ReceiptPaymentType.DISCOUNT == receiptPaymentType) {
                    totalDiscount = totalDiscount.add(amount);
                }
            }
            if (!CollectionUtils.isEmpty(oldReceiptPaymentIds)) {
                receiptPaymentRepository.deleteAllById(oldReceiptPaymentIds);
            }
            if (BigDecimal.ZERO.compareTo(totalCash) < 0) {
                receipt.setTotalCash(totalCash);
            }
            if (BigDecimal.ZERO.compareTo(totalCard) < 0) {
                receipt.setTotalCard(totalCard);
            }
            if (BigDecimal.ZERO.compareTo(totalDiscount) < 0) {
                receipt.setTotalDiscount(totalDiscount);
            }
            if (receipt.getPayments().size() == 1 && ReceiptPaymentType.DISCOUNT.equals(receipt.getPayments().getFirst().getPaymentType())) {
                ReceiptPayment receiptPayment = new ReceiptPayment();
                receiptPayment.setPaymentType(ReceiptPaymentType.CASH);
                receiptPayment.setPrice(BigDecimal.ZERO);
                receipt.addPayment(receiptPayment);
            }

        }
        if (!CollectionUtils.isEmpty(receiptDTO.getItems())) {
            List<Long> oldReceiptItemIds = new ArrayList<>(receipt.getItems().stream().map(SimpleEntity::getId).toList());
            for (TerminalReceiptItemDTO receiptItemDTO : receiptDTO.getItems()) {
                ReceiptItem receiptItem;
                if (receiptItemDTO.getId() != null) {
                    oldReceiptItemIds.remove(receiptItemDTO.getId());
                    receiptItem = receiptItemRepository.findById(receiptItemDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(ReceiptItem.class.getSimpleName(), "id", receiptItemDTO.getId()));
                } else {
                    receiptItem = new ReceiptItem();
                }
                receiptItem.setProductId(receiptItemDTO.getProduct().getId());
                receiptItem.setUnitId(receiptItemDTO.getProduct().getUnitId());
                receiptItem.setPrice(receiptItemDTO.getProduct().getPrice());
                receiptItem.setQty(receiptItemDTO.getQty());
                receiptItem.setTotalPrice(receiptItemDTO.getTotalPrice());
                receiptItem.setVatRate(receiptItemDTO.getVatRate());
                receiptItem.setVatPrice(receiptItemDTO.getVatPrice());
                receiptItem.setTotalVatPrice(receiptItemDTO.getTotalVatPrice());
                receiptItem.setMarks(receiptItemDTO.getMarks());
                receiptItem.setAggregationBlockCodes(receiptItemDTO.getAggregationBlockCodes());
                receiptItem.setComittentTin(receiptItemDTO.getComittentTin());
                receiptItem.setTotalDiscountPrice(receiptItemDTO.getTotalDiscountPrice());
                receiptItem.setDiscountPrice(receiptItemDTO.getDiscountPrice());
                receiptItem.setDiscountPercent(receiptItemDTO.getDiscountPercent());
                receipt.addItem(receiptItem);
            }
            if (!CollectionUtils.isEmpty(oldReceiptItemIds)) {
                receiptItemRepository.deleteAllById(oldReceiptItemIds);
            }
        }
        receiptRepository.save(receipt);
        return receipt;
    }

    public Page<ReceiptListDTO> getList(ReceiptFilter filter) {
        ResultList<Receipt> resultList = receiptRepository.getResultList(filter);
        List<ReceiptListDTO> result = resultList.getList()
                .stream().map(receipt -> {
                    ReceiptListDTO receiptListDTO = new ReceiptListDTO();
                    receiptListDTO.setId(receipt.getId());
                    receiptListDTO.setCreatedDate(receipt.getCreatedDate());
                    receiptListDTO.setStatus(receipt.getStatus());
                    receiptListDTO.setTotalPrice(receipt.getTotalPrice());
                    receiptListDTO.setTotalCash(receipt.getTotalCash());
                    receiptListDTO.setTotalCard(receipt.getTotalCard());
                    receiptListDTO.setTotalVat(receipt.getTotalVat());
                    receiptListDTO.setTerminalSerialNumber(receipt.getTerminalSerialNumber());
                    receiptListDTO.setShiftNumber(receipt.getShiftNumber());
                    receiptListDTO.setPayments(receipt.getPayments().stream().map(receiptPayment -> {
                        ReceiptPaymentListDTO receiptPaymentListDTO = new ReceiptPaymentListDTO();
                        receiptPaymentListDTO.setPaymentType(receiptPayment.getPaymentType());
                        receiptPaymentListDTO.setAmount(receiptPayment.getPrice());
                        return receiptPaymentListDTO;
                    }).toList());
                    return receiptListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public TerminalReceiptDTO get(Long id) {
        return receiptRepository
                .findById(id)
                .map(this::get)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Receipt.class.getSimpleName(), "id", id));
    }

    public CabinetReceiptDetailDTO getForCabinet(Long id) {
        return receiptRepository
                .findById(id)
                .map(receipt -> {
                    CabinetReceiptDetailDTO terminalReceiptDTO = new CabinetReceiptDetailDTO();
                    terminalReceiptDTO.setId(receipt.getId());
                    terminalReceiptDTO.setUid(receipt.getUid());
                    terminalReceiptDTO.setStatus(receipt.getStatus());
                    terminalReceiptDTO.setPositionCount(receipt.getPositionCount());
                    if (StringUtils.isNotEmpty(receipt.getFiscalUrl())) {
                        TerminalReceiptFiscalDTO fiscalDTO = new TerminalReceiptFiscalDTO();
                        fiscalDTO.setFiscalSign(receipt.getFiscalSign());
                        fiscalDTO.setFiscalUrl(receipt.getFiscalUrl());
                        fiscalDTO.setFiscalSerialNumber(receipt.getTerminalFiscalNumber());
                        fiscalDTO.setShiftNumber(receipt.getShiftNumber());
                        fiscalDTO.setReceiptSeq(receipt.getReceiptSeq());
                        fiscalDTO.setFiscalReceiptCreatedDate(receipt.getCreatedDate());
                        terminalReceiptDTO.setFiscalDTO(fiscalDTO);
                    }
                    if (!CollectionUtils.isEmpty(receipt.getPayments())) {
                        terminalReceiptDTO.setPayments(receipt.getPayments().stream().map(receiptPayment -> {
                            ReceiptPaymentListDTO receiptPaymentListDTO = new ReceiptPaymentListDTO();
                            receiptPaymentListDTO.setPaymentType(receiptPayment.getPaymentType());
                            receiptPaymentListDTO.setAmount(receiptPayment.getPrice());
                            return receiptPaymentListDTO;
                        }).toList());
                    }
                    terminalReceiptDTO.setCompany(receipt.getCompany().toCommonBaseDTO());
                    terminalReceiptDTO.setTotalPrice(receipt.getTotalPrice());
                    terminalReceiptDTO.setTotalPaid(receipt.getTotalPaid());
                    terminalReceiptDTO.setTotalVat(receipt.getTotalVat());
                    terminalReceiptDTO.setTotalQty(receipt.getTotalQty());
                    terminalReceiptDTO.setTotalCash(receipt.getTotalCash());
                    terminalReceiptDTO.setTotalDiscount(receipt.getTotalDiscount());
                    terminalReceiptDTO.setTotalCard(receipt.getTotalCard());
                    terminalReceiptDTO.setAppVersion(receipt.getAppVersion());
                    if (receipt.getBranchId() != null) {
                        BranchAddressLookupDTO branchAddressLookupDTO = new BranchAddressLookupDTO();
                        branchAddressLookupDTO.setId(receipt.getBranch().getId());
                        branchAddressLookupDTO.setName(receipt.getBranch().getName());
                        if (receipt.getBranch().getAddress() != null) {
                            branchAddressLookupDTO.setAddress(addressMapper.toDetailDTO(receipt.getBranch().getAddress()));
                        }
                        terminalReceiptDTO.setBranch(branchAddressLookupDTO);
                    }
                    if (receipt.getStockOutId() != null) {
                        terminalReceiptDTO.setStockOut(receipt.getStockOut().toStockCommonDTO());
                    }
                    if (receipt.getStockReturnId() != null) {
                        terminalReceiptDTO.setStockOut(receipt.getStockReturn().toStockCommonDTO());
                    }
                    terminalReceiptDTO.setCustomer(receipt.getCustomer().toCommonDTO());
                    if (receipt.getCreatorId() != null) {
                        terminalReceiptDTO.setCreator(receipt.getCreator().toCommonDTO());
                    }
                    for (ReceiptItem receiptItem : receipt.getItems()) {
                        TerminalReceiptItemDTO terminalReceiptItemDTO = new TerminalReceiptItemDTO();
                        terminalReceiptItemDTO.setId(receiptItem.getId());
                        terminalReceiptItemDTO.setComittentTin(receiptItem.getComittentTin());
                        terminalReceiptItemDTO.setQty(receiptItem.getQty());
                        terminalReceiptItemDTO.setVatRate(receiptItem.getVatRate());
                        terminalReceiptItemDTO.setVatPrice(receiptItem.getVatPrice());
                        terminalReceiptItemDTO.setTotalVatPrice(receiptItem.getTotalVatPrice());
                        terminalReceiptItemDTO.setTotalPrice(receiptItem.getTotalPrice());
                        terminalReceiptItemDTO.setMarks(receiptItem.getMarks());
                        terminalReceiptItemDTO.setDiscountPercent(receiptItem.getDiscountPercent());
                        terminalReceiptItemDTO.setDiscountPrice(receiptItem.getDiscountPrice());
                        terminalReceiptItemDTO.setTotalDiscountPrice(receiptItem.getTotalDiscountPrice());

                        TerminalProductListDTO product = new TerminalProductListDTO();
                        product.setId(receiptItem.getProductId());
                        product.setName(receiptItem.getProduct().getName());
                        product.setBarcode(receiptItem.getProduct().getBarcode());
                        product.setVatBarcode(receiptItem.getProduct().getVatBarcode());
                        product.setVat(receiptItem.getProduct().getVat());
                        product.setUnitId(receiptItem.getProduct().getUnitId());
                        product.setUnitName(receiptItem.getProduct().getUnit().getName());
                        product.setPrice(receiptItem.getPrice());
                        product.setPackageCode(Optional.ofNullable(receiptItem.getPackageCode()).orElseGet(() -> receiptItem.getProduct().getPackageCode()));
                        product.setPackageName(Optional.ofNullable(receiptItem.getPackageName()).orElseGet(() -> receiptItem.getProduct().getPackageName()));
                        terminalReceiptItemDTO.setProduct(product);
                        terminalReceiptDTO.addItem(terminalReceiptItemDTO);
                    }
                    return terminalReceiptDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Receipt.class.getSimpleName(), "id", id));
    }

    public List<TerminalReceiptDTO> getAllByStockOutId(Long id) {
        return stockOutRepository.findById(id).map(stock -> stock.getReceipts().stream().map(this::get).toList()).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
    }

    public TerminalReceiptDTO get(String uid) {
        return receiptRepository
                .getDetail(uid)
                .map(this::get)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Receipt.class.getSimpleName(), "uid", uid));
    }

    public TerminalReceiptDTO get(Receipt receipt) {
        TerminalReceiptDTO terminalReceiptDTO = new TerminalReceiptDTO();
        terminalReceiptDTO.setId(receipt.getId());
        terminalReceiptDTO.setUid(receipt.getUid());
        terminalReceiptDTO.setStatus(receipt.getStatus());
        terminalReceiptDTO.setPositionCount(receipt.getPositionCount());
        if (StringUtils.isNotEmpty(receipt.getFiscalUrl())) {
            TerminalReceiptFiscalDTO fiscalDTO = new TerminalReceiptFiscalDTO();
            fiscalDTO.setFiscalSign(receipt.getFiscalSign());
            fiscalDTO.setFiscalUrl(receipt.getFiscalUrl());
            fiscalDTO.setFiscalSerialNumber(receipt.getTerminalFiscalNumber());
            fiscalDTO.setShiftNumber(receipt.getShiftNumber());
            fiscalDTO.setReceiptSeq(receipt.getReceiptSeq());
            fiscalDTO.setFiscalReceiptCreatedDate(receipt.getCreatedDate());
            terminalReceiptDTO.setFiscalDTO(fiscalDTO);
        }
        if (!CollectionUtils.isEmpty(receipt.getPayments())) {
            Map<String, BigDecimal> payments = new LinkedHashMap<>();
            for (ReceiptPayment payment : receipt.getPayments()) {
                payments.put(payment.getPaymentType().getCode(), payment.getPrice());
            }
            terminalReceiptDTO.setPayments(payments);
        }
        terminalReceiptDTO.setCompany(receipt.getCompany().toCommonBaseDTO());
        terminalReceiptDTO.setTotalPrice(receipt.getTotalPrice());
        terminalReceiptDTO.setTotalPaid(receipt.getTotalPaid());
        terminalReceiptDTO.setTotalVat(receipt.getTotalVat());
        terminalReceiptDTO.setTotalQty(receipt.getTotalQty());
        terminalReceiptDTO.setTotalCash(receipt.getTotalCash());
        terminalReceiptDTO.setTotalDiscount(receipt.getTotalDiscount());
        terminalReceiptDTO.setTotalCard(receipt.getTotalCard());
        terminalReceiptDTO.setAppVersion(receipt.getAppVersion());
        if (receipt.getBranchId() != null) {
            BranchAddressLookupDTO branchAddressLookupDTO = new BranchAddressLookupDTO();
            branchAddressLookupDTO.setId(receipt.getBranch().getId());
            branchAddressLookupDTO.setName(receipt.getBranch().getName());
            if (receipt.getBranch().getAddress() != null) {
                branchAddressLookupDTO.setAddress(addressMapper.toDetailDTO(receipt.getBranch().getAddress()));
            }
            terminalReceiptDTO.setBranch(branchAddressLookupDTO);
        }
        terminalReceiptDTO.setStockOutId(receipt.getStockOutId());
        terminalReceiptDTO.setStockReturnId(receipt.getStockReturnId());
        if (!CollectionUtils.isEmpty(receipt.getPayments())) {
            Map<String, BigDecimal> payments = new LinkedHashMap<>();
            for (ReceiptPayment payment : receipt.getPayments()) {
                payments.put(payment.getPaymentType().getCode(), payment.getPrice());
            }
            terminalReceiptDTO.setPayments(payments);
        }
        terminalReceiptDTO.setCustomer(receipt.getCustomer().toCommonDTO());
        if (receipt.getCreatorId() != null) {
            terminalReceiptDTO.setCreator(receipt.getCreator().toCommonDTO());
        }
        for (ReceiptItem receiptItem : receipt.getItems()) {
            TerminalReceiptItemDTO terminalReceiptItemDTO = new TerminalReceiptItemDTO();
            terminalReceiptItemDTO.setId(receiptItem.getId());
            terminalReceiptItemDTO.setComittentTin(receiptItem.getComittentTin());
            terminalReceiptItemDTO.setQty(receiptItem.getQty());
            terminalReceiptItemDTO.setVatRate(receiptItem.getVatRate());
            terminalReceiptItemDTO.setVatPrice(receiptItem.getVatPrice());
            terminalReceiptItemDTO.setTotalVatPrice(receiptItem.getTotalVatPrice());
            terminalReceiptItemDTO.setTotalPrice(receiptItem.getTotalPrice());
            terminalReceiptItemDTO.setMarks(receiptItem.getMarks());
            terminalReceiptItemDTO.setAggregationBlockCodes(receiptItem.getAggregationBlockCodes());
            terminalReceiptItemDTO.setDiscountPercent(receiptItem.getDiscountPercent());
            terminalReceiptItemDTO.setDiscountPrice(receiptItem.getDiscountPrice());
            terminalReceiptItemDTO.setTotalDiscountPrice(receiptItem.getTotalDiscountPrice());

            TerminalProductListDTO product = new TerminalProductListDTO();
            product.setId(receiptItem.getProductId());
            product.setName(receiptItem.getProduct().getName());
            product.setBarcode(receiptItem.getProduct().getBarcode());
            product.setVatBarcode(receiptItem.getProduct().getVatBarcode());
            product.setVat(receiptItem.getProduct().getVat());
            product.setUnitId(receiptItem.getProduct().getUnitId());
            product.setUnitName(receiptItem.getProduct().getUnit().getName());
            product.setPrice(receiptItem.getPrice());
            product.setPackageCode(Optional.ofNullable(receiptItem.getPackageCode()).orElseGet(() -> receiptItem.getProduct().getPackageCode()));
            product.setPackageName(Optional.ofNullable(receiptItem.getPackageName()).orElseGet(() -> receiptItem.getProduct().getPackageName()));
            product.setBlockProducts(receiptItem.getProduct().getBlockProducts());
            terminalReceiptItemDTO.setProduct(product);
            terminalReceiptDTO.addItem(terminalReceiptItemDTO);
        }
        return terminalReceiptDTO;
    }

    @Transactional
    public void convertFromStockReturn(Long stockReturnId, Boolean ignoreForeignMark) {
        if (receiptRepository.existsByStockReturnIdAndDeletedIsFalse(stockReturnId)) {
            throw validationService.badRequestExceptionThrow("Stock return already has receipt").get();
        }
        this.convertFromStock(stockReturnId, ignoreForeignMark);
    }

    @Transactional
    public void convertFromStockOut(Long stockReturnId, Boolean ignoreForeignMark) {
        if (receiptRepository.existsByStockOutIdAndDeletedIsFalse(stockReturnId)) {
            throw validationService.badRequestExceptionThrow("Stock out already has receipt").get();
        }
        this.convertFromStock(stockReturnId, ignoreForeignMark);
    }

    private void convertFromStock(Long stockId, Boolean ignoreForeignMark) {
        boolean throwException = ignoreForeignMark == null || !ignoreForeignMark;
        stockOutRepository.getDetail(stockId)
                .ifPresentOrElse(stock -> {
/*                    if (PaymentType.TRANSFER.equals(stock.getPaymentType())) {
                        throw validationService.badRequestExceptionThrow("Payment type transfer not supported").get();
                    }*/
                    if (StockStatus.COMPLETED != stock.getStatus()) {
                        throw validationService.badRequestExceptionThrow("Stock status must be completed").get();
                    }
                    TerminalReceiptDTO receipt = new TerminalReceiptDTO();
                    receipt.setUid(String.valueOf(System.currentTimeMillis()).concat(Optional.of(stock.getCreatorId().toString()).orElse("")));
                    if (stock.getProducts() != null) {
                        receipt.setPositionCount(stock.getProducts().size());
                    }
                    receipt.setCompany(stock.getCompany().toCommonBaseDTO());
                    receipt.setPayments(new LinkedHashMap<>());
                    receipt.setTotalPrice(stock.getTotalPrice());
                    receipt.setTotalVat(stock.getTotalVatPrice());
                    receipt.setTotalQty(stock.getTotalQty());
                    receipt.setTotalDiscount(stock.getTotalDiscountPrice());
                    if (stock.getBranchId() != null) {
                        BranchAddressLookupDTO branchAddressLookupDTO = new BranchAddressLookupDTO();
                        branchAddressLookupDTO.setId(stock.getBranch().getId());
                        branchAddressLookupDTO.setName(stock.getBranch().getName());
                        if (stock.getBranch().getAddress() != null) {
                            branchAddressLookupDTO.setAddress(addressMapper.toDetailDTO(stock.getBranch().getAddress()));
                        }
                        receipt.setBranch(branchAddressLookupDTO);
                    }
                    receipt.setStatus(ReceiptStatus.DRAFT);
                    if (TransferType.RETURN == stock.getTransferType()) {
                        receipt.setStockReturnId(stockId);
                    } else {
                        receipt.setStockOutId(stockId);
                    }
                    receipt.setCustomer(stock.getCustomer().toCommonDTO());
                    BigDecimal markCount = BigDecimal.ZERO;
                    for (StockProduct stockProduct : stock.getProducts()) {
                        if (!stockProduct.isHasMarking()) {
                            TerminalReceiptItemDTO receiptItem = convertToReceiptItem(stockProduct);
                            receipt.addItem(receiptItem);
                        } else {
                            CompanySettings xtraceSettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.XTRACE_ENABLED).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanySettings.class.getSimpleName(), "companyId", getCurrentCompanyId()));
                            String token = xtraceSettings.isEnabled() ? getXTraceInfo().getApiKey() : validateAslBelgiAndGetAggregationInfo().getAslBelgiToken();
                            List<String> marks = new ArrayList<>();
                            List<String> aggregationBlockCodes = new ArrayList<>();
                            if (!CollectionUtils.isEmpty(stockProduct.getStockProductAggregations())) {
                                List<String> codes = stockProduct.getStockProductAggregations().stream().map(StockProductAggregation::getCode).collect(Collectors.toList());
                                this.getMarks(marks, codes, stockProduct.getProduct().getProductType(), CodeType.AGGREGATION, token, xtraceSettings.isEnabled());
                                if (marks.isEmpty()) {
                                    if (throwException) {
                                        throw new BadRequestException("Внимание! При проверке найдены коды маркировки не принадлежащие вашей организации.");
                                    }
                                }
                            }
                            if (!CollectionUtils.isEmpty(stockProduct.getStockProductAggregationBlocks())) {
                                aggregationBlockCodes = stockProduct.getStockProductAggregationBlocks().stream().map(StockProductAggregationBlock::getCode).toList();

//                                this.getMarks(marks, codes, stockProduct.getProduct().getProductType(), CodeType.BLOCK, token, xtraceSettings.isEnabled());
//                                if (marks.isEmpty()) {
//                                    if (throwException) {
//                                        throw new BadRequestException("Внимание! При проверке найдены коды маркировки не принадлежащие вашей организации.");
//                                    }
//                                }
                            }
                            if (!CollectionUtils.isEmpty(stockProduct.getStockProductMarks())) {
                                marks.addAll(stockProduct.getStockProductMarks().stream().map(StockProductMark::getCode).toList());
                            }
                            TerminalReceiptItemDTO receiptItem = convertToReceiptItem(stockProduct);
                            if (!CollectionUtils.isEmpty(marks)) {
                                receiptItem.setMarks(new HashSet<>(marks));
                            } else {
                                if (throwException) {
                                    throw new BadRequestException("Внимание! При проверке найдены коды маркировки не принадлежащие вашей организации.");
                                }
                            }
                            receiptItem.getAggregationBlockCodes().addAll(aggregationBlockCodes);
                            receipt.addItem(receiptItem);
                            markCount = markCount.add(BigDecimal.valueOf(marks.size()));
                        }
                    }
                    receipt.setMarkCount(markCount);
                    stock.getReceipts().clear();
                    log.info("TERMINAL RECEIPT: {}", receipt);

                    List<TerminalReceiptDTO> receipts;
                    if (stock.getPaymentType() == PaymentType.TRANSFER) {
                        receipts = List.of(receipt);
                    } else {
                        receipts = this.divideReceipt(receipt);
                    }
                    for (TerminalReceiptDTO terminalReceiptDTO : receipts) {
                        Receipt savedReceipt = this.saveEntity(terminalReceiptDTO);
                        stock.getReceipts().add(savedReceipt);
                    }
                    stockOutRepository.save(stock);
                }, () -> {
                    throw validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", stockId).get();
                });
    }

    private TerminalReceiptItemDTO convertToReceiptItem(StockProduct stockProduct) {
        TerminalReceiptItemDTO receiptItem = new TerminalReceiptItemDTO();
        receiptItem.setQty(stockProduct.getQty());
        receiptItem.setVatRate(stockProduct.getVatRate());
        receiptItem.setVatPrice(stockProduct.getVatPrice());
        receiptItem.setTotalPrice(stockProduct.getTotalSellingPrice());
        receiptItem.setTotalVatPrice(stockProduct.getTotalVatPrice());
        receiptItem.setDiscountPercent(stockProduct.getDiscountPercent());
        receiptItem.setDiscountPrice(stockProduct.getDiscountPrice());
        receiptItem.setTotalDiscountPrice(stockProduct.getTotalDiscountPrice());
        if (stockProduct.getCompanyCommittent() != null && stockProduct.getCompanyCommittent().getCommittent() != null) {
            receiptItem.setComittentTin(stockProduct.getCompanyCommittent().getCommittent().getTin());
        }
        TerminalProductListDTO product = new TerminalProductListDTO();
        product.setId(stockProduct.getProductId());
        product.setName(stockProduct.getProduct().getName());
        product.setBarcode(stockProduct.getProduct().getBarcode());
        product.setVatBarcode(stockProduct.getProduct().getVatBarcode());
        product.setVat(stockProduct.getProduct().getVat());
        product.setUnitId(stockProduct.getProduct().getUnitId());
        product.setUnitName(stockProduct.getProduct().getUnit().getName());
        product.setPrice(stockProduct.getSellingPrice());
        product.setPackageCode(Optional.ofNullable(stockProduct.getPackageCode()).orElse(stockProduct.getProduct().getPackageCode()));
        product.setPackageName(Optional.ofNullable(stockProduct.getPackageName()).orElse(stockProduct.getProduct().getPackageName()));
        receiptItem.setProduct(product);
        return receiptItem;
    }

    public ReceiptStatisticDTO getStatistics(ReceiptFilter filter) {
        return receiptRepository.getStatistics(filter);
    }

    /**
     * It gets codes recursively from top to bottom.
     */
    private void getMarks(List<String> marks, List<String> codes, ProductType productType, CodeType codeType, String token, boolean isXtraceEnabled) {
        log.info("CODES: {}, codeType: {}, marks: {}", codes, codeType, marks);
        CisInfoResponseResult cisInfoResponseResult = isXtraceEnabled
                ? aslBelgiClient.checkPrivateInfo(token, codes, codeType, productType)
                : aslBelgiClient.checkAggregatedPublicList(token, codes, codeType, productType);
        if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
            for (CisInfoResponse cisInfoResponse : cisInfoResponseResult.getCisInfoResponses()) {
                if (cisInfoResponse != null && cisInfoResponse.getCisInfo() != null && (TuronMarkStatus.isSuccess(cisInfoResponse.getCisInfo().getStatus()) || StringUtils.isNotEmpty(cisInfoResponse.getCisInfo().getErrorMessage()))) {
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

    /**
     * Dividing receipt method.
     * If receipt contains more than MAX_MARK_QTY_FOR_RECEIPT marks, it starts to divide receipt.
     */
    private List<TerminalReceiptDTO> divideReceipt(TerminalReceiptDTO terminalReceiptDTO) {
        long currentTimeInMillis = System.currentTimeMillis();
        List<TerminalReceiptDTO> receipts = new ArrayList<>();
        BigDecimal maxMarkQty = BigDecimal.valueOf(Constants.MAX_MARK_QTY_FOR_RECEIPT);
        BigDecimal markQty = terminalReceiptDTO.getMarkCount();
        TerminalReceiptDTO additionalReceipt = new TerminalReceiptDTO();
        if (markQty.compareTo(maxMarkQty) > 0) {
            for (TerminalReceiptItemDTO receiptDetailDTO : terminalReceiptDTO.getItems()) {
                if (maxMarkQty.compareTo(BigDecimal.valueOf(receiptDetailDTO.getMarks().size())) < 0 && receiptDetailDTO.getQty().compareTo(BigDecimal.valueOf(receiptDetailDTO.getMarks().size())) > 0) {
                    BigDecimal qty = Optional.of(receiptDetailDTO.getQty()).orElse(BigDecimal.ZERO).subtract(BigDecimal.valueOf(receiptDetailDTO.getMarks().size()));
                    TerminalReceiptItemDTO receiptItemDTO = new TerminalReceiptItemDTO();
                    receiptItemDTO.setUid(UUID.randomUUID());
                    TerminalProductListDTO product = new TerminalProductListDTO();
                    product.setId(receiptDetailDTO.getProduct().getId());
                    product.setName(receiptDetailDTO.getProduct().getName());
                    product.setBarcode(receiptDetailDTO.getProduct().getBarcode());
                    product.setVatBarcode(receiptDetailDTO.getProduct().getVatBarcode());
                    product.setVat(receiptDetailDTO.getProduct().getVat());
                    product.setUnitId(receiptDetailDTO.getProduct().getUnitId());
                    product.setUnitName(receiptDetailDTO.getProduct().getUnitName());
                    product.setPrice(receiptDetailDTO.getProduct().getPrice());
                    product.setPackageCode(receiptDetailDTO.getProduct().getPackageCode());
                    product.setPackageName(receiptDetailDTO.getProduct().getPackageName());
                    receiptItemDTO.setVatPrice(receiptDetailDTO.getVatPrice());
                    receiptItemDTO.setProduct(product);
                    receiptItemDTO.setQty(qty);
                    receiptItemDTO.setDiscountPrice(receiptDetailDTO.getDiscountPrice());
                    receiptItemDTO.setDiscountPercent(receiptDetailDTO.getDiscountPercent());
                    receiptItemDTO.setTotalDiscountPrice(receiptDetailDTO.getTotalDiscountPrice());
                    receiptItemDTO.setVatRate(receiptDetailDTO.getVatRate());
                    receiptItemDTO.setTotalPrice(Optional.ofNullable(receiptDetailDTO.getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.of(qty).orElse(BigDecimal.ZERO)));
                    receiptItemDTO.setTotalVatPrice(Optional.ofNullable(receiptItemDTO.getVatPrice()).orElse(BigDecimal.ZERO).multiply(Optional.of(qty).orElse(BigDecimal.ZERO)));
                    additionalReceipt.addItem(receiptItemDTO);
                }
            }
            Map<UUID, TerminalReceiptItemDTO> map = splitReceiptItems(terminalReceiptDTO);

            List<Map.Entry<UUID, TerminalReceiptItemDTO>> sortedItems = new ArrayList<>(map.entrySet());
            sortedItems.sort(Comparator.comparing(e -> e.getValue().getQty(), Comparator.reverseOrder()));

            BigDecimal markCount = BigDecimal.ZERO;
            Set<UUID> processedKeys = new HashSet<>();

            for (Map.Entry<UUID, TerminalReceiptItemDTO> entry : sortedItems) {
                TerminalReceiptItemDTO currentItem = entry.getValue();

                if (processedKeys.contains(entry.getKey())) {
                    continue;
                }

                TerminalReceiptDTO receiptDTO = new TerminalReceiptDTO();
                receiptDTO.setUid(String.valueOf(System.currentTimeMillis()).concat(RandomStringUtils.randomNumeric(4, 4)));
                markCount = addItemToReceipt(receiptDTO, currentItem, markCount);
                processedKeys.add(entry.getKey());

                for (UUID key : map.keySet()) {
                    if (processedKeys.contains(key)) {
                        continue;
                    }

                    TerminalReceiptItemDTO item = map.get(key);
                    BigDecimal newCount = markCount.add(Optional.ofNullable(item.getMarkQty()).orElse(BigDecimal.ZERO));

                    if (newCount.compareTo(maxMarkQty) > 0) {
                        break;
                    }
                    if (newCount.add(markCount).compareTo(maxMarkQty) > 0) {
                        break;
                    }

                    markCount = addItemToReceipt(receiptDTO, item, markCount);
                    processedKeys.add(key);
                }

                if (!receiptDTO.getItems().isEmpty()) {
                    receipts.add(receiptDTO);
                }
                markCount = BigDecimal.ZERO;
            }
        } else {
            receipts.add(terminalReceiptDTO);
        }

        if (!CollectionUtils.isEmpty(additionalReceipt.getItems())) {
            receipts.add(additionalReceipt);
        }
        receipts.forEach(receiptDTO -> {
            finalizeReceipt(terminalReceiptDTO, receiptDTO);
        });
        return receipts;
    }

    private Map<UUID, TerminalReceiptItemDTO> splitReceiptItems(TerminalReceiptDTO smartposReceiptDTO) {
        Map<UUID, TerminalReceiptItemDTO> map = new TreeMap<>();
        for (TerminalReceiptItemDTO item : smartposReceiptDTO.getItems()) {
            if (item.getMarks().size() > Constants.MAX_MARK_QTY_FOR_RECEIPT) {
                List<List<String>> partitions = Lists.partition(new ArrayList<>(item.getMarks()), Constants.MAX_MARK_QTY_FOR_RECEIPT);
                for (List<String> set : partitions) {
                    TerminalReceiptItemDTO newItem = cloneReceiptItem(item, new HashSet<>(set));
                    map.put(newItem.getUid(), newItem);
                }
            } else {
                item.setUid(UUID.randomUUID());
                map.put(item.getUid(), item);
            }
        }
        return map;
    }

    private TerminalReceiptItemDTO cloneReceiptItem(TerminalReceiptItemDTO original, Set<String> marks) {
        TerminalReceiptItemDTO clone = new TerminalReceiptItemDTO();
        clone.setUid(UUID.randomUUID());
        TerminalProductListDTO product = new TerminalProductListDTO();
        product.setId(original.getProduct().getId());
        product.setName(original.getProduct().getName());
        product.setBarcode(original.getProduct().getBarcode());
        product.setVatBarcode(original.getProduct().getVatBarcode());
        product.setVat(original.getProduct().getVat());
        product.setUnitId(original.getProduct().getUnitId());
        product.setUnitName(original.getProduct().getUnitName());
        product.setPrice(original.getProduct().getPrice());
        product.setPackageCode(original.getProduct().getPackageCode());
        product.setPackageName(original.getProduct().getPackageName());
        clone.setProduct(product);

        clone.setVatPrice(original.getVatPrice());
        clone.setMarks(marks);
        clone.setQty(BigDecimal.valueOf(marks.size()));
        clone.setVatRate(original.getVatRate());
        clone.setMarkQty(BigDecimal.valueOf(marks.size()));
        clone.setDiscountPercent(original.getDiscountPercent());
        clone.setTotalDiscountPrice(Optional.ofNullable(original.getDiscountPrice()).orElse(BigDecimal.ZERO).multiply(clone.getQty()));
        clone.setDiscountPrice(original.getDiscountPrice());
        clone.setTotalPrice(Optional.ofNullable(original.getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(clone.getQty()));
        clone.setTotalVatPrice(Optional.ofNullable(clone.getVatPrice()).orElse(BigDecimal.ZERO).multiply(clone.getQty()));
        return clone;
    }

    private BigDecimal addItemToReceipt(TerminalReceiptDTO receipt, TerminalReceiptItemDTO item, BigDecimal currentCount) {
        receipt.addItem(item);
        return currentCount.add(BigDecimal.valueOf(item.getMarks().size()));
    }

    private void finalizeReceipt(TerminalReceiptDTO source, TerminalReceiptDTO receipt) {
        source.clone(receipt);
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalVatPrice = BigDecimal.ZERO;
        BigDecimal totalMarks = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (TerminalReceiptItemDTO item : receipt.getItems()) {
            totalQty = totalQty.add(item.getQty());
            totalCost = totalCost.add(item.getTotalPrice());
            totalVatPrice = totalVatPrice.add(item.getTotalVatPrice());
            totalMarks = totalMarks.add(Optional.ofNullable(item.getMarkQty()).orElse(BigDecimal.ZERO));
            totalDiscount = totalDiscount.add(Optional.ofNullable(item.getTotalDiscountPrice()).orElse(BigDecimal.ZERO));
        }
        receipt.setTotalPrice(totalCost);
        receipt.setTotalQty(totalQty);
        receipt.setTotalPaid(totalCost.subtract(totalDiscount));
        receipt.setTotalVat(totalVatPrice);
        receipt.setMarkCount(totalMarks);
        if (BigDecimal.ZERO.compareTo(totalDiscount) < 0) {
            receipt.setTotalDiscount(totalDiscount);
        }
    }

    public TerminalDashboardStatisticsDTO getDashboardStatistics(ReceiptFilter filter) {
        filter.setCreatorId(SecurityUtils.getCurrentUserId());
        return receiptRepository.getTerminalDashboardStatistics(filter);
    }

    @Transactional
    public void updatePayment(Long stockOutId, TerminalReceiptUpdatePaymentDTO updatePaymentDTO) {
        stockOutRepository.findById(stockOutId).ifPresentOrElse(stock -> {
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (BigDecimal amount : updatePaymentDTO.getPayments().values()) {
                totalPrice = totalPrice.add(amount);
            }
            if (stock.getTotalPrice().subtract(Optional.ofNullable(stock.getTotalDiscountPrice()).orElse(BigDecimal.ZERO)).compareTo(totalPrice) != 0) {
                throw validationService.badRequestExceptionThrow("Total price does not match with stock. ").get();
            }
            Map<ReceiptPaymentType, TerminalReceiptPaymentTypeDTO> payments = new LinkedHashMap<>();
            updatePaymentDTO.getPayments().forEach((receiptPaymentType, amount) -> payments.put(receiptPaymentType, new TerminalReceiptPaymentTypeDTO(amount)));
            for (Receipt receipt : stock.getReceipts()) {
                TerminalReceiptDTO terminalReceiptDTO = get(receipt);
                BigDecimal receiptTotalPrice = Optional.ofNullable(receipt.getTotalPrice()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(receipt.getTotalDiscount()).orElse(BigDecimal.ZERO));
                Map<String, BigDecimal> receiptPayments = new LinkedHashMap<>();
                if (receipt.getTotalDiscount() != null && BigDecimal.ZERO.compareTo(receipt.getTotalDiscount()) < 0) {
                    receiptPayments.put(ReceiptPaymentType.DISCOUNT.getCode(), receipt.getTotalDiscount());
                }
                for (Map.Entry<ReceiptPaymentType, TerminalReceiptPaymentTypeDTO> entry : payments.entrySet()) {
                    ReceiptPaymentType receiptPaymentType = entry.getKey();
                    TerminalReceiptPaymentTypeDTO terminalReceiptPaymentTypeDTO = entry.getValue();
                    if (BigDecimal.ZERO.compareTo(receiptTotalPrice) < 0 && terminalReceiptPaymentTypeDTO.isValid()) {
                        if (receiptTotalPrice.compareTo(terminalReceiptPaymentTypeDTO.getAmount()) == 0) {
                            receiptPayments.put(receiptPaymentType.getCode(), terminalReceiptPaymentTypeDTO.getAmount());
                            terminalReceiptPaymentTypeDTO.setAmount(BigDecimal.ZERO);
                            terminalReceiptPaymentTypeDTO.setValid(false);
                            terminalReceiptDTO.setPayments(receiptPayments);
                            break;
                        } else if (receiptTotalPrice.compareTo(terminalReceiptPaymentTypeDTO.getAmount()) < 0) {
                            receiptPayments.put(receiptPaymentType.getCode(), receiptTotalPrice);
                            terminalReceiptPaymentTypeDTO.setAmount(terminalReceiptPaymentTypeDTO.getAmount().subtract(receiptTotalPrice));
                            terminalReceiptPaymentTypeDTO.setValid(true);
                            terminalReceiptDTO.setPayments(receiptPayments);
                            break;
                        } else {
                            receiptPayments.put(receiptPaymentType.getCode(), terminalReceiptPaymentTypeDTO.getAmount());
                            terminalReceiptPaymentTypeDTO.setValid(false);
                        }
                        receiptTotalPrice = receiptTotalPrice.subtract(terminalReceiptPaymentTypeDTO.getAmount());
                    } else {
                        terminalReceiptDTO.setPayments(receiptPayments);
                    }
                }
                this.create(terminalReceiptDTO);
            }
        }, () -> validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", stockOutId));
    }

    public Page<TerminalReceiptDTO> getListTo1C(ReceiptFilter filter) {
        filter.setSize(1000);
        Company company = companyRepository.findFirstByTinAndDeletedIsFalse(filter.getTin()).orElse(null);
        if (company == null) {
            throw validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", filter.getTin()).get();
        }
        filter.setCompanyId(company.getId());
        List<TerminalReceiptDTO> result = new ArrayList<>();
        ResultList<Receipt> resultList = receiptRepository
                .getResultList(filter);
        resultList.getList()
                .forEach(receipt -> {
                    TerminalReceiptDTO terminalReceiptDTO = new TerminalReceiptDTO();
                    terminalReceiptDTO.setId(receipt.getId());
                    terminalReceiptDTO.setUid(receipt.getUid());
                    terminalReceiptDTO.setStatus(receipt.getStatus());
                    terminalReceiptDTO.setPositionCount(receipt.getPositionCount());
                    if (StringUtils.isNotEmpty(receipt.getFiscalUrl())) {
                        TerminalReceiptFiscalDTO fiscalDTO = new TerminalReceiptFiscalDTO();
                        fiscalDTO.setFiscalSign(receipt.getFiscalSign());
                        fiscalDTO.setFiscalUrl(receipt.getFiscalUrl());
                        fiscalDTO.setFiscalSerialNumber(receipt.getTerminalFiscalNumber());
                        fiscalDTO.setShiftNumber(receipt.getShiftNumber());
                        fiscalDTO.setReceiptSeq(receipt.getReceiptSeq());
                        fiscalDTO.setFiscalReceiptCreatedDate(receipt.getCreatedDate());
                        terminalReceiptDTO.setFiscalDTO(fiscalDTO);
                    }
                    if (!CollectionUtils.isEmpty(receipt.getPayments())) {
                        Map<String, BigDecimal> payments = new LinkedHashMap<>();
                        for (ReceiptPayment payment : receipt.getPayments()) {
                            payments.put(payment.getPaymentType().getCode(), payment.getPrice());
                        }
                        terminalReceiptDTO.setPayments(payments);
                    }
                    terminalReceiptDTO.setCompany(receipt.getCompany().toCommonBaseDTO());
                    terminalReceiptDTO.setTotalPrice(receipt.getTotalPrice());
                    terminalReceiptDTO.setTotalPaid(receipt.getTotalPaid());
                    terminalReceiptDTO.setTotalVat(receipt.getTotalVat());
                    terminalReceiptDTO.setTotalQty(receipt.getTotalQty());
                    terminalReceiptDTO.setTotalCash(receipt.getTotalCash());
                    terminalReceiptDTO.setTotalDiscount(receipt.getTotalDiscount());
                    terminalReceiptDTO.setTotalCard(receipt.getTotalCard());
                    if (receipt.getBranchId() != null) {
                        BranchAddressLookupDTO branchAddressLookupDTO = new BranchAddressLookupDTO();
                        branchAddressLookupDTO.setId(receipt.getBranch().getId());
                        branchAddressLookupDTO.setName(receipt.getBranch().getName());
                        if (receipt.getBranch().getAddress() != null) {
                            branchAddressLookupDTO.setAddress(addressMapper.toDetailDTO(receipt.getBranch().getAddress()));
                        }
                        terminalReceiptDTO.setBranch(branchAddressLookupDTO);
                    }
                    terminalReceiptDTO.setStockOutId(receipt.getStockOutId());
                    terminalReceiptDTO.setStockReturnId(receipt.getStockReturnId());
                    if (!CollectionUtils.isEmpty(receipt.getPayments())) {
                        Map<String, BigDecimal> payments = new LinkedHashMap<>();
                        for (ReceiptPayment payment : receipt.getPayments()) {
                            payments.put(payment.getPaymentType().getCode(), payment.getPrice());
                        }
                        terminalReceiptDTO.setPayments(payments);
                    }
                    terminalReceiptDTO.setCustomer(receipt.getCustomer().toCommonDTO());
                    if (receipt.getCreatorId() != null) {
                        terminalReceiptDTO.setCreator(receipt.getCreator().toCommonDTO());
                    }
                    for (ReceiptItem receiptItem : receipt.getItems()) {
                        TerminalReceiptItemDTO terminalReceiptItemDTO = new TerminalReceiptItemDTO();
                        terminalReceiptItemDTO.setId(receiptItem.getId());
                        terminalReceiptItemDTO.setComittentTin(receiptItem.getComittentTin());
                        terminalReceiptItemDTO.setQty(receiptItem.getQty());
                        terminalReceiptItemDTO.setVatRate(receiptItem.getVatRate());
                        terminalReceiptItemDTO.setVatPrice(receiptItem.getVatPrice());
                        terminalReceiptItemDTO.setTotalVatPrice(receiptItem.getTotalVatPrice());
                        terminalReceiptItemDTO.setTotalPrice(receiptItem.getTotalPrice());
                        terminalReceiptItemDTO.setMarks(receiptItem.getMarks());
                        terminalReceiptItemDTO.setDiscountPercent(receiptItem.getDiscountPercent());
                        terminalReceiptItemDTO.setDiscountPrice(receiptItem.getDiscountPrice());
                        terminalReceiptItemDTO.setTotalDiscountPrice(receiptItem.getTotalDiscountPrice());

                        TerminalProductListDTO product = new TerminalProductListDTO();
                        product.setId(receiptItem.getProductId());
                        product.setName(receiptItem.getProduct().getName());
                        product.setBarcode(receiptItem.getProduct().getBarcode());
                        product.setVatBarcode(receiptItem.getProduct().getVatBarcode());
                        product.setVat(receiptItem.getProduct().getVat());
                        product.setUnitId(receiptItem.getProduct().getUnitId());
                        product.setUnitName(receiptItem.getProduct().getUnit().getName());
                        product.setPrice(receiptItem.getPrice());
                        product.setPackageCode(Optional.ofNullable(receiptItem.getPackageCode()).orElseGet(() -> receiptItem.getProduct().getPackageCode()));
                        product.setPackageName(Optional.ofNullable(receiptItem.getPackageName()).orElseGet(() -> receiptItem.getProduct().getPackageName()));
                        terminalReceiptItemDTO.setProduct(product);
                        terminalReceiptDTO.addItem(terminalReceiptItemDTO);
                    }
                    result.add(terminalReceiptDTO);
                });
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }
}