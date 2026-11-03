package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.Department;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.department.DepartmentDTO;
import uz.uzkassa.horeca.dto.department.DepartmentDetailDTO;
import uz.uzkassa.horeca.dto.department.DepartmentListDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.interfaces.CrudAdvanced;
import uz.uzkassa.horeca.repository.DepartmentRepository;
import uz.uzkassa.horeca.web.rest.errors.ConflictException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class DepartmentService
        extends BaseService
        implements CrudAdvanced<Long, DepartmentDTO, DepartmentDetailDTO, DepartmentListDTO, BaseFilter, CommonStatus, CommonDTO> {

    DepartmentRepository departmentRepository;

    @Override
    public void validate(DepartmentDTO departmentDTO) {
        Long branchId = getCurrentBranchId(departmentDTO.getBranchId(), true);
        if (StringUtils.isEmpty(departmentDTO.getName())) {
            throw badRequestExceptionThrow("Name is required").get();
        }
        if (departmentDTO.getType() == null) {
            throw badRequestExceptionThrow("Type is required").get();
        }
        if (departmentDTO.getId() == null && departmentRepository.existsByBranchIdAndDeletedIsFalseAndNameIgnoreCase(branchId, departmentDTO.getName())) {
            throw conflictExceptionThrow("Департмент с таким названием " + departmentDTO.getName() + " уже существует.").get();
        }
        if (departmentDTO.getId() != null && departmentRepository.existsByBranchIdAndDeletedIsFalseAndNameIgnoreCaseAndIdNot(branchId, departmentDTO.getName(), departmentDTO.getId())) {
            if (departmentDTO.getType() == null) {
                if (departmentDTO.getId() == null && departmentRepository.existsByBranchIdAndDeletedIsFalseAndNameIgnoreCase(branchId, departmentDTO.getName())) {
                    throw conflictExceptionThrow("Департмент с таким названием " + departmentDTO.getName() + " уже существует.").get();
                }
            }
        }
    }

    @Transactional
    @Override
    public Long create(DepartmentDTO departmentDTO) {
        this.validate(departmentDTO);
        Department department = null;
        if (departmentDTO.getUniqueId() != null) {
            department = departmentRepository.findFirstByUniqueIdAndBranchIdAndDeletedIsFalse(departmentDTO.getUniqueId(), departmentDTO.getBranchId()).orElse(null);
        }
        if (department == null) {
            department = new Department();
        }
        department.setUniqueId(Optional.ofNullable(departmentDTO.getUniqueId()).orElse(UUID.randomUUID()));
        department.setName(departmentDTO.getName());
        department.setType(departmentDTO.getType());
        department.setBranchId(getCurrentBranchId(departmentDTO.getBranchId()));
        departmentRepository.saveAndFlush(department);
        return department.getId();
    }

    @Override
    public DepartmentDetailDTO get(Long id) {
        return departmentRepository
                .findById(id)
                .map(department -> {
                    DepartmentDetailDTO departmentDetailDTO = new DepartmentDetailDTO();
                    departmentDetailDTO.setId(department.getId());
                    departmentDetailDTO.setName(department.getName());
                    departmentDetailDTO.setType(department.getType());
                    departmentDetailDTO.setBranch(department.getBranch().toCommonDTO());
                    return departmentDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Department.class.getSimpleName(), "id", id));
    }

    @Transactional
    @Override
    public Long update(Long id, DepartmentDTO departmentDTO) {
        return departmentRepository.findById(id).map(department -> {
            departmentDTO.setId(id);
            this.validate(departmentDTO);
            department.setName(departmentDTO.getName());
            department.setType(departmentDTO.getType());
            department.setBranchId(getCurrentBranchId(departmentDTO.getBranchId()));
            departmentRepository.save(department);
            return department.getId();
        }).orElseThrow(notFoundExceptionThrow(Department.class.getSimpleName(), "id", id));
    }

    @Override
    public Page<DepartmentListDTO> getList(BaseFilter filter) {
        ResultList<Department> resultList = departmentRepository.getResultList(filter);
        List<DepartmentListDTO> result = resultList.getList().stream().map(department -> {
            DepartmentListDTO departmentListDTO = new DepartmentListDTO();
            departmentListDTO.setId(department.getId());
            departmentListDTO.setName(department.getName());
            departmentListDTO.setType(department.getType());
            departmentListDTO.setBranch(department.getBranch().toCommonDTO());
            departmentListDTO.setStatus(department.getStatus());
            return departmentListDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        departmentRepository.deleteById(id);
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return departmentRepository.getResultList(filter).getList().stream().map(Department::toCommonDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateStatus(Long id, CommonStatus status) {
        departmentRepository
                .findById(id)
                .map(department -> {
                    department.setStatus(status);
                    departmentRepository.save(department);
                    return department;
                })
                .orElseThrow(notFoundExceptionThrow(Department.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void syncDepartment(Long fromBranchId, Long toBranchId) {
        List<Department> departments = departmentRepository.findAllByBranchIdAndDeletedIsFalse(fromBranchId);
        if (!CollectionUtils.isEmpty(departments)) {
            for (Department department : departments) {
                DepartmentDTO departmentDTO = new DepartmentDTO();
                departmentDTO.setName(department.getName());
                departmentDTO.setType(department.getType());
                departmentDTO.setBranchId(toBranchId);
                departmentDTO.setUniqueId(department.getUniqueId());
                try {
                    this.create(departmentDTO);
                } catch (ConflictException e) {
                    log.info("Department error: {}", e.getMessage());
                    departmentRepository.findFirstByBranchIdAndDeletedIsFalseAndNameIgnoreCase(toBranchId, departmentDTO.getName())
                            .ifPresent(toDepartment -> {
                                toDepartment.setUniqueId(department.getUniqueId());
                                departmentRepository.saveAndFlush(toDepartment);
                            });
                } catch (Exception e) {
                    log.info("Department error: {}", e.getMessage());
                }
            }
        }
    }
}
