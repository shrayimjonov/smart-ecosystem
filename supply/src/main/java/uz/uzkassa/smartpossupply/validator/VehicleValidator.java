package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleDTO;
import uz.uzkassa.smartpossupply.repository.VehicleRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class VehicleValidator extends BaseValidator implements ValidatorStrategy<VehicleDTO> {

    VehicleRepository vehicleRepository;

    public VehicleValidator(LocalizationMessage localizationMessage, VehicleRepository vehicleRepository) {
        super(localizationMessage);
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public void validate(VehicleDTO dto) {
        Long companyId = Optional.ofNullable(dto.getCompanyId()).orElseGet(SecurityUtils::getCurrentCompanyId);
        if (companyId == null) {
            throw new BadRequestException("Company is required");
        }
        if (StringUtils.hasText(dto.getTransportCompanyTin()) && !StringUtils.hasText(dto.getTransportCompanyName())) {
            throw new BadRequestException("Transport company name is required");
        }
        if (dto.getId() == null) {
            validateExistsInDatabase(vehicleRepository.existsByCompanyIdAndNameIgnoreCase(companyId, dto.getName()), VEHICLE, NAME, dto.getName());
            if (StringUtils.hasText(dto.getModel()) && StringUtils.hasText(dto.getRegNo())) {
                validateExistsInDatabase(vehicleRepository.existsByCompanyIdAndRegNoIgnoreCase(companyId, dto.getRegNo()), VEHICLE, REGISTRATION_NUMBER, dto.getName());
            }
        } else {
            validateExistsInDatabase(vehicleRepository.existsByCompanyIdAndNameIgnoreCaseAndIdNot(companyId, dto.getName(), dto.getId()), VEHICLE, NAME, dto.getName());
            if (StringUtils.hasText(dto.getModel()) && StringUtils.hasText(dto.getRegNo())) {
                validateExistsInDatabase(vehicleRepository.existsByCompanyIdAndRegNoIgnoreCaseAndIdNot(companyId, dto.getRegNo(), dto.getId()), VEHICLE, REGISTRATION_NUMBER, dto.getName());

            }
        }
    }
}
