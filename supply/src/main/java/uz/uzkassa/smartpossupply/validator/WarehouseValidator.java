package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.warehouse.WarehouseDTO;
import uz.uzkassa.smartpossupply.repository.WarehouseRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

import java.util.Objects;

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
        if (dto.getParentId() == null && dto.getId() == null) {
                validateExistsInDatabase(warehouseRepository.existsByBranchIdAndNameIgnoreCaseAndDeletedIsFalse(dto.getBranchId(), dto.getName()), WAREHOUSE, NAME, dto.getName());
                if (StringUtils.hasText(dto.getTruckModel()) && StringUtils.hasText(dto.getTruckRegNo())) {
                    validateExistsInDatabase(warehouseRepository.existsByBranch_CompanyIdAndTruckModelAndTruckRegNo(SecurityUtils.getCurrentCompanyId(), dto.getTruckModel(), dto.getTruckRegNo()), WAREHOUSE, TRUCK_MODEL, dto.getTruckModel());
                }
            }

        if (dto.getId() != null) {
            Warehouse warehouse = warehouseRepository.findById(dto.getId()).orElse(null);
            if (warehouse == null) {
                throw new NotFoundException("Warehouse not found");
            }
            if (!Objects.equals(warehouse.getBranchId(), dto.getBranchId()) && warehouseRepository.checkQtyInWarehouse(dto.getId())) {
                throw new BadRequestException("Обновление филиала не разрешено", "На этом складе уже есть товар");
            }
            validateExistsInDatabase(warehouseRepository.existsByBranchIdAndNameIgnoreCaseAndIdNotAndDeletedIsFalse(dto.getBranchId(), dto.getName(), dto.getId()), WAREHOUSE, NAME, dto.getName());
            if (dto.getId() != null && StringUtils.hasText(dto.getTruckModel()) && StringUtils.hasText(dto.getTruckRegNo())) {
                validateExistsInDatabase(warehouseRepository.existsByBranch_CompanyIdAndTruckModelAndTruckRegNoAndIdNot(SecurityUtils.getCurrentCompanyId(), dto.getTruckModel(), dto.getTruckRegNo(), dto.getId()), WAREHOUSE, TRUCK_MODEL, dto.getTruckModel());
            }
        }
    }
}
