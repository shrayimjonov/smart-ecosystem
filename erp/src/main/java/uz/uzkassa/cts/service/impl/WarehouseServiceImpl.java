package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.inventory.Warehouse;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.warehouse.WarehouseDTO;
import uz.uzkassa.cts.dto.warehouse.WarehouseDetailAdminDTO;
import uz.uzkassa.cts.dto.warehouse.WarehouseListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.AddressMapper;
import uz.uzkassa.cts.repository.WarehouseRepository;
import uz.uzkassa.cts.service.WarehouseService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class WarehouseServiceImpl extends BaseServiceImpl<Warehouse, Long> implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final AddressMapper addressMapper;
    private final ValidationContext validationContext;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, AddressMapper addressMapper, ValidationContext validationContext) {
        super(warehouseRepository);
        this.warehouseRepository = warehouseRepository;
        this.addressMapper = addressMapper;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(WarehouseDTO warehouseDTO) {
        validationContext.validate(warehouseDTO);
        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseDTO.getName());
        warehouse.setBranchId(warehouseDTO.getBranchId());
        warehouse.setDepartmentId(warehouseDTO.getDepartmentId());
        warehouse.setManagerId(warehouseDTO.getManagerId());
        warehouse.setAddress(addressMapper.toEntity(warehouseDTO.getAddress()));
        return save(warehouse).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, WarehouseDTO warehouseDTO) {
        warehouseDTO.setId(id);
        validationContext.validate(warehouseDTO);
        return warehouseRepository.findById(id)
                .map(warehouse -> {
                    warehouse.setName(warehouseDTO.getName());
                    warehouse.setBranchId(warehouseDTO.getBranchId());
                    warehouse.setDepartmentId(warehouseDTO.getDepartmentId());
                    warehouse.setManagerId(warehouseDTO.getManagerId());
                    warehouse.setAddress(addressMapper.merge(warehouseDTO.getAddress(), Optional.ofNullable(warehouse.getAddress()).orElse(new BaseAddress())));
                    save(warehouse);
                    return id;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(WAREHOUSE));
    }

    @Override
    public Page<WarehouseListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<Warehouse> resultList = warehouseRepository.getResultList(filter);
        List<WarehouseListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(warehouse -> {
                    WarehouseListAdminDTO warehouseListDTO = new WarehouseListAdminDTO();
                    warehouseListDTO.setId(warehouse.getId());
                    warehouseListDTO.setName(warehouse.getName());
                    warehouseListDTO.setManager(warehouse.getManager().toCommonDTO());
                    warehouseListDTO.setBranch(warehouse.getBranch().toCommonDTO());
                    warehouseListDTO.setCompany(warehouse.getBranch().getCompany().toCommonDTO());
                    warehouseListDTO.setStatus(warehouse.getStatus());
                    warehouseListDTO.setAddress(addressMapper.toShortDTO(warehouse.getAddress()));
                    if (warehouse.getDepartmentId() != null) {
                        warehouseListDTO.setDepartment(warehouse.getDepartment().toCommonDTO());
                    }
                    return warehouseListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return warehouseRepository.getResultList(filter)
                .getList()
                .stream()
                .map(Warehouse::toCommonDTO)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseDetailAdminDTO get(Long id) {
        return warehouseRepository
                .findById(id)
                .map(warehouse -> {
                    WarehouseDetailAdminDTO warehouseDetailDTO = new WarehouseDetailAdminDTO();
                    warehouseDetailDTO.setId(warehouse.getId());
                    warehouseDetailDTO.setName(warehouse.getName());
                    warehouseDetailDTO.setManager(warehouse.getManager().toCommonDTO());
                    warehouseDetailDTO.setBranch(warehouse.getBranch().toCommonDTO());
                    warehouseDetailDTO.setCompany(warehouse.getBranch().getCompany().toCommonDTO());
                    warehouseDetailDTO.setStatus(warehouse.getStatus());
                    warehouseDetailDTO.setAddress(addressMapper.toDetailDTO(warehouse.getAddress()));
                    if (warehouse.getDepartmentId() != null) {
                        warehouseDetailDTO.setDepartment(warehouse.getDepartment().toCommonDTO());
                    }
                    return warehouseDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(WAREHOUSE));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        warehouseRepository
                .findById(id)
                .ifPresentOrElse(warehouse -> {
                    warehouse.setStatus(status);
                    warehouseRepository.save(warehouse);
                }, () -> validationService.entityNotFoundExceptionThrow(WAREHOUSE).get());

    }
}
