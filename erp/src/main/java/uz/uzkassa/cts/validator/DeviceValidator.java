package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.device.DeviceDTO;
import uz.uzkassa.cts.repository.DeviceRepository;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeviceValidator extends BaseValidator implements ValidatorStrategy<DeviceDTO> {

    DeviceRepository deviceRepository;

    public DeviceValidator(LocalizationMessage localizationMessage, DeviceRepository deviceRepository) {
        super(localizationMessage);
        this.deviceRepository = deviceRepository;
    }

    @Override
    public void validate(DeviceDTO dto) {
        validateRequiredField(dto.getSerialNumber(), SERIAL_NUMBER);
        validateRequiredField(dto.getModelId(), DEVICE_MODEL);
        validateExistsInDatabase(deviceRepository.existsBySerialNumberAndModelId(dto.getSerialNumber(), dto.getModelId()), DEVICE, SERIAL_NUMBER, dto.getSerialNumber());
    }
}
