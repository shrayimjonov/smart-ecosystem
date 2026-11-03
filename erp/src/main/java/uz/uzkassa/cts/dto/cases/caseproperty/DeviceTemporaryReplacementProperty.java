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
import uz.uzkassa.cts.dto.applicationfile.ApplicationTemplateDTO;
import uz.uzkassa.cts.dto.fm.FiscalizationSyncDTO;
import uz.uzkassa.cts.dto.ofdapplication.SwapDeviceDTO;
import uz.uzkassa.cts.dto.payload.SmartPosRemoveDevicePayload;
import uz.uzkassa.cts.enums.ApplicationTemplateType;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqSmartPosProducer;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.service.FiscalizationService;
import uz.uzkassa.cts.service.OfdApplicationService;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Component
public class DeviceTemporaryReplacementProperty implements CaseAction {
    @Autowired
    @JsonIgnore
    private OfdApplicationService ofdApplicationService;
    @Autowired
    @JsonIgnore
    private FiscalizationRepository fiscalizationRepository;
    @Autowired
    @JsonIgnore
    private FiscalizationService fiscalizationService;
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
    @NotBlank
    private String reason;
    private String description;

    @Override
    @Transactional
    public void accept() {
        //Step 1da status processing
        //1-: /api/applications/swap/device
        //2-: /api/fiscalizations/swap/device
        //Ikkinchi marta yana shu action takrorlanadi, step 2 ga o'zgaradi.
        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndDeviceId(this.customerId, this.deviceId);
        //1 -step: /api/applications/swap/device PUT
        SwapDeviceDTO swapDeviceDTO = mapToSwapDto(fiscalization);
        if (swapDeviceDTO.getApplicationId() != null) {
            ofdApplicationService.swapDevice(swapDeviceDTO);
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
        dto.setFiscalizationId(fiscalization.getId());
        dto.setDeviceSerial(newDeviceSerialNumber);
        dto.setReason(reason);
        return dto;
    }

    @Override
    public List<String> generateTemplate() {
        return Collections.singletonList(fiscalizationService.downloadApplicationTemplate(mapToTemplateDto()));
    }

    private ApplicationTemplateDTO mapToTemplateDto() {
        return fiscalizationRepository.findFirstByDeviceIdOrderByIdDesc(this.deviceId).map(fiscalization -> {
            ApplicationTemplateDTO applicationTemplateDTO = new ApplicationTemplateDTO();
            applicationTemplateDTO.setFiscalizationId(fiscalization.getId());
            applicationTemplateDTO.setTemplateType(ApplicationTemplateType.DEVICE_REPLACEMENT_TEMPORARY_ACT);
            applicationTemplateDTO.setNewSerialNumber(newDeviceSerialNumber);
            return applicationTemplateDTO;
        }).orElseThrow(() -> new NotFoundException(String.format("Device with id %s not found", this.deviceId)));
    }

    @Override
    @JsonIgnore
    public boolean is2Step() {
        return false;
    }
}
