package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.warehouse.Vehicle;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleAssignWarehouseDTO;
import uz.uzkassa.smartpossupply.dto.warehouse.WarehouseDTO;
import uz.uzkassa.smartpossupply.dto.warehouse.WarehouseDetailDTO;
import uz.uzkassa.smartpossupply.dto.warehouse.WarehouseListDTO;
import uz.uzkassa.smartpossupply.dto.warehouse.WarehouseLookUpDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.WarehouseLookUpOfflineFilter;
import uz.uzkassa.smartpossupply.mappers.UserMapper;
import uz.uzkassa.smartpossupply.repository.VehicleRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseRepository;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WarehouseService extends BaseService  {

    WarehouseRepository warehouseRepository;
    UserMapper userMapper;
    VehicleRepository vehicleRepository;
    ValidationContext validationContext;

    @Transactional
    public void createDefault(Branch branch) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName("Основной склад");
        warehouse.setMain(true);
        warehouse.setBranchId(branch.getId());
        warehouse.setStatus(CommonStatus.ACTIVE);
        warehouseRepository.save(warehouse);
    }

    @Transactional
    public Long create(WarehouseDTO warehouseDTO) {
        return this.createAndReturn(warehouseDTO).getId();
    }

    private Warehouse createAndReturn(WarehouseDTO warehouseDTO) {
        validationContext.validate(warehouseDTO);
        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseDTO.getName());
        warehouse.setBranchId(warehouseDTO.getBranchId());
        warehouse.setOwnerId(warehouseDTO.getOwnerId());
        warehouse.setParentId(warehouseDTO.getParentId());
        warehouse.setTruckRegNo(warehouseDTO.getTruckRegNo());
        warehouse.setTruckModel(warehouseDTO.getTruckModel());
        warehouse.setTrailerModel(warehouseDTO.getTrailerModel());
        warehouse.setTrailerRegNo(warehouseDTO.getTrailerRegNo());
        warehouse.setTrailerType(warehouseDTO.getTrailerType());
        warehouse.setTransportCompanyName(warehouseDTO.getTransportCompanyName());
        warehouse.setTransportCompanyTin(warehouseDTO.getTransportCompanyTin());
        warehouse.setAddress(addressMapper.toEntity(warehouseDTO.getAddress()));
        warehouse.setStatus(CommonStatus.ACTIVE);
        warehouse.setMain(warehouseDTO.isMain());
        warehouseRepository.save(warehouse);
        if (warehouse.isMain()) {
            warehouseRepository.updateSetMainFalseByBranchId(warehouseDTO.getBranchId(), warehouse.getId());
        }
        return warehouse;
    }

    @Transactional
    public Long update(Long id, WarehouseDTO warehouseDTO) {
        return warehouseRepository.findById(id).map(warehouse -> {
            warehouseDTO.setId(id);
            validationContext.validate(warehouseDTO);
            warehouse.setName(warehouseDTO.getName());
            warehouse.setBranchId(warehouseDTO.getBranchId());
            warehouse.setOwnerId(warehouseDTO.getOwnerId());
            warehouse.setMain(warehouseDTO.isMain());
            warehouse.setParentId(warehouseDTO.getParentId());
            warehouse.setTruckRegNo(warehouseDTO.getTruckRegNo());
            warehouse.setTruckModel(warehouseDTO.getTruckModel());
            warehouse.setTrailerModel(warehouseDTO.getTrailerModel());
            warehouse.setTrailerRegNo(warehouseDTO.getTrailerRegNo());
            warehouse.setTrailerType(warehouseDTO.getTrailerType());
            warehouse.setTransportCompanyName(warehouseDTO.getTransportCompanyName());
            warehouse.setTransportCompanyTin(warehouseDTO.getTransportCompanyTin());
            warehouse.setAddress(addressMapper.toEntity(warehouseDTO.getAddress()));
            warehouseRepository.save(warehouse);
            if (warehouseDTO.isMain()) {
                warehouseRepository.updateSetMainFalseByBranchId(warehouseDTO.getBranchId(), id);
            }
            return warehouse.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Warehouse.class.getSimpleName(), "id", id));
    }

    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(Optional.ofNullable(filter.getStatus()).orElse(CommonStatus.ACTIVE.getCode()));
        return warehouseRepository.lookUp(filter)
            .stream()
            .map(Warehouse::toCommonDTO)
            .toList();
    }

    public List<WarehouseLookUpDTO> lookUpForOffline(WarehouseLookUpOfflineFilter filter) {
        return warehouseRepository.findAllByBranchIdInAndStatusAndDeletedIsFalse(filter.getBranchIds(),CommonStatus.ACTIVE)
                .stream()
                .map(warehouse -> {
                            WarehouseLookUpDTO lookUpDTO = new WarehouseLookUpDTO();
                            lookUpDTO.setId(warehouse.getId());
                            lookUpDTO.setName(warehouse.getName());
                            lookUpDTO.setBranchId(warehouse.getBranchId());
                            return lookUpDTO;
                        }
                ).toList();
    }

    public Page<WarehouseListDTO> getList(BaseFilter filter) {
        ResultList<Warehouse> resultList = warehouseRepository.getResultList(filter);
        List<WarehouseListDTO> result = resultList
                .getList()
                .stream()
                .map(warehouse -> {
                    WarehouseListDTO warehouseListDTO = new WarehouseListDTO();
                    warehouseListDTO.setId(warehouse.getId());
                    warehouseListDTO.setName(warehouse.getName());
                    warehouseListDTO.setBranch(warehouse.getBranch().toCommonDTO());
                    if (warehouse.getOwnerId() != null) {
                        warehouseListDTO.setOwner(userMapper.toOwnerDTO(warehouse.getOwner()));
                    }
                    warehouseListDTO.setStatus(warehouse.getStatus());
                    warehouseListDTO.setMain(warehouse.isMain());
                    if (warehouse.getParentId() != null) {
                        warehouseListDTO.setParent(warehouse.getParent().toCommonDTO());
                    }
                    warehouseListDTO.setTruckModel(warehouse.getTruckModel());
                    warehouseListDTO.setHasChild(!CollectionUtils.isEmpty(warehouse.getChildren()));
                    warehouseListDTO.setTruckRegNo(warehouse.getTruckRegNo());
                    return warehouseListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void delete(Long id) {
        if (warehouseRepository.checkQtyInWarehouse(id)) {
            throw validationService.badRequestExceptionThrow("Удаление филиала не разрешено", "На этом складе уже есть товар").get();
        }
        warehouseRepository.deleteById(id);
    }

    public WarehouseDetailDTO get(Long id) {
        return warehouseRepository.getDetail(id).map(warehouse -> {
            WarehouseDetailDTO warehouseDetailDTO = new WarehouseDetailDTO();
            warehouseDetailDTO.setId(warehouse.getId());
            warehouseDetailDTO.setName(warehouse.getName());
            warehouseDetailDTO.setMain(warehouse.isMain());
            warehouseDetailDTO.setBranch(warehouse.getBranch().toCommonDTO());
            warehouseDetailDTO.setStatus(warehouse.getStatus());
            if (warehouse.getOwnerId() != null) {
                warehouseDetailDTO.setOwner(userMapper.toOwnerDTO(warehouse.getOwner()));
            }
            if (warehouse.getParentId() != null) {
                warehouseDetailDTO.setParent(warehouse.getParent().toCommonDTO());
            }
            if (!CollectionUtils.isEmpty(warehouse.getChildren())) {
                for (Warehouse child : warehouse.getChildren()) {
                    WarehouseDetailDTO childDetaliDTO = new WarehouseDetailDTO();
                    childDetaliDTO.setId(child.getId());
                    childDetaliDTO.setName(child.getName());
                    childDetaliDTO.setMain(child.isMain());
                    childDetaliDTO.setBranch(child.getBranch().toCommonDTO());
                    childDetaliDTO.setStatus(child.getStatus());
                    if (child.getOwnerId() != null) {
                        childDetaliDTO.setOwner(userMapper.toOwnerDTO(child.getOwner()));
                    }
                    if (child.getParentId() != null) {
                        childDetaliDTO.setParent(child.getParent().toCommonDTO());
                    }
                    childDetaliDTO.setTruckModel(child.getTruckModel());
                    childDetaliDTO.setTruckRegNo(child.getTruckRegNo());
                    warehouseDetailDTO.addChild(childDetaliDTO);
                }
            }
            warehouseDetailDTO.setTruckModel(warehouse.getTruckModel());
            warehouseDetailDTO.setTruckRegNo(warehouse.getTruckRegNo());
            warehouseDetailDTO.setTrailerRegNo(warehouse.getTrailerRegNo());
            warehouseDetailDTO.setTrailerModel(warehouse.getTrailerModel());
            warehouseDetailDTO.setTransportCompanyName(warehouse.getTransportCompanyName());
            warehouseDetailDTO.setTransportCompanyTin(warehouse.getTransportCompanyTin());
            warehouseDetailDTO.setTrailerType(warehouse.getTrailerType());
            warehouseDetailDTO.setAddress(addressMapper.toDetailDTO(warehouse.getAddress()));
            return warehouseDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Warehouse.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        warehouseRepository.findById(id).ifPresentOrElse(warehouse -> {
            warehouse.setStatus(status);
            warehouseRepository.save(warehouse);
        }, () -> validationService.entityNotFoundExceptionThrow(Warehouse.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void addVehicle(VehicleAssignWarehouseDTO assignWarehouseDTO) {
        vehicleRepository.findById(assignWarehouseDTO.getVehicleId()).ifPresentOrElse(vehicle -> {
            List<Warehouse> warehouses = List.of();
            if (assignWarehouseDTO.getWarehouseIds() != null) {
                warehouses = warehouseRepository.findAllById(assignWarehouseDTO.getWarehouseIds());
            } else if (assignWarehouseDTO.isAddAll()) {
                warehouses = warehouseRepository.getAllByCompanyId(vehicle.getCompanyId());
            }
            for (Warehouse warehouse : warehouses) {
                Warehouse wow;
                wow = warehouseRepository.findFirstByVehicleIdAndParentIdAndDeletedIsFalse(vehicle.getId(), warehouse.getId()).orElse(null);
                if (wow == null) {
                    wow = warehouseRepository.findFirstByTruckRegNoAndTruckModelAndParentIdAndDeletedIsFalse(vehicle.getRegNo(), vehicle.getModel(), warehouse.getId()).orElse(null);
                }
                if (wow == null) {
                    WarehouseDTO warehouseDTO = new WarehouseDTO();
                    warehouseDTO.setParentId(warehouse.getId());
                    warehouseDTO.setVehicleId(vehicle.getId());
                    warehouseDTO.setBranchId(warehouse.getBranchId());
                }
            }
            List<Warehouse> resultList = new ArrayList<>(warehouseRepository.findAllByVehicleIdAndParentIdNotInAndDeletedIsFalse(vehicle.getId(), warehouses.stream().map(Warehouse::getId).collect(Collectors.toList())).stream().peek(warehouse -> warehouse.setStatus(CommonStatus.IN_ACTIVE)).toList());
            if (!CollectionUtils.isEmpty(resultList)) {
                warehouseRepository.saveAll(resultList);
            }

        }, () -> validationService.entityNotFoundExceptionThrow(Vehicle.class.getSimpleName(), "vehicleId", assignWarehouseDTO.getVehicleId()));
    }
}
