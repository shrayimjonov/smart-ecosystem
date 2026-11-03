package uz.uzkassa.smartpossupply.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.dto.eimzo.CertificateDto;
import uz.uzkassa.smartpossupply.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpossupply.dto.eimzo.SignatureData;
import uz.uzkassa.smartpossupply.dto.eimzo.SignerDto;
import uz.uzkassa.smartpossupply.dto.factura.*;
import uz.uzkassa.smartpossupply.enums.FacturaType;
import uz.uzkassa.smartpossupply.integration.EimzoClient;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqFacturaProducer;
import uz.uzkassa.smartpossupply.repository.FacturaRepository;
import uz.uzkassa.smartpossupply.utils.DateUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class FacturaWebhookService extends BaseService {

    private static final String SELLER = "SELLER";
    private static final String BUYER = "BUYER";
    FacturaRepository facturaRepository;
    RabbitMqFacturaProducer rabbitMqFacturaProducer;
    FacturaRestClient facturaRestClient;
    EimzoClient eimzoClient;
    TelegramBotFacturaService telegramFacturaService;
    Environment environment;

    @Transactional
    public void onReceive(String sign) {
        StringBuilder message = new StringBuilder();
        message.append("<b>Environment:</b> ").append(environment.getActiveProfiles()[0]).append("\n");
        message.append("<b>Method:</b> ").append("onReceive").append("\n");
        message.append("<b>Sign:</b> ").append(sign).append("\n");
        Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
        if (pkcs7Dto.isSuccess()) {
            FacturaDTO facturaDTO = parseBase64(pkcs7Dto.getPkcs7Info().getDocumentBase64());
            //log.info("================ ON RECEIVE FacturaDTO: {} ===============", facturaDTO);
            if (facturaDTO != null) {
                Map<String, Company> companiesMap = getSellerAndBuyerCompanies("ON RECEIVE", facturaDTO);
                if (CollectionUtils.isEmpty(companiesMap)) {
                    log.info("============= Seller and buyer are empty! (onReceive) ===============");
                    message.append("<b>Message:</b> ").append("Seller and buyer are empty!");
                    telegramFacturaService.send(message.toString());
                    return;
                }
                Company seller = companiesMap.get(SELLER);
                Company buyer = companiesMap.get(BUYER);

                SignerDto signerDto = pkcs7Dto.getPkcs7Info().getSigners().getFirst();
                CertificateDto certificateDto = signerDto.getCertificate().getFirst();

                Factura factura = facturaRepository
                        .findFirstByFacturaIdAndDeletedIsFalse(facturaDTO.getFacturaId())
                        .orElseGet(Factura::new);
                if (factura.getId() == null) {
                    factura.setFacturaId(facturaDTO.getFacturaId());
                    factura.setStatus(Status.PENDING);
                    factura.setSign(sign);
                    factura.setSendNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
                    factura.setSendBy(certificateDto.getSubjectInfo().getFullName());
                    if (signerDto.getTimeStampInfo() != null && signerDto.getTimeStampInfo().getTime() != null) {
                        String time = signerDto.getTimeStampInfo().getTime();
                        try {
                            factura.setSendDate(LocalDateTime.parse(time, time.contains("-") ? DateUtils.DATE_TIME_HYPHEN_FORMATTER : DateUtils.DATE_TIME_FORMATTER));
                        } catch (Exception e) {
                            log.error("Factura Send Date, Time Stamp Info Error :{}", time);
                            log.error(e.getMessage(), e);
                        }
                    }
                    if (facturaDTO.getFacturaDoc() != null) {
                        factura.setFacturaNo(facturaDTO.getFacturaDoc().getFacturaNo());
                        factura.setFacturaDate(facturaDTO.getFacturaDoc().getFacturaDate());
                    }

                    factura.setSellerId(seller.getId());
                    factura.setBuyerId(buyer.getId());

                    if (facturaDTO.getProductList() != null) {
                        if (facturaDTO.getProductList().getDeliverySumWithVat() != null) {
                            factura.setTotal(facturaDTO.getProductList().getDeliverySumWithVat());
                        } else if (!CollectionUtils.isEmpty(facturaDTO.getProductList().getProducts())) {
                            BigDecimal deliverSumWithVat = BigDecimal.ZERO;
                            for (FacturaProductDTO facturaProductDTO : facturaDTO.getProductList().getProducts()) {
                                deliverSumWithVat = deliverSumWithVat.add(facturaProductDTO.getDeliverySumWithVat());
                            }
                            factura.setTotal(deliverSumWithVat);
                        }
                        factura.setFacturaProductId(facturaDTO.getProductList().getFacturaProductId());
                    }
                    factura.setVersion(Optional.ofNullable(facturaDTO.getVersion()).orElse(1));
                    factura.setFacturaType(Optional.ofNullable(facturaDTO.getFacturaType()).orElse(FacturaType.STANDART.getId()));
                    factura.setSingleSidedType(facturaDTO.getSingleSidedType());
                    factura.setUpdatedDate(LocalDateTime.now());

                    facturaRepository.save(factura);
                } else if (Status.DRAFT.equals(factura.getStatus())) {
                    factura.setSign(sign);
                    if (factura.getSendDate() == null || StringUtils.isEmpty(factura.getSendBy())) {
                        factura.setSendNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
                        factura.setSendBy(certificateDto.getSubjectInfo().getFullName());

                        if (signerDto.getTimeStampInfo() != null && signerDto.getTimeStampInfo().getTime() != null) {
                            String time = signerDto.getTimeStampInfo().getTime();
                            try {
                                factura.setSendDate(LocalDateTime.parse(time, time.contains("-") ? DateUtils.DATE_TIME_HYPHEN_FORMATTER : DateUtils.DATE_TIME_FORMATTER));
                            } catch (Exception e) {
                                log.error("Factura Send Date, Time Stamp Info Error :{}", time);
                                log.error(e.getMessage(), e);
                            }
                        }
                    }
                    factura.setStatus(Status.PENDING);
                    facturaRepository.save(factura);
                }
            }
        } else {
            log.error("JSON: {}", pkcs7Dto);
            message.append("<b>Message:</b> ").append(pkcs7Dto);
            telegramFacturaService.send(message.toString());
            throw new IntegrationException("Не подтверждено", pkcs7Dto.getMessage());
        }
    }

    @Transactional
    public void onStatusChange(String sign, Status status) {
        StringBuilder message = new StringBuilder();
        message.append("<b>Environment:</b> ").append(environment.getActiveProfiles()[0]).append("\n");
        message.append("<b>Method:</b> ").append("onStatusChange").append("\n");
        message.append("<b>Sign:</b> ").append(sign).append("\n");
        message.append("<b>Status:</b> ").append(status).append("\n");
        Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
        if (pkcs7Dto.isSuccess()) {
            FacturaDTO facturaDTO = parseBase64(pkcs7Dto.getPkcs7Info().getDocumentBase64());
            //log.info("On Status Change FacturaDTO: {}", facturaDTO);
            if (facturaDTO != null) {
                if (StringUtils.isEmpty(facturaDTO.getFacturaId())) {
                    log.info("FacturaId is empty (onStatusChange), facturaId: {}", facturaDTO.getFacturaId());
                    message.append("<b>Message:</b> ").append("FacturaId is empty: ").append(facturaDTO.getFacturaId());
                    telegramFacturaService.send(message.toString());
                    return;
                }

                Map<String, Company> companiesMap = getSellerAndBuyerCompanies("ON ACCEPT/REJECT", facturaDTO);
                if (CollectionUtils.isEmpty(companiesMap)) {
                    log.info("============= Seller and buyer are empty! (onStatusChange) ===============");
                    message.append("<b>Message:</b> ").append("Seller and buyer are empty!");
                    telegramFacturaService.send(message.toString());
                    return;
                }


                SignerDto signerDto = pkcs7Dto.getPkcs7Info().getSigners().size() > 1 ?
                        pkcs7Dto.getPkcs7Info().getSigners().get(1) : pkcs7Dto.getPkcs7Info().getSigners().get(0);
                CertificateDto certificateDto = signerDto.getCertificate().getFirst();

                Factura factura = facturaRepository.findFirstByFacturaIdAndDeletedIsFalse(facturaDTO.getFacturaId()).orElse(new Factura());
                factura.setStatus(status);
                factura.setNotes(facturaDTO.getNotes());
                factura.setApprovedNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
                factura.setApprovedBy(certificateDto.getSubjectInfo().getFullName());

                if (signerDto.getTimeStampInfo() != null && signerDto.getTimeStampInfo().getTime() != null) {
                    String time = signerDto.getTimeStampInfo().getTime();
                    try {
                        factura.setApprovedDate(LocalDateTime.parse(time, time.contains("-") ? DateUtils.DATE_TIME_HYPHEN_FORMATTER : DateUtils.DATE_TIME_FORMATTER));
                    } catch (Exception e) {
                        log.error("Factura Approved Date, Time Stamp Info Error :{}", time);
                        log.error(e.getMessage(), e);
                    }
                }

                if (factura.getSendDate() == null || StringUtils.isEmpty(factura.getSendBy())) {
                    SignerDto senderSign = pkcs7Dto.getPkcs7Info().getSigners().getFirst();
                    CertificateDto senderCert = senderSign.getCertificate().getFirst();
                    factura.setSendNumber(String.valueOf(Integer.parseInt(senderCert.getSerialNumber(), 16)));
                    factura.setSendBy(senderCert.getSubjectInfo().getFullName());
                    if (senderSign.getTimeStampInfo() != null && senderSign.getTimeStampInfo().getTime() != null) {
                        String time = senderSign.getTimeStampInfo().getTime();
                        try {
                            factura.setSendDate(LocalDateTime.parse(time, time.contains("-") ? DateUtils.DATE_TIME_HYPHEN_FORMATTER : DateUtils.DATE_TIME_FORMATTER));
                        } catch (Exception e) {
                            log.error("Factura Send Date, Time Stamp Info Error :{}", time);
                            log.error(e.getMessage(), e);
                        }
                    }
                }

                if (factura.getId() == null) {

                    Company seller = companiesMap.get(SELLER);
                    Company buyer = companiesMap.get(BUYER);

                    factura.setSellerId(seller.getId());
                    factura.setBuyerId(buyer.getId());

                    factura.setFacturaId(facturaDTO.getFacturaId());
                    factura.setSign(sign);

                    if (facturaDTO.getFacturaDoc() != null) {
                        factura.setFacturaNo(facturaDTO.getFacturaDoc().getFacturaNo());
                        factura.setFacturaDate(facturaDTO.getFacturaDoc().getFacturaDate());
                    }

                    if (facturaDTO.getProductList() != null) {
                        if (facturaDTO.getProductList().getDeliverySumWithVat() != null) {
                            factura.setTotal(facturaDTO.getProductList().getDeliverySumWithVat());
                        } else if (!CollectionUtils.isEmpty(facturaDTO.getProductList().getProducts())) {
                            BigDecimal deliverSumWithVat = BigDecimal.ZERO;
                            for (FacturaProductDTO facturaProductDTO : facturaDTO.getProductList().getProducts()) {
                                deliverSumWithVat = deliverSumWithVat.add(facturaProductDTO.getDeliverySumWithVat());
                            }
                            factura.setTotal(deliverSumWithVat);
                        }
                        factura.setFacturaProductId(facturaDTO.getProductList().getFacturaProductId());
                    }
                    factura.setVersion(Optional.ofNullable(facturaDTO.getVersion()).orElse(1));
                    factura.setFacturaType(Optional.ofNullable(facturaDTO.getFacturaType()).orElse(FacturaType.STANDART.getId()));
                    factura.setSingleSidedType(facturaDTO.getSingleSidedType());
                    factura.setUpdatedDate(LocalDateTime.now());
                }
                facturaRepository.save(factura);
            }
        } else {
            log.debug("JSON: {}", pkcs7Dto);
            message.append("<b>Message:</b> ").append(pkcs7Dto);
            telegramFacturaService.send(message.toString());
            throw new IntegrationException("Не подтверждено", pkcs7Dto.getMessage());
        }
    }

    public void onUpdate(FacturaNewDTO facturaDTO) {
        if (facturaDTO != null &&
                StringUtils.isNotEmpty(facturaDTO.getFacturaId()) &&
                StringUtils.isNotEmpty(facturaDTO.getSellerTin()) &&
                StringUtils.isNotEmpty(facturaDTO.getBuyerTin())) {
            rabbitMqFacturaProducer.onUpdateQueue(facturaDTO);
        }
    }

    @Transactional
    public void onUpdateQueue(FacturaNewDTO facturaDTO) {
        StringBuilder message = new StringBuilder();
        message.append("<b>Environment:</b> ").append(environment.getActiveProfiles()[0]).append("\n");
        message.append("<b>Factura:</b> ").append(facturaDTO).append("\n");
        Company seller = companyRepository.findByTinAndDeletedIsFalse(facturaDTO.getSellerTin()).orElse(null);
        if (seller == null) {
            log.warn("Factura seller is null (onUpdateQueue), seller tin: {}", facturaDTO.getSellerTin());
            message.append("<b>Message:</b> ").append("Seller is null");
            telegramFacturaService.send(message.toString());
            return;
        }

        Company buyer = companyRepository.findByTinAndDeletedIsFalse(facturaDTO.getBuyerTin()).orElse(null);
        if (buyer == null) {
            log.warn("Factura buyer is null (onUpdateQueue), buyer tin: {}", facturaDTO.getBuyerTin());
            message.append("<b>Message:</b> ").append("Buyer is null");
            telegramFacturaService.send(message.toString());
            return;
        }

        if (!(seller.isAllowInvoiceSync() || buyer.isAllowInvoiceSync())) {
            log.warn("Factura isAllowInvoiceSync of seller and buyer is false (onUpdateQueue)");
            message.append("<b>Message:</b> ").append("isAllowInvoiceSync of seller and buyer is false");
            telegramFacturaService.send(message.toString());
            return;
        }

        Factura factura = facturaRepository.findFirstByFacturaIdAndDeletedIsFalse(facturaDTO.getFacturaId()).orElseGet(Factura::new);
        if (factura.getId() == null) {
            factura.setBuyerId(buyer.getId());
            factura.setSellerId(seller.getId());
            factura.setFacturaId(facturaDTO.getFacturaId());
            factura.setFacturaProductId(facturaDTO.getFacturaProductId());
            factura.setFacturaNo(facturaDTO.getFacturaNo());
            if (facturaDTO.getFacturaDate() != null) {
                factura.setFacturaDate(facturaDTO.getFacturaDate().toLocalDate());
            }
            factura.setTotal(facturaDTO.getPayableTotal());
            factura.setNotes(facturaDTO.getNotes());
        }
        String sign = null;
        try {
            sign = facturaRestClient.getSign(facturaDTO.getFacturaId(), facturaDTO.getBuyerTin(), facturaDTO.getState(), true);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        if (StringUtils.isEmpty(sign)) {
            try {
                sign = facturaRestClient.getSign(facturaDTO.getFacturaId(), facturaDTO.getSellerTin(), facturaDTO.getState(), false);
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
        }
        if (StringUtils.isNotEmpty(sign)) {
            updateSignatureData(sign, factura);
            factura.setSign(sign);
        } else {
            log.warn("Factura sign is null (onUpdateQueue). Factura Id :{}", facturaDTO.getFacturaId());
            message.append("<b>Message:</b> ").append("Sign is null");
            telegramFacturaService.send(message.toString());
            return;
        }
        factura.setStatus(facturaDTO.getState());
        //log.info("Factura object before saving (onUpdateQueue): {}", factura);
        facturaRepository.save(factura);
    }

    @Transactional
    public void updateSignatureData(String sign, Factura factura) {
        Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
        if (pkcs7Dto.isSuccess()
                && pkcs7Dto.getPkcs7Info() != null
                && !CollectionUtils.isEmpty(pkcs7Dto.getPkcs7Info().getSigners())) {
            SignatureData signatureData;
            if (StringUtils.isAnyEmpty(factura.getSendBy(), factura.getSendNumber())) {
                signatureData = parseSignatureData(pkcs7Dto.getPkcs7Info().getSigners().getFirst());
                factura.setSenderData(signatureData);
                try {
                    log.info("Sender data: {}", objectMapper.writeValueAsString(signatureData));
                } catch (JsonProcessingException e) {
                    log.error("Object convert error");
                }
            }
            if (StringUtils.isAnyEmpty(factura.getApprovedBy(), factura.getApprovedNumber()) && pkcs7Dto.getPkcs7Info().getSigners().size() > 1) {
                signatureData = parseSignatureData(pkcs7Dto.getPkcs7Info().getSigners().get(1));
                factura.setApproverData(signatureData);
                try {
                    log.info("Approver data: {}", objectMapper.writeValueAsString(signatureData));
                } catch (JsonProcessingException e) {
                    log.error("Object convert error");
                }
            }
        } else {
            log.debug("JSON: {}", pkcs7Dto);
        }
    }

    private SignatureData parseSignatureData(SignerDto signerDto) {
        final CertificateDto certificateDto = signerDto.getCertificate().getFirst();
        SignatureData data = new SignatureData();
        data.setNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
        data.setOwner(certificateDto.getSubjectInfo().getFullName());
        if (StringUtils.isNotEmpty(signerDto.getTimeStampInfo().getTime())) {
            String time = signerDto.getTimeStampInfo().getTime();
            try {
                data.setDateTime(LocalDateTime.parse(time, time.contains("-") ? DateUtils.DATE_TIME_HYPHEN_FORMATTER : DateUtils.DATE_TIME_FORMATTER));
            } catch (Exception e) {
                log.error("Factura signer date time: {}", time);
                log.error(e.getMessage(), e);
            }
        }
        return data;
    }

    public FacturaDTO parseBase64(String base64) {
        final String json = new String(Base64.getDecoder().decode(base64));
        FacturaDTO result = null;
        try {
            result = objectMapper.readValue(json, FacturaDTO.class);
        } catch (Exception ex) {
            log.error("JSON: {}", json);
            if (ex instanceof InvalidFormatException) {
                log.error("ERROR: {}", ex.getMessage());
                throw validationService.badRequestExceptionThrow(ex.getMessage(), "Невозможно открыть документ. В счет-фактуре допущены ошибки").get();
            } else if (ex instanceof IOException) {
                log.error("Factura sign parse error:" + ex.getMessage(), ex);
                try {
                    FacturaRejectDTO facturaRejectDTO = objectMapper.readValue(json, FacturaRejectDTO.class);
                    result = facturaRejectDTO.getFactura();
                    result.setNotes(facturaRejectDTO.getNotes());
                } catch (IOException e) {
                    log.info("JSON: {}", json);
                    log.error("Factura sign reject parse error:" + ex.getMessage(), e);
                }
            }
        }
        return result;
    }

    private Map<String, Company> getSellerAndBuyerCompanies(String method, FacturaDTO facturaDTO) {

        if (StringUtils.isEmpty(facturaDTO.getSellerTin())) {
            return null;
        }
        if (StringUtils.isEmpty(facturaDTO.getBuyerTin())) {
            return null;
        }

        Company seller = companyRepository.findByTinAndDeletedIsFalse(facturaDTO.getSellerTin()).orElse(null);
        if (seller == null) {
            return null;
        }

        Company buyer = companyRepository.findByTinAndDeletedIsFalse(facturaDTO.getBuyerTin()).orElse(null);
        if (buyer == null) {
            return null;
        }

        if (seller.isAllowInvoiceSync() || buyer.isAllowInvoiceSync()) {
            Map<String, Company> companiesMap = new HashMap<>();
            companiesMap.put(SELLER, seller);
            companiesMap.put(BUYER, buyer);
            return companiesMap;
        }
        return null;
    }
}
