package uz.uzkassa.smartpossupply.service;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.waybill.Waybill;
import uz.uzkassa.smartpossupply.domain.waybill.WaybillExecutor;
import uz.uzkassa.smartpossupply.dto.eimzo.CertificateDto;
import uz.uzkassa.smartpossupply.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpossupply.dto.eimzo.SignatureData;
import uz.uzkassa.smartpossupply.dto.eimzo.SignerDto;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillWebhookDTO;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.TransportType;
import uz.uzkassa.smartpossupply.enums.WaybillStatus;
import uz.uzkassa.smartpossupply.enums.WaybillType;
import uz.uzkassa.smartpossupply.integration.EimzoClient;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqWaybillProducer;
import uz.uzkassa.smartpossupply.repository.WaybillExecutorRepository;
import uz.uzkassa.smartpossupply.repository.WaybillRepository;
import uz.uzkassa.smartpossupply.utils.DateUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 17.07.2024
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class WaybillWebhookService extends BaseService {

    WaybillRepository waybillRepository;
    FacturaRestClient facturaRestClient;
    EimzoClient eimzoClient;
    WaybillExecutorRepository waybillExecutorRepository;
    RabbitMqWaybillProducer rabbitMqWaybillProducer;

    public void onUpdate(WaybillWebhookDTO waybillWebhookDTO) {
        if (waybillWebhookDTO != null && StringUtils.hasText(waybillWebhookDTO.getWaybillLocalId())) {
            rabbitMqWaybillProducer.onUpdateQueue(waybillWebhookDTO);
        }
    }

    @Transactional
    public void onUpdateQueue(WaybillWebhookDTO waybillWebhookDTO) {
        Waybill waybill = waybillRepository.findFirstByWaybillLocalId(waybillWebhookDTO.getWaybillLocalId()).orElseGet(Waybill::new);
        if (waybill.getId() == null) {
            Company consignee = companyRepository.findByTinAndDeletedIsFalse(waybillWebhookDTO.getConsigneeTinOrPinfl()).orElse(null);
            if (consignee == null) {
                return;
            }
            Company consignor = companyRepository.findByTinAndDeletedIsFalse(waybillWebhookDTO.getConsignorTinOrPinfl()).orElse(null);
            if (consignor == null) {
                return;
            }
            if (consignee.getOwner() != null) {
                if (CompanyStatus.ACTIVE != consignee.getStatus()) {
                    return;
                }
            } else if (consignor.getOwner() != null) {
                if (CompanyStatus.ACTIVE != consignor.getStatus()) {
                    return;
                }
            } else {
                return;
            }
            waybill.setWaybillLocalId(waybillWebhookDTO.getWaybillLocalId());
            waybill.setWaybillType(WaybillType.INCOMING);
            waybill.setTransportType(TransportType.fromCode(waybillWebhookDTO.getWaybillLocalType()));
            waybill.setWaybillNo(waybillWebhookDTO.getWaybillNo());
            waybill.setWaybillDate(waybillWebhookDTO.getWaybillDate());
            waybill.setContractNo(waybillWebhookDTO.getContractNo());
            waybill.setContractDate(waybillWebhookDTO.getContractDate());
            waybill.setConsignorTinOrPinfl(waybillWebhookDTO.getConsignorTinOrPinfl());
            waybill.setConsignorName(waybillWebhookDTO.getConsignorName());
            waybill.setConsignorBranchName(waybillWebhookDTO.getConsignorBranchName());
            waybill.setConsignorBranchCode(waybillWebhookDTO.getConsignorBranchCode());
            waybill.setConsigneeTinOrPinfl(waybillWebhookDTO.getConsigneeTinOrPinfl());
            waybill.setConsigneeName(waybillWebhookDTO.getConsigneeName());
            waybill.setConsigneeBranchName(waybillWebhookDTO.getConsigneeBranchName());
            waybill.setConsigneeBranchCode(waybillWebhookDTO.getConsigneeBranchCode());
            waybill.setResponsiblePersonPinfl(waybillWebhookDTO.getResponsiblePersonPinfl());
            waybill.setResponsiblePersonFullName(waybillWebhookDTO.getResponsiblePersonFullName());
            waybill.setResponsibleCompanyTin(waybillWebhookDTO.getResponsibleCompanyTin());
            waybill.setResponsibleCompanyName(waybillWebhookDTO.getResponsibleCompanyName());
            waybill.setResponsibleCompanyBranchCode(waybillWebhookDTO.getResponsibleCompanyBranchCode());
            waybill.setResponsibleCompanyBranchName(waybillWebhookDTO.getResponsibleCompanyBranchName());
            waybillRepository.save(waybill);
        }
        waybill.setStatus(WaybillStatus.fromCode(waybillWebhookDTO.getCurrentStateId()));
        String sign = null;
        try {
            sign = this.getSign(waybillWebhookDTO.getConsignorTinOrPinfl(), waybillWebhookDTO.getWaybillLocalId(), waybillWebhookDTO.getCurrentStateId(), true);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        if (!StringUtils.hasText(sign)) {
            try {
                sign = this.getSign(waybillWebhookDTO.getConsigneeTinOrPinfl(), waybillWebhookDTO.getWaybillLocalId(), waybillWebhookDTO.getCurrentStateId(), false);
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
        }
        if (StringUtils.hasText(sign)) {
            Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
            if (pkcs7Dto != null && pkcs7Dto.isSuccess() && pkcs7Dto.getPkcs7Info() != null && !CollectionUtils.isEmpty(pkcs7Dto.getPkcs7Info().getSigners())) {
                List<WaybillExecutor> executors = new ArrayList<>();
                for (SignerDto signerDto : pkcs7Dto.getPkcs7Info().getSigners()) {
                    try {
                        SignatureData signatureData = parseSignatureData(signerDto);
                        WaybillExecutor executor = waybillExecutorRepository.findFirstByWaybillIdAndOperationNumber(waybill.getId(), signatureData.getNumber()).orElseGet(WaybillExecutor::new);
                        executor.setWaybillId(waybill.getId());
                        executor.setWaybill(waybill);
                        executor.setStatus(waybill.getStatus());
                        executor.setOperationNumber(signatureData.getNumber());
                        executor.setOperationDate(signatureData.getDateTime());
                        executor.setOperationBy(signatureData.getOwner());
                        executors.add(executor);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                waybillExecutorRepository.saveAll(executors);
            } else {
                log.debug("JSON: {}", pkcs7Dto);
            }
            waybill.setSignatureContent(sign);
        } else {
            log.error("Waybill sign is null. WaybillId Id:{}", waybillWebhookDTO.getWaybillLocalId());
            return;
        }
        log.info("---------Waybill saved successfully from CALL BACK. id: {}---------", waybill.getWaybillLocalId());
        waybillRepository.save(waybill);
    }

    private SignatureData parseSignatureData(SignerDto signerDto) {
        final CertificateDto certificateDto = signerDto.getCertificate().getFirst();
        SignatureData data = new SignatureData();
        data.setNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
        data.setOwner(certificateDto.getSubjectInfo().getFullName());
        if (StringUtils.hasText(signerDto.getTimeStampInfo().getTime())) {
            String time = signerDto.getTimeStampInfo().getTime();
            try {
                data.setDateTime(LocalDateTime.parse(time, time.contains("-") ? DateUtils.DATE_TIME_HYPHEN_FORMATTER : DateUtils.DATE_TIME_FORMATTER));
            } catch (Exception e) {
                log.error("Waybill signer date time: {}", time);
                log.error(e.getMessage(), e);
            }
        }
        return data;
    }

    public WaybillDTO convertFromSign(String sign) {
        log.info("=========================== Start convert sign ===========================");
        try {
            Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
            if (pkcs7Dto != null && pkcs7Dto.isSuccess()) {
                return this.parseBase64(pkcs7Dto.getPkcs7Info().getDocumentBase64());
            } else {
                log.error("Parse sign error: {}", pkcs7Dto.getMessage());
            }
        } catch (Exception e) {
            log.error("Parse sign error: {}", e.getMessage());
            throw validationService.badRequestExceptionThrow(e.getMessage()).get();
        }
        return null;
    }

    private WaybillDTO parseBase64(String base64) {
        final String json = new String(Base64.getDecoder().decode(base64));
        WaybillDTO result = null;
        try {
            result = objectMapper.readValue(json, WaybillDTO.class);
        } catch (Exception ex) {
            log.error("JSON: {}", json);
            if (ex instanceof InvalidFormatException) {
                log.error("ERROR: {}", ex.getMessage());
                throw validationService.badRequestExceptionThrow(ex.getMessage(), "Невозможно открыть документ. В товарно-транспортная накладная допущены ошибки").get();
            } else if (ex instanceof IOException) {
                log.error("Waybill sign parse error:{}", ex.getMessage(), ex);
            }
        }
        return result;
    }

    public String getSign(String tinOrPinfl, String waybillLocalId, Integer currentStateId, boolean isConsignor) {
        String sign = null;
        try {
            sign = facturaRestClient.getWaybillSign(waybillLocalId, tinOrPinfl, WaybillStatus.fromCode(currentStateId), isConsignor);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return sign;
    }
}