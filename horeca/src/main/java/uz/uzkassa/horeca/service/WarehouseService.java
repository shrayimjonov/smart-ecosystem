package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.warehouse.Warehouse;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.warehouse.WarehouseCheckDTO;
import uz.uzkassa.horeca.dto.warehouse.WarehouseDTO;
import uz.uzkassa.horeca.dto.warehouse.WarehouseDetailDTO;
import uz.uzkassa.horeca.dto.warehouse.WarehouseListDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.StockTransferStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.interfaces.CrudAdvanced;
import uz.uzkassa.horeca.repository.StockTransferRepository;
import uz.uzkassa.horeca.repository.WarehouseItemRepository;
import uz.uzkassa.horeca.repository.WarehouseRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WarehouseService extends BaseService implements CrudAdvanced<Long, WarehouseDTO, WarehouseDetailDTO, WarehouseListDTO, BaseFilter, CommonStatus, CommonDTO> {

    WarehouseRepository warehouseRepository;
    WarehouseItemRepository warehouseItemRepository;
    StockTransferRepository stockTransferRepository;

    @Override
    public void validate(WarehouseDTO warehouseDTO) {
        Long branchId = getCurrentBranchId(warehouseDTO.getBranchId(), true);
        if (StringUtils.isEmpty(warehouseDTO.getName())) {
            throw badRequestExceptionThrow("Name is required").get();
        }
        if (warehouseDTO.getId() == null && warehouseRepository.existsByBranchIdAndDeletedIsFalseAndNameIgnoreCase(branchId, warehouseDTO.getName().trim())) {
            throw conflictExceptionThrow("Склад с таким названием " + warehouseDTO.getName() + " уже существует.").get();
        }
        if (warehouseDTO.getId() != null && warehouseRepository.existsByBranchIdAndDeletedIsFalseAndNameIgnoreCaseAndIdNot(branchId, warehouseDTO.getName().trim(), warehouseDTO.getId())) {
            throw conflictExceptionThrow("Склад с таким названием " + warehouseDTO.getName() + " уже существует.").get();
        }
        if (warehouseDTO.getDepartmentId() != null) {
            if (warehouseDTO.getId() == null && warehouseRepository.existsByBranchIdAndDepartmentIdAndDeletedIsFalse(branchId, warehouseDTO.getDepartmentId())) {
                throw badRequestExceptionThrow("Для выбранного цеха уже привязан склад. Чтобы изменить привязку склада с цехом, сначала удалите текущий склад цеха.").get();
            }
            if (warehouseDTO.getId() != null && warehouseRepository.existsByBranchIdAndDepartmentIdAndIdNotAndDeletedIsFalse(branchId, warehouseDTO.getDepartmentId(), warehouseDTO.getId())) {
                throw badRequestExceptionThrow("Для выбранного цеха уже привязан склад. Чтобы изменить привязку склада с цехом, сначала удалите текущий склад цеха.").get();
            }
        } /*else {
            if (warehouseRepository.existsByBranchIdAndDepartmentIdIsNullAndDeletedIsFalse(branchId)) {
                throw badRequestExceptionThrow("Для выбранного цеха уже привязан склад. Чтобы изменить привязку склада с цехом, сначала удалите текущий склад цеха.").get();
            }
        }*/
    }

    @Override
    @Transactional
    public Long create(WarehouseDTO warehouseDTO) {
        this.validate(warehouseDTO);
        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseDTO.getName().trim());
        warehouse.setManagerId(warehouseDTO.getManagerId());
        warehouse.setDepartmentId(warehouseDTO.getDepartmentId());
        warehouse.setBranchId(getCurrentBranchId(warehouseDTO.getBranchId(), true));
        warehouseRepository.save(warehouse);
        return warehouse.getId();
    }

    @Override
    public WarehouseDetailDTO get(Long id) {
        return warehouseRepository.findById(id).map(warehouse -> {
            WarehouseDetailDTO warehouseDetailDTO = new WarehouseDetailDTO();
            warehouseDetailDTO.setId(warehouse.getId());
            warehouseDetailDTO.setName(warehouse.getName());
            if (warehouse.getManagerId() != null) {
                warehouseDetailDTO.setManager(warehouse.getManager().toCommonDTO());
            }
            if (warehouse.getDepartmentId() != null) {
                warehouseDetailDTO.setDepartment(warehouse.getDepartment().toCommonDTO());
            }
            warehouseDetailDTO.setBranch(warehouse.getBranch().toCommonDTO());
            warehouseDetailDTO.setStatus(warehouse.getStatus());
            return warehouseDetailDTO;
        }).orElseThrow(notFoundExceptionThrow(Warehouse.class.getSimpleName(), "id", id));
    }

    @Override
    @Transactional
    public Long update(Long id, WarehouseDTO warehouseDTO) {
        warehouseDTO.setId(id);
        this.validate(warehouseDTO);
        if (warehouseDTO.getId() != null && stockTransferRepository.existsByFromWarehouseIdAndStatus(warehouseDTO.getId(), StockTransferStatus.IN_TRANSIT)) {
            return id;
        }
        return warehouseRepository.findById(id).map(warehouse -> {
            warehouse.setName(warehouseDTO.getName().trim());
            warehouse.setManagerId(warehouseDTO.getManagerId());
            warehouse.setDepartmentId(warehouseDTO.getDepartmentId());
            warehouseRepository.save(warehouse);
            return id;
        }).orElseThrow(notFoundExceptionThrow(Warehouse.class.getSimpleName(), "id", id));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (warehouseItemRepository.existsByWarehouseIdAndQtyIsGreaterThan(id, BigDecimal.ZERO)) {
            throw badRequestExceptionThrow(localizationService.localize("warehouse.cant.be.deleted")).get();
        }
        warehouseRepository.deleteById(id);
    }

    @Override
    public Page<WarehouseListDTO> getList(BaseFilter filter) {
        ResultList<Warehouse> resultList = warehouseRepository.getResultList(filter);
        List<WarehouseListDTO> result = resultList.getList().stream().map(warehouse -> {
            WarehouseListDTO warehouseListDTO = new WarehouseListDTO();
            warehouseListDTO.setId(warehouse.getId());
            warehouseListDTO.setName(warehouse.getName());
            if (warehouse.getManagerId() != null) {
                warehouseListDTO.setManager(warehouse.getManager().toCommonDTO());
            }
            if (warehouse.getDepartmentId() != null) {
                warehouseListDTO.setDepartment(warehouse.getDepartment().toCommonDTO());
            }
            warehouseListDTO.setBranch(warehouse.getBranch().toCommonDTO());
            warehouseListDTO.setStatus(warehouse.getStatus());
            return warehouseListDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return warehouseRepository.getResultList(filter).getList().stream().map(Warehouse::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        warehouseRepository.findById(id).map(warehouse -> {
            warehouse.setStatus(status);
            warehouseRepository.save(warehouse);
            return warehouse;
        }).orElseThrow(notFoundExceptionThrow(Warehouse.class.getSimpleName(), "id", id));
    }
}
