package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.warehouse.WarehouseDTO;
import uz.uzkassa.cts.repository.WarehouseRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class WarehouseValidator extends BaseValidator implements ValidatorStrategy<WarehouseDTO> {

    WarehouseRepository warehouseRepository;

    public WarehouseValidator(LocalizationMessage localizationMessage, WarehouseRepository warehouseRepository) {
        super(localizationMessage);
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public void validate(WarehouseDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getManagerId(), MANAGER);

        if (dto.getId() == null) {
            validateExistsInDatabase(warehouseRepository.existsByNameIsIgnoreCaseAndBranchIdAndDeletedIsFalse(dto.getName().trim(), dto.getBranchId()), WAREHOUSE, NAME, dto.getName());
        } else {
            validateExistsInDatabase(warehouseRepository.existsByNameIsIgnoreCaseAndBranchIdAndDeletedIsFalseAndIdNot(dto.getName(), dto.getBranchId(), dto.getId()), WAREHOUSE, NAME, dto.getName());
        }
    }
}
