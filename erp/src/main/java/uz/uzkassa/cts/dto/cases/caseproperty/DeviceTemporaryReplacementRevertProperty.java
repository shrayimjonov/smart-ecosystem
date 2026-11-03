package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.dto.applicationfile.ApplicationTemplateDTO;
import uz.uzkassa.cts.dto.fm.FiscalizationSyncDTO;
import uz.uzkassa.cts.dto.ofdapplication.SwapDeviceDTO;
import uz.uzkassa.cts.enums.ApplicationTemplateType;
import uz.uzkassa.cts.repository.DeviceRepository;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.service.CaseService;
import uz.uzkassa.cts.service.FiscalizationService;
import uz.uzkassa.cts.service.OfdApplicationService;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Component
public class DeviceTemporaryReplacementRevertProperty implements CaseAction {
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
    private CaseService caseService;
    @Autowired
    @JsonIgnore
    private DeviceRepository deviceRepository;


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
    @NotNull
    private Long parentCaseId;

    @Override
    @Transactional
    public void accept() {
        //Step 1da status processing
        //1-: /api/applications/swap/device
        //2-: /api/fiscalizations/swap/device
        //Ikkinchi marta yana shu action takrorlanadi, step 2 ga o'zgaradi.

        //1 -step: /api/applications/swap/device PUT
        Device device = deviceRepository.findBySerialNumber(newDeviceSerialNumber);
        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndDeviceId(this.customerId, device.getId());
        SwapDeviceDTO swapDeviceDTO = mapToSwapDto(fiscalization);
        if (swapDeviceDTO.getApplicationId() != null) {
            ofdApplicationService.swapDevice(swapDeviceDTO);
        }
        //2-step:  /api/fiscalizations/swap/device  PUT
        fiscalizationService.swapDevice(mapToFiscalSwapDto(fiscalization));
        fiscalizationService.syncWithSmartpos(new FiscalizationSyncDTO(fiscalization.getId()));
    }

    private SwapDeviceDTO mapToSwapDto(Fiscalization fiscalization) {
        SwapDeviceDTO swapDeviceDTO = new SwapDeviceDTO();
        swapDeviceDTO.setApplicationId(fiscalization.getOfdApplicationId());
        swapDeviceDTO.setDeviceSerial(deviceSerialNumber);
        return swapDeviceDTO;
    }

    private uz.uzkassa.cts.dto.fm.SwapDeviceDTO mapToFiscalSwapDto(Fiscalization fiscalization) {
        uz.uzkassa.cts.dto.fm.SwapDeviceDTO swapDeviceDTO = new uz.uzkassa.cts.dto.fm.SwapDeviceDTO();
        swapDeviceDTO.setFiscalizationId(fiscalization.getId());
        swapDeviceDTO.setDeviceSerial(deviceSerialNumber);
        swapDeviceDTO.setReason(reason);
        return swapDeviceDTO;
    }

    @Override
    public List<String> generateTemplate() {
        return Collections.singletonList(fiscalizationService.downloadApplicationTemplate(mapToTemplateDto()));
    }

    private ApplicationTemplateDTO mapToTemplateDto() {
        Device device = deviceRepository.findBySerialNumber(newDeviceSerialNumber);
        return fiscalizationRepository.findFirstByDeviceIdOrderByIdDesc(device.getId()).map(fiscalization -> {
            ApplicationTemplateDTO applicationTemplateDTO = new ApplicationTemplateDTO();
            applicationTemplateDTO.setFiscalizationId(fiscalization.getId());
            applicationTemplateDTO.setTemplateType(ApplicationTemplateType.DEVICE_REPLACEMENT_TEMPORARY_ACT);
            applicationTemplateDTO.setNewSerialNumber(deviceSerialNumber);
            return applicationTemplateDTO;
        }).orElseThrow(() -> new NotFoundException("No device found with serial number " + newDeviceSerialNumber));

    }

    @Override
    @JsonIgnore
    public boolean is2Step() {
        return false;
    }
}
