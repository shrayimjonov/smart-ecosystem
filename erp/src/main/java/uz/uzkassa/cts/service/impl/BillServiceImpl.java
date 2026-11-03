package uz.uzkassa.cts.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.domain.Bill;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.domain.PaymentQuote;
import uz.uzkassa.cts.domain.TradeInPriceHistory;
import uz.uzkassa.cts.dto.apay.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.bill.*;
import uz.uzkassa.cts.dto.payload.ContractReportPayload;
import uz.uzkassa.cts.enums.ContractType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.PaymentQuoteStatus;
import uz.uzkassa.cts.enums.ServiceReferenceType;
import uz.uzkassa.cts.enums.apay.APayMerchantReferenceType;
import uz.uzkassa.cts.enums.apay.APayStatus;
import uz.uzkassa.cts.filter.BillFilter;
import uz.uzkassa.cts.mappings.BillMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqContractReportProducer;
import uz.uzkassa.cts.repository.BillRepository;
import uz.uzkassa.cts.repository.TradeInPriceHistoryRepository;
import uz.uzkassa.cts.service.APayService;
import uz.uzkassa.cts.service.BillService;
import uz.uzkassa.cts.service.DeviceService;
import uz.uzkassa.cts.service.PaymentQuoteService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class BillServiceImpl extends BaseServiceImpl<Bill, Long> implements BillService {

    private final BillRepository billRepository;
    private final APayService aPayService;
    private final DeviceService deviceService;
    private final TradeInPriceHistoryRepository tradeInPriceHistoryRepository;
    private final ObjectMapper objectMapper;
    private final PaymentQuoteService paymentQuoteService;
    private final ApplicationProperties applicationProperties;
    private final BillMapper billMapper;
    private final RabbitMqContractReportProducer rabbitMqContractReportProducer;

    public BillServiceImpl(BillRepository billRepository,
                           APayService aPayService,
                           DeviceService deviceService,
                           TradeInPriceHistoryRepository tradeInPriceHistoryRepository,
                           ObjectMapper objectMapper,
                           PaymentQuoteService paymentQuoteService,
                           ApplicationProperties applicationProperties,
                           BillMapper billMapper, RabbitMqContractReportProducer rabbitMqContractReportProducer) {
        super(billRepository);
        this.billRepository = billRepository;
        this.aPayService = aPayService;
        this.deviceService = deviceService;
        this.tradeInPriceHistoryRepository = tradeInPriceHistoryRepository;
        this.objectMapper = objectMapper;
        this.paymentQuoteService = paymentQuoteService;
        this.applicationProperties = applicationProperties;
        this.billMapper = billMapper;
        this.rabbitMqContractReportProducer = rabbitMqContractReportProducer;
    }

    @Override
    public String create(BillDTO billDTO) {
        Bill bill = new Bill();
        PaymentQuote paymentQuote = paymentQuoteService.findById(billDTO.getPaymentQuoteId());
        bill.setDeviceId(billDTO.getDeviceId());
        bill.setBranchId(billDTO.getBranchId());
        bill.setCustomerId(billDTO.getCustomerId());
        bill.setCustomerBranchId(billDTO.getCustomerBranchId());
        bill.setCustomerBranchDeviceId(billDTO.getCustomerBranchDeviceId());
        bill.setPaymentQuoteId(billDTO.getPaymentQuoteId());
        bill.setDeviceType(billDTO.getDeviceType());
        if (billRepository.countByPaymentQuoteId(billDTO.getPaymentQuoteId()) >= paymentQuote.getQty()) {
            throw validationService.badRequestExceptionThrow(PAYMENT_QUOTE_ERROR).get();
        }
        Device device = deviceService.findById(billDTO.getDeviceId());
        if (device.getModel() == null) {
            throw validationService.requiredExceptionThrow(DEVICE_MODEL).get();
        }
        TradeInPriceHistory tradeInPriceHistory = tradeInPriceHistoryRepository.findFirstByActiveTrueAndContractTypeAndDeviceModelIdAndDeviceType(ContractType.PAYMENT_CONTRACT, device.getModelId(), billDTO.getDeviceType());
        if (tradeInPriceHistory == null) {
            throw validationService.entityNotFoundExceptionThrow(PRICE).get();
        }
        APayAddTransactionResponseDto responseDto = this.createAPayTransaction(tradeInPriceHistory.getPrice1pcWithVat(), billDTO.getCustomerBranchId());
        bill.setBillId(responseDto.getId());
        bill.setPayStatus(responseDto.getStatus());
        bill.setAmount(tradeInPriceHistory.getPrice1pcWithVat());
        bill.setTradeInPriceHistoryId(tradeInPriceHistory.getId());
        save(bill);

        return responseDto.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public BillDetailDTO get(Long id) {
        return billRepository
                .findById(id)
                .map(bill -> {
                    BillDetailDTO responseDTO = billMapper.toDto(bill);
                    responseDTO.setCustomer(bill.getCustomer().toCustomerShortDTO());
                    responseDTO.setBranch(bill.getBranch().toCommonDTO());
                    return responseDTO;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(BILL));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillDetailDTO> list(BillFilter filter) {
        ResultList<Bill> resultList = billRepository.getResultList(filter);
        return new PageImpl<>(resultList.getList()
                .stream()
                .map(bill -> {
                    BillDetailDTO responseDTO = billMapper.toDto(bill);
                    responseDTO.setCustomer(bill.getCustomer().toCustomerShortDTO());
                    responseDTO.setBranch(bill.getBranch().toCommonDTO());
                    return responseDTO;
                })
                .collect(Collectors.toList()), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public void updateAPay(BillUpdateDTO billUpdateDTO) {
        Optional<Bill> billOptional = billRepository.findFirstByBillId(billUpdateDTO.getBillId());
        if (billOptional.isPresent()) {
            try {
                APayBillUpdateRequestDTO updateRequestDTO = new APayBillUpdateRequestDTO();
                updateRequestDTO.setBillId(billUpdateDTO.getBillId());
                updateRequestDTO.setPan(billUpdateDTO.getPan());
                updateRequestDTO.setExpiry(billUpdateDTO.getExpiry());
                aPayService.updateBillTransaction(updateRequestDTO);
            } catch (Exception e) {
                log.error(e.getMessage());
                APayErrorResponseDto errorResponseDto = new APayErrorResponseDto();
                try {
                    errorResponseDto = objectMapper.readValue(e.getMessage(), APayErrorResponseDto.class);
                    billOptional.get().setPayErrorMessage(errorResponseDto.getDetail());
                } catch (JsonProcessingException jsonProcessingException) {
                    log.error("Json parsing error: {}", jsonProcessingException.getMessage());
                    errorResponseDto.setDetail(jsonProcessingException.getMessage() + " " + billOptional.get().getBillId());
                    billOptional.get().setPayErrorMessage("A-Pay updating : Json parsing error");
                }
                billRepository.save(billOptional.get());
                throw new IntegrationException("Error occurred while updating code send A-Pay transaction :" + errorResponseDto.getTitle());
            }
        }
    }

    @Override
    public void confirmationPay(BillConfirmDTO billConfirmDTO) {
        Bill billEntity = billRepository.findFirstByBillId(billConfirmDTO.getBillId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BILL));
        try {
            APayPayResponseDto responseDto = aPayService.confirm(new APayPayRequestDto(billEntity.getBillId(), billConfirmDTO.getConfirmationKey()));
            billEntity.setPayTime(responseDto.getTime());
            billEntity.setPayStatus(responseDto.getStatus());
            if (Objects.equals(APayStatus.PAID.getCode(), responseDto.getStatus().getCode())) {
                if (StringUtils.isEmpty(billEntity.getFiscalCheckUrl())) {
                    String fiscalUrl = this.fiscalize(billEntity);
                    billEntity.setFiscalCheckUrl(fiscalUrl);
                }
                paymentQuoteService.updateStatus(billEntity.getPaymentQuoteId(), PaymentQuoteStatus.ACCEPTED);
            }
            billRepository.save(billEntity);
        } catch (Exception e) {
            log.error(e.getMessage());
            APayErrorResponseDto errorResponseDto = new APayErrorResponseDto();
            try {
                errorResponseDto = objectMapper.readValue(e.getMessage(), APayErrorResponseDto.class);
                billEntity.setPayErrorMessage(errorResponseDto.getDetail());
            } catch (JsonProcessingException jsonProcessingException) {
                log.error("Json parsing error: {}", jsonProcessingException.getMessage());
                errorResponseDto.setDetail(jsonProcessingException.getMessage() + " " + billEntity.getBillId());
                billEntity.setPayErrorMessage("A-Pay confirmation : Json parsing error");
            }
            billRepository.save(billEntity);
            throw new IntegrationException("Error occurred while confirming code send A-Pay transaction :" + errorResponseDto.getTitle());
        }
    }

    @Override
    public BillResponseDTO checkPay(String billId) {
        Bill bill = billRepository.findFirstByBillId(billId).orElseThrow(validationService.entityNotFoundExceptionThrow(BILL));
        BillResponseDTO billResponseDTO = new BillResponseDTO();
        try {
            APayResponseDTO checkResponseDto = aPayService.getByBillId(billId);
            bill.setPayTime(checkResponseDto.getTransactionDate());
            bill.setPayStatus(checkResponseDto.getStatus());
            bill.setTerminalId(checkResponseDto.getTerminalId());
            bill.setFiscalCheckUrl(checkResponseDto.getQrCodeURL());
            bill.setFiscalSign(checkResponseDto.getFiscalSign());
            if (Objects.equals(APayStatus.PAID.getCode(), checkResponseDto.getStatus().getCode())) {
                if (StringUtils.isEmpty(bill.getFiscalCheckUrl())) {
                    String fiscalUrl = this.fiscalize(bill);
                    bill.setFiscalCheckUrl(fiscalUrl);
                }
                paymentQuoteService.updateStatus(bill.getPaymentQuoteId(), PaymentQuoteStatus.ACCEPTED);
                /*Create device in Contract report*/
                rabbitMqContractReportProducer.createContractReport(new ContractReportPayload(bill.getId(), EntityTypeEnum.BILL));
            }
            billRepository.save(bill);
            billResponseDTO.setBillId(billId);
            billResponseDTO.setBillStatus(checkResponseDto.getStatus());
            billResponseDTO.setFiscalCheckUrl(checkResponseDto.getQrCodeURL());
        } catch (Exception e) {
            log.error(e.getMessage());
            APayErrorResponseDto errorResponseDto = new APayErrorResponseDto();
            try {
                errorResponseDto = objectMapper.readValue(e.getMessage(), APayErrorResponseDto.class);
                bill.setPayErrorMessage(errorResponseDto.getDetail());
            } catch (JsonProcessingException jsonProcessingException) {
                log.error("Json parsing error: {}", jsonProcessingException.getMessage());
                errorResponseDto.setDetail(jsonProcessingException.getMessage() + " " + billId);
                bill.setPayErrorMessage("A-Pay checking : Json parsing error");
            }
            billRepository.save(bill);
            billResponseDTO.setPayErrorMessage(bill.getPayErrorMessage());
            throw new IntegrationException("Error occurred while checking code send A-Pay transaction :" + errorResponseDto.getTitle());
        }
        return billResponseDTO;
    }

    @Override
    public void updateBillStatusByBillId(String billId, APayUpdateBillStatusRequestDTO billStatusRequestDTO) {
        Bill billEntity = billRepository.findFirstByBillId(billId).orElseThrow(validationService.entityNotFoundExceptionThrow(BILL));
        if (billEntity.getPayStatus() != null && APayStatus.PENDING != billEntity.getPayStatus()) {
            return;
        }
        billEntity.setPayStatus(APayStatus.valueOf(billStatusRequestDTO.getStatus()));
        billEntity.setPayTime(billStatusRequestDTO.getTransactionDate());
        billEntity.setTerminalId(billStatusRequestDTO.getTerminalId());
        billEntity.setFiscalSign(billStatusRequestDTO.getFiscalSign());
        billEntity.setFiscalCheckUrl(billStatusRequestDTO.getQrCodeURL());
        billRepository.save(billEntity);
        if (Objects.equals(APayStatus.PAID.getCode(), billStatusRequestDTO.getStatus())) {
            if (StringUtils.isEmpty(billEntity.getFiscalCheckUrl())) {
                String fiscalUrl = this.fiscalize(billEntity);
                billEntity.setFiscalCheckUrl(fiscalUrl);
            }
            paymentQuoteService.updateStatus(billEntity.getPaymentQuoteId(), PaymentQuoteStatus.ACCEPTED);
            /*Create device in Contract report*/
            rabbitMqContractReportProducer.createContractReport(new ContractReportPayload(billEntity.getId(), EntityTypeEnum.BILL));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponseDTO getByDevice(Long deviceId) {
        return billRepository.findFirstByDeviceId(deviceId)
                .map(bill -> {
                    BillResponseDTO dto = new BillResponseDTO();
                    dto.setBillId(bill.getBillId());
                    dto.setBillStatus(bill.getPayStatus());
                    dto.setFiscalCheckUrl(bill.getFiscalCheckUrl());
                    dto.setPayErrorMessage(bill.getPayErrorMessage());
                    return dto;
                }).orElse(null);
    }

    @Override
    public void canceledOverTime() {
        billRepository.updateApayStatus(APayStatus.PENDING, APayStatus.CANCELED, LocalDate.now().atStartOfDay().minusDays(1));
    }

    private APayAddTransactionResponseDto createAPayTransaction(BigDecimal price, Long customerBranchId) {
        APayAddTransactionResponseDto responseDTO = new APayAddTransactionResponseDto();
        responseDTO.setStatus(APayStatus.PAID);

        APayBillCreateRequestDTO billRequest = new APayBillCreateRequestDTO();
        billRequest.setCtoId(customerBranchId);
        billRequest.setReferenceId(applicationProperties.getApayProperty().getMerchantId());
        billRequest.setReferenceType(APayMerchantReferenceType.SMARTPOS.getCode());
        billRequest.setAmount(price);
        billRequest.setPan(null);
        billRequest.setExpiry(null);
        billRequest.setCardId(null);
        Map<String, String> properties = new HashMap<>();
        properties.put("callbackUrl", "https://api.support24.uz/api/bill/bill-response");
        properties.put("clientType", "CTO");
        billRequest.setProperties(properties);

        log.info("Create A-Pay Bill REQUEST: {}", billRequest);
        try {
            return aPayService.createBillTransaction(billRequest);
        } catch (Exception e) {
            throw validationService.badRequestExceptionThrow(e.getMessage()).get();
        }
    }

    private String fiscalize(Bill bill) {
        if (bill.getTradeInPriceHistoryId() != null) {
            OfdBillFiscalRequestDTO apayRequest = new OfdBillFiscalRequestDTO();
            apayRequest.setReferenceType(ServiceReferenceType.CTO);
            apayRequest.setBillId(bill.getBillId());
            apayRequest.setMerchantInfo(new OfdCommitmentDTO("306866358"));
            BigDecimal price = bill.getAmount().multiply(BigDecimal.valueOf(100));
            long vatAmount = price.multiply(BigDecimal.valueOf(12)).divide(BigDecimal.valueOf(112), 4, RoundingMode.HALF_UP).longValue();
            apayRequest.setTotalVat(vatAmount);
            apayRequest.setReceivedECash(price.longValue());
            apayRequest.setTime(DateUtil.getLocalDateTimeAsString(LocalDateTime.now()));

            OfdBillItem billItem = new OfdBillItem();
            billItem.setName(bill.getDevice().getSerialNumber());
            billItem.setSpic(bill.getTradeInPriceHistory().getCatalogCode());
            billItem.setPackageCode(bill.getTradeInPriceHistory().getPackageCode());
            billItem.setGoodPrice(price.intValue());
            billItem.setAmount(1000);
            billItem.setPrice(price.longValue());
            billItem.setVat(vatAmount);
            billItem.setVatPercent(12);
            billItem.setCommissionInfo(new OfdCommitmentDTO("306866358"));

            apayRequest.setItems(Collections.singletonList(billItem));
            try {
                log.info("########################## ApayRequest fiscalization ###############################");
                log.info(objectMapper.writeValueAsString(apayRequest));
                log.info("######################################################################");
            } catch (JsonProcessingException ignored) {
            }
            OfdPaymentInfoResponseDTO fiscalResponse;
            try {
                fiscalResponse = aPayService.fiscal(apayRequest);
                try {
                    log.info("########################## ApayResponse fiscalization ###############################");
                    log.info(objectMapper.writeValueAsString(fiscalResponse));
                    log.info("#######################################################################");
                } catch (JsonProcessingException ignored) {
                }
                if (fiscalResponse == null || fiscalResponse.getCode() != 0) {
                    throw validationService.badRequestExceptionThrow(FISCALIZATION_CHECK).get();
                }
                return fiscalResponse.getQrCodeUrl();
            } catch (Exception e) {
                log.error("Fiscalization error: {}", e.getMessage());
                throw validationService.badRequestExceptionThrow("Bad error").get();
            }
        }
        return null;
    }
}
