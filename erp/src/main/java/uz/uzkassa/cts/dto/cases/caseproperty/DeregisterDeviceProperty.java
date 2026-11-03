package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.FiscalModule;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.domain.cases.CaseFile;
import uz.uzkassa.cts.dto.applicationfile.ApplicationTemplateDTO;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.dto.payload.SmartPosRemoveDevicePayload;
import uz.uzkassa.cts.enums.ApplicationTemplateType;
import uz.uzkassa.cts.enums.DeregisterReason;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqFMReportProducer;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqSmartPosProducer;
import uz.uzkassa.cts.repository.CaseFileRepository;
import uz.uzkassa.cts.repository.FiscalModuleRepository;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.FileUtils;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Component
public class DeregisterDeviceProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private FiscalizationService fiscalizationService;
    @Autowired
    @JsonIgnore
    private FiscalizationRepository fiscalizationRepository;
    @Autowired
    @JsonIgnore
    private FiscalModuleRepository fiscalModuleRepository;
    @Autowired
    @JsonIgnore
    private RabbitMqSmartPosProducer rabbitMqSmartPosProducer;
    @Autowired
    @JsonIgnore
    private OfdApplicationService ofdApplicationService;
    @Autowired
    @JsonIgnore
    private ContractService contractService;
    @Autowired
    @JsonIgnore
    private OneTimeOrderOldService oneTimeOrderOldService;
    @Autowired
    @JsonIgnore
    private CaseFileRepository caseFileRepository;
    @Autowired
    @JsonIgnore
    private ServicePaymentHistoryCronService servicePaymentHistoryCronService;
    @Autowired
    @JsonIgnore
    private CustomerOldService customerOldService;
    @Autowired
    @JsonIgnore
    private RabbitMqFMReportProducer rabbitMqFMReportProducer;


    @NotNull
    private Long deviceId;
    @NotNull
    private Long customerId;
    @NotBlank
    private String deviceSerialNumber;
    @NotBlank
    private String fmSerialNumber;
    private boolean lossFiscalModule = false;
    private String templateType;
    @Enumerated(EnumType.STRING)
    private DeregisterReason reason;
    private LocalDate currentDate = LocalDate.now();
    private Long caseId;
    private int step;
    private static Long ONE_TIME_SERVICE_ID = 18L;

    @Override
    @Transactional
    public void accept() {
        // lossFiscalModule == true bo`lsa usluga yaratilib factura ham chiqishi kerak;
        // 1-step. api/fiscalizations/download/application/template  - download template
        // 2-step. /api/applications/reject
        try {
            if (step == 1) {
                Customer customer = customerOldService.findById(customerId);
                servicePaymentHistoryCronService.recalculateHistory(customer.getTin(), deviceSerialNumber, LocalDate.now());
            } else if (step == 2) {
                FiscalModule fiscalModule = fiscalModuleRepository.findFirstBySerialNumber(this.fmSerialNumber);
                Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndDeviceIdAndFiscalModuleId(this.customerId, this.deviceId, fiscalModule.getId());
                CaseFile file = caseFileRepository.findByCaseIdAndDocumentType(caseId, DocumentType.APPLICATION)
                        .orElseThrow(() -> new BadRequestException("File not Found with DocumentType" + DocumentType.APPLICATION));
                MultipartFile multipart = FileUtils.getMultipartFileFromUrl(file.getFileUrl());
                ofdApplicationService.reject(multipart, fiscalization.getOfdApplicationId());
                rabbitMqSmartPosProducer.removeDevice(new SmartPosRemoveDevicePayload(deviceSerialNumber));
                rabbitMqFMReportProducer.send(new Payload(caseId));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> generateTemplate() {
        // 1-step. api/fiscalizations/download/application/template  - download template
        return Collections.singletonList(fiscalizationService.downloadApplicationTemplate(mapToTemplateDto()));
    }

    private ApplicationTemplateDTO mapToTemplateDto() {
        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndDeviceId(this.customerId, this.deviceId);
        ApplicationTemplateDTO applicationTemplateDTO = new ApplicationTemplateDTO();
        applicationTemplateDTO.setFiscalizationId(fiscalization.getId());
        applicationTemplateDTO.setReason(reason);
        applicationTemplateDTO.setOfdFileId(fiscalization.getOfdApplicationId());
        applicationTemplateDTO.setCurrentDate(currentDate);
        applicationTemplateDTO.setTemplateType(ApplicationTemplateType.DEREGISTER);
        return applicationTemplateDTO;
    }


    @Override
    @JsonIgnore
    public boolean is2Step() {
        return true;
    }
}
