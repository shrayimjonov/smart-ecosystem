package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.factura.Factura;
import uz.uzkassa.horeca.dto.eimzo.CertificateDto;
import uz.uzkassa.horeca.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.horeca.dto.eimzo.SignatureData;
import uz.uzkassa.horeca.dto.eimzo.SignerDto;
import uz.uzkassa.horeca.dto.factura.FacturaDTO;
import uz.uzkassa.horeca.dto.factura.FacturaNewDTO;
import uz.uzkassa.horeca.dto.factura.FacturaRejectDTO;
import uz.uzkassa.horeca.dto.factura.Status;
import uz.uzkassa.horeca.enums.FacturaType;
import uz.uzkassa.horeca.integration.EimzoClient;
import uz.uzkassa.horeca.integration.FacturaClient;
import uz.uzkassa.horeca.rabbitmq.producer.RabbitMqFacturaProducer;
import uz.uzkassa.horeca.repository.FacturaRepository;
import uz.uzkassa.horeca.utils.DateUtils;
import uz.uzkassa.horeca.web.rest.errors.IntegrationException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FacturaWebhookService extends BaseService {

    private static final String SELLER = "SELLER";
    private static final String BUYER = "BUYER";
    FacturaRepository facturaRepository;
    RabbitMqFacturaProducer rabbitMqFacturaProducer;
    FacturaClient facturaClient;
    EimzoClient eimzoClient;

    public void onReceive(String sign) {
        Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
        if (pkcs7Dto.isSuccess()) {
            FacturaDTO facturaDTO = parseBase64(pkcs7Dto.getPkcs7Info().getDocumentBase64());
            if (facturaDTO != null) {
                log.info("================ ON RECEIVE FacturaDTO: {} ===============", facturaDTO);
                Map<String, Company> companiesMap = getSellerAndBuyerCompanies("ON RECEIVE", facturaDTO);
                if (CollectionUtils.isEmpty(companiesMap)) {
                    return;
                }
                Company seller = companiesMap.get(SELLER);
                Company buyer = companiesMap.get(BUYER);

                SignerDto signerDto = pkcs7Dto.getPkcs7Info().getSigners().get(0);
                CertificateDto certificateDto = signerDto.getCertificate().get(0);

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
                            factura.setSendDate(
                                LocalDateTime.parse(
                                    time,
                                    time.contains("-") ? DateUtils.dateTimeHyphenFormatter : DateUtils.dateTimeFormatter
                                )
                            );
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
                        factura.setTotal(facturaDTO.getProductList().getDeliverySumWithVat());
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
                                factura.setSendDate(
                                    LocalDateTime.parse(
                                        time,
                                        time.contains("-") ? DateUtils.dateTimeHyphenFormatter : DateUtils.dateTimeFormatter
                                    )
                                );
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
            throw new IntegrationException("Не подтверждено", pkcs7Dto.getMessage());
        }
    }

    public void onStatusChange(String sign, Status status) {
        Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
        if (pkcs7Dto.isSuccess()) {
            FacturaDTO facturaDTO = parseBase64(pkcs7Dto.getPkcs7Info().getDocumentBase64());
            if (facturaDTO != null) {
                log.info("On Status Change FacturaDTO: {}", facturaDTO);
                if (StringUtils.isEmpty(facturaDTO.getFacturaId())) {
                    return;
                }

                Map<String, Company> companiesMap = getSellerAndBuyerCompanies("ON ACCEPT/REJECT", facturaDTO);
                if (CollectionUtils.isEmpty(companiesMap)) {
                    log.info("============= Seller and buyer are empty! ===============");
                    return;
                }

                Factura factura = facturaRepository.findFirstByFacturaIdAndDeletedIsFalse(facturaDTO.getFacturaId()).orElse(new Factura());

                SignerDto signerDto = pkcs7Dto.getPkcs7Info().getSigners().size() > 1
                    ? pkcs7Dto.getPkcs7Info().getSigners().get(1)
                    : pkcs7Dto.getPkcs7Info().getSigners().get(0);
                CertificateDto certificateDto = signerDto.getCertificate().get(0);

                factura.setNotes(facturaDTO.getNotes());
                factura.setApprovedNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
                factura.setApprovedBy(certificateDto.getSubjectInfo().getFullName());

                if (signerDto.getTimeStampInfo() != null && signerDto.getTimeStampInfo().getTime() != null) {
                    String time = signerDto.getTimeStampInfo().getTime();
                    try {
                        factura.setApprovedDate(
                            LocalDateTime.parse(time, time.contains("-") ? DateUtils.dateTimeHyphenFormatter : DateUtils.dateTimeFormatter)
                        );
                    } catch (Exception e) {
                        log.error("Factura Approved Date, Time Stamp Info Error :{}", time);
                        log.error(e.getMessage(), e);
                    }
                }

                if (factura.getSendDate() == null || StringUtils.isEmpty(factura.getSendBy())) {
                    SignerDto senderSign = pkcs7Dto.getPkcs7Info().getSigners().get(0);
                    CertificateDto senderCert = senderSign.getCertificate().get(0);
                    factura.setSendNumber(String.valueOf(Integer.parseInt(senderCert.getSerialNumber(), 16)));
                    factura.setSendBy(senderCert.getSubjectInfo().getFullName());
                    if (senderSign.getTimeStampInfo() != null && senderSign.getTimeStampInfo().getTime() != null) {
                        String time = senderSign.getTimeStampInfo().getTime();
                        try {
                            factura.setSendDate(
                                LocalDateTime.parse(
                                    time,
                                    time.contains("-") ? DateUtils.dateTimeHyphenFormatter : DateUtils.dateTimeFormatter
                                )
                            );
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
                    factura.setStatus(status);
                    factura.setSign(sign);

                    if (facturaDTO.getFacturaDoc() != null) {
                        factura.setFacturaNo(facturaDTO.getFacturaDoc().getFacturaNo());
                        factura.setFacturaDate(facturaDTO.getFacturaDoc().getFacturaDate());
                    }

                    if (facturaDTO.getProductList() != null) {
                        factura.setTotal(facturaDTO.getProductList().getDeliverySumWithVat());
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
            throw new IntegrationException("Не подтверждено", pkcs7Dto.getMessage());
        }
    }

    public void onUpdate(FacturaNewDTO facturaDTO) {
        if (
            facturaDTO != null &&
            StringUtils.isNotEmpty(facturaDTO.getFacturaId()) &&
            StringUtils.isNotEmpty(facturaDTO.getSellerTin()) &&
            StringUtils.isNotEmpty(facturaDTO.getBuyerTin())
        ) {
            rabbitMqFacturaProducer.onUpdateQueue(facturaDTO);
        }
    }

    public void onUpdateQueue(FacturaNewDTO facturaDTO) {
        Company seller = companyRepository.findByTinAndDeletedIsFalse(facturaDTO.getSellerTin()).orElse(null);
        if (seller == null) {
            return;
        }

        Company buyer = companyRepository.findByTinAndDeletedIsFalse(facturaDTO.getBuyerTin()).orElse(null);
        if (buyer == null) {
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
            sign = facturaClient.getSign(facturaDTO.getFacturaId(), facturaDTO.getBuyerTin(), facturaDTO.getState(), true);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        if (StringUtils.isEmpty(sign)) {
            try {
                sign = facturaClient.getSign(facturaDTO.getFacturaId(), facturaDTO.getSellerTin(), facturaDTO.getState(), false);
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
        }
        if (sign != null) {
            updateSignatureData(sign, factura);
            factura.setSign(sign);
        } else {
            log.error("Factura sign is null. Factura Id:{}", facturaDTO.getFacturaId());
            return;
        }
        factura.setStatus(facturaDTO.getState());
        facturaRepository.save(factura);
    }

    public void updateSignatureData(String sign, Factura factura) {
        Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
        if (pkcs7Dto.isSuccess() && pkcs7Dto.getPkcs7Info() != null && !CollectionUtils.isEmpty(pkcs7Dto.getPkcs7Info().getSigners())) {
            SignatureData signatureData;

            if (StringUtils.isAnyEmpty(factura.getSendBy(), factura.getSendNumber())) {
                signatureData = parseSignatureData(pkcs7Dto.getPkcs7Info().getSigners().get(0));
                factura.setSenderData(signatureData);
            }
            if (
                StringUtils.isAnyEmpty(factura.getApprovedBy(), factura.getApprovedNumber()) &&
                pkcs7Dto.getPkcs7Info().getSigners().size() > 1
            ) {
                signatureData = parseSignatureData(pkcs7Dto.getPkcs7Info().getSigners().get(1));
                factura.setApproverData(signatureData);
            }
        } else {
            log.debug("JSON: {}", pkcs7Dto);
        }
    }

    private SignatureData parseSignatureData(SignerDto signerDto) {
        final CertificateDto certificateDto = signerDto.getCertificate().get(0);
        SignatureData data = new SignatureData();
        data.setNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
        data.setOwner(certificateDto.getSubjectInfo().getFullName());
        if (StringUtils.isNotEmpty(signerDto.getTimeStampInfo().getTime())) {
            String time = signerDto.getTimeStampInfo().getTime();
            try {
                data.setDateTime(
                    LocalDateTime.parse(time, time.contains("-") ? DateUtils.dateTimeHyphenFormatter : DateUtils.dateTimeFormatter)
                );
            } catch (Exception e) {
                log.error("Factura signer date time: {}", time);
                log.error(e.getMessage(), e);
            }
        }
        return data;
    }

    public FacturaDTO parseBase64(String base64) {
        String json = new String(Base64.getDecoder().decode(base64));
        FacturaDTO result;
        try {
            result = objectMapper.readValue(json, FacturaDTO.class);
        } catch (IOException e) {
            try {
                FacturaRejectDTO facturaRejectDTO = objectMapper.readValue(json, FacturaRejectDTO.class);
                result = facturaRejectDTO.getFactura();
                result.setNotes(facturaRejectDTO.getNotes());
            } catch (IOException ex) {
                throw new IntegrationException("Ошибка от ГНК", ex.getMessage());
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

        Map<String, Company> companiesMap = new HashMap<>();
        companiesMap.put(SELLER, seller);
        companiesMap.put(BUYER, buyer);
        return companiesMap;
    }
}
