package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.domain.OfdApplication;
import uz.uzkassa.cts.dto.applicationfile.ApplicationTemplateDTO;
import uz.uzkassa.cts.dto.cases.request.CaseLossModuleDto;
import uz.uzkassa.cts.dto.contract.response.ContactResponseDto;
import uz.uzkassa.cts.dto.fm.FiscalSwapFiscalModuleDTO;
import uz.uzkassa.cts.dto.ofdapplication.SwapFiscalModuleDTO;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.enums.ApplicationTemplateType;
import uz.uzkassa.cts.enums.Reason;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqFMReportProducer;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.repository.OfdApplicationRepository;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Component
public class FiscalModuleReplacementProperty implements CaseAction {
    @Autowired
    @JsonIgnore
    private OfdApplicationService ofdApplicationService;
    @Autowired
    @JsonIgnore
    private FiscalizationService fiscalizationService;
    @Autowired
    @JsonIgnore
    private ContractService contractService;
    @Autowired
    @JsonIgnore
    private OneTimeOrderOldService oneTimeOrderOldService;
    @Autowired
    @JsonIgnore
    private ThymeleafService thymeleafService;
    @Autowired
    @JsonIgnore
    private FiscalizationRepository fiscalizationRepository;
    @Autowired
    @JsonIgnore
    private OfdApplicationRepository ofdApplicationRepository;
    @Autowired
    @JsonIgnore
    private RabbitMqFMReportProducer rabbitMqFMReportProducer;


    @NotNull
    private Long deviceId;
    @NotNull
    private Long customerId;
    @NotBlank
    private String fiscalModuleSerial;
    @NotBlank
    private String newFiscalModuleSerial;
    @NotBlank
    private String deviceSerialNumber;
    private Reason reason;
    private String reasonText;
    private boolean lossFiscalModule = false;
    private static Long ONE_TIME_SERVICE_ID = 18L;
    private Long caseId;

    @Override
    @Transactional
    public void accept() {
        // ZAYAVLENIYA Tashah KERAK --= DILSHODDAN SORAYMIZ
        // lossFiscalModule == true bo`lsa usluga yaratilib factura ham chiqishi kerak;
        //1- step: /api/applications/swap/fiscal/module PUT
        Long id = ofdApplicationService.swapFiscalModule(mapToSwapDto());
        //2- step: /api/fiscalizations/swap/fiscal/module PUT
        fiscalizationService.swapFiscalModule(mapToFiscalModuleDto(id));
        rabbitMqFMReportProducer.send(new Payload(caseId));
    }

    private FiscalSwapFiscalModuleDTO mapToFiscalModuleDto(Long ofdApplicationId) {
        Fiscalization fiscalization = fiscalizationRepository.findFirstByOfdApplicationId(ofdApplicationId);
        FiscalSwapFiscalModuleDTO dto = new FiscalSwapFiscalModuleDTO();
        dto.setFiscalModuleSerial(newFiscalModuleSerial);
        dto.setFiscalizationId(fiscalization.getId());
        dto.setReason(reason.getCode());
        return dto;
    }

    private SwapFiscalModuleDTO mapToSwapDto() {
        SwapFiscalModuleDTO swapFiscalModuleDTO = new SwapFiscalModuleDTO();
        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndDeviceId(this.customerId, this.deviceId);
        OfdApplication ofdApplication = ofdApplicationRepository.findById(fiscalization.getOfdApplicationId()).orElse(null);
        if (ofdApplication != null && !Objects.equals(ofdApplication.getCustomerId(), this.customerId)) {
            throw new BadRequestException("Этот ИД(" + this.customerId + ") клиента фискализации не соответствует ИД(" + ofdApplication.getCustomerId() + ") клиента Заявления.");
        }
        swapFiscalModuleDTO.setApplicationId(fiscalization.getOfdApplicationId());
        swapFiscalModuleDTO.setFiscalModuleSerial(newFiscalModuleSerial);
        swapFiscalModuleDTO.setReason(reason);
        return swapFiscalModuleDTO;
    }

    @Override
    public List<String> generateTemplate() {
        List<String> templates = new ArrayList<>();
        templates.add(fiscalizationService.downloadApplicationTemplate(mapToTemplateDto()));
        if (lossFiscalModule) {
            templates.add(thymeleafService.lossModule(mapToTemplateDtoForModuleLost()));
        }
        return templates;
    }

    private ApplicationTemplateDTO mapToTemplateDto() {
        Fiscalization fiscalization = fiscalizationService.findFirstByDeviceIdOrderByIdDesc(this.deviceId);
        ApplicationTemplateDTO applicationTemplateDTO = new ApplicationTemplateDTO();
        applicationTemplateDTO.setFiscalizationId(fiscalization.getId());
        applicationTemplateDTO.setTemplateType(ApplicationTemplateType.FISCAL_MODULE_REPLACEMENT_ACT);
        applicationTemplateDTO.setNewSerialNumber(newFiscalModuleSerial);
        applicationTemplateDTO.setFiscalModuleSerialNumber(fiscalModuleSerial);
        applicationTemplateDTO.setDescription(reason.getName());
        return applicationTemplateDTO;
    }

    private CaseLossModuleDto mapToTemplateDtoForModuleLost() {
        ContactResponseDto contactResponseDto = contractService.getCustomerActiveContractForService(customerId);
        Fiscalization fiscalization = fiscalizationService.findFirstByDeviceIdOrderByIdDesc(this.deviceId);
        CaseLossModuleDto caseLossModuleDto = new CaseLossModuleDto();
        caseLossModuleDto.setTin(contactResponseDto.getCustomerTin());
        caseLossModuleDto.setDate(LocalDate.now());
        caseLossModuleDto.setFiscalNumber(this.fiscalModuleSerial);
        caseLossModuleDto.setDeviceNumber(fiscalization.getDevice().getSerialNumber());
        caseLossModuleDto.setDirector(contactResponseDto.getCustomerDirectorFullName());
        caseLossModuleDto.setCompanyName(contactResponseDto.getCustomerName());
        return caseLossModuleDto;
    }

    @Override
    @JsonIgnore
    public boolean is2Step() {
        return false;
    }
}
