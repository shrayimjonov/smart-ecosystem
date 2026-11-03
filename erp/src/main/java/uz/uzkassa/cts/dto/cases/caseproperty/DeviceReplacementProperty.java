package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.dto.applicationfile.ApplicationTemplateDTO;
import uz.uzkassa.cts.dto.fm.FiscalizationSyncDTO;
import uz.uzkassa.cts.dto.ofdapplication.SwapDeviceDTO;
import uz.uzkassa.cts.dto.payload.SmartPosRemoveDevicePayload;
import uz.uzkassa.cts.enums.ApplicationTemplateType;
import uz.uzkassa.cts.enums.ConstantReplacementReason;
import uz.uzkassa.cts.enums.Reason;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqSmartPosProducer;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.service.FiscalizationService;
import uz.uzkassa.cts.service.OfdApplicationService;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Component
public class DeviceReplacementProperty implements CaseAction {
    @Autowired
    @JsonIgnore
    private OfdApplicationService ofdApplicationService;
    @Autowired
    @JsonIgnore
    private FiscalizationService fiscalizationService;
    @Autowired
    @JsonIgnore
    private FiscalizationRepository fiscalizationRepository;
    @Autowired
    @JsonIgnore
    private RabbitMqSmartPosProducer rabbitMqSmartPosProducer;

    @NotNull
    private Long deviceId;
    @NotNull
    private Long customerId;
    @NotBlank
    private String newDeviceSerialNumber;
    @NotBlank
    private String deviceSerialNumber;
    @Enumerated(EnumType.STRING)
    private ConstantReplacementReason otherReason;
    private String otherReasonDesc;

    @Override
    public void accept() {
        //1 -step: /api/applications/swap/device PUT
        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndDeviceId(this.customerId, this.deviceId);
        SwapDeviceDTO swapDeviceDTO = mapToSwapDto(fiscalization);
        if (swapDeviceDTO.getApplicationId() != null) {
            ofdApplicationService.swapDevice(mapToSwapDto(fiscalization));
        }
        //2-step:  /api/fiscalizations/swap/device  PUT
        fiscalizationService.swapDevice(mapToFiscalSwapDto(fiscalization));
        fiscalizationService.syncWithSmartpos(new FiscalizationSyncDTO(fiscalization.getId()));
        rabbitMqSmartPosProducer.removeDevice(new SmartPosRemoveDevicePayload(deviceSerialNumber));
    }

    private SwapDeviceDTO mapToSwapDto(Fiscalization fiscalization) {
        SwapDeviceDTO dto = new SwapDeviceDTO();
        dto.setApplicationId(fiscalization.getOfdApplicationId());
        dto.setDeviceSerial(newDeviceSerialNumber);
        return dto;
    }

    private uz.uzkassa.cts.dto.fm.SwapDeviceDTO mapToFiscalSwapDto(Fiscalization fiscalization) {
        uz.uzkassa.cts.dto.fm.SwapDeviceDTO dto = new uz.uzkassa.cts.dto.fm.SwapDeviceDTO();
        dto.setDeviceSerial(newDeviceSerialNumber);
        dto.setFiscalizationId(fiscalization.getId());
        dto.setReason(Reason.OTHER.getCode());
        return dto;
    }


    @Override
    public List<String> generateTemplate() {
        return Collections.singletonList(fiscalizationService.downloadApplicationTemplate(mapToTemplateDto()));
    }

    private ApplicationTemplateDTO mapToTemplateDto() {
        ApplicationTemplateDTO applicationTemplateDTO = new ApplicationTemplateDTO();
        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndDeviceId(this.customerId, this.deviceId);
        applicationTemplateDTO.setFiscalizationId(fiscalization.getId());
        applicationTemplateDTO.setTemplateType(ApplicationTemplateType.DEVICE_REPLACEMENT_ACT);
        applicationTemplateDTO.setNewSerialNumber(newDeviceSerialNumber);
        if (otherReason != null && ConstantReplacementReason.OTHER == otherReason) {
            applicationTemplateDTO.setOtherReason(otherReasonDesc);
        } else if (otherReason != null) {
            applicationTemplateDTO.setOtherReason(otherReason.getName());
        }
        return applicationTemplateDTO;
    }

    @Override
    @JsonIgnore
    public boolean is2Step() {
        return false;
    }
}
