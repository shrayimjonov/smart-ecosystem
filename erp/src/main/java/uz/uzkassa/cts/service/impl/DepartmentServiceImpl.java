package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.hrms.Department;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.CommonTreeDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.department.DepartmentDTO;
import uz.uzkassa.cts.dto.department.admin.DepartmentDetailAdminDTO;
import uz.uzkassa.cts.dto.department.admin.DepartmentListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BranchRepository;
import uz.uzkassa.cts.repository.DepartmentRepository;
import uz.uzkassa.cts.service.DepartmentService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class DepartmentServiceImpl extends BaseServiceImpl<Department, Long> implements DepartmentService, CacheConstants {

    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;
    private final ValidationContext validationContext;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, BranchRepository branchRepository, ValidationContext validationContext) {
        super(departmentRepository);
        this.departmentRepository = departmentRepository;
        this.branchRepository = branchRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(DepartmentDTO departmentDTO) {
        validationContext.validate(departmentDTO);
        Department department = new Department();
        department.setNameUzLatin(departmentDTO.getNameUzLatin());
        department.setNameUzCyrillic(departmentDTO.getNameUzCyrillic());
        department.setNameRu(departmentDTO.getNameRu());
        department.setParentId(departmentDTO.getParentId());
        department.setBranchId(departmentDTO.getBranchId());
        departmentRepository.save(department);
        return department.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, DepartmentDTO departmentDTO) {
        departmentDTO.setId(id);
        validationContext.validate(departmentDTO);
        return departmentRepository.findById(id).map(department -> {
            department.setNameUzLatin(departmentDTO.getNameUzLatin());
            department.setNameUzCyrillic(departmentDTO.getNameUzCyrillic());
            department.setNameRu(departmentDTO.getNameRu());
            if (!Objects.equals(department.getParentId(), departmentDTO.getParentId())) {
                department.setParentId(departmentDTO.getParentId());
                department.setParent(departmentRepository.findById(departmentDTO.getParentId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEPARTMENT)));
            }
            if (!Objects.equals(department.getBranchId(), departmentDTO.getBranchId())) {
                department.setBranchId(departmentDTO.getBranchId());
                department.setBranch(branchRepository.findById(departmentDTO.getBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH)));
            }
            departmentRepository.save(department);
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(DEPARTMENT));
    }

    @Override
    public Page<DepartmentListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<Department> resultList = departmentRepository.getResultList(filter);
        List<DepartmentListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(department -> {
                    DepartmentListAdminDTO departmentListDTO = new DepartmentListAdminDTO();
                    departmentListDTO.setId(department.getId());
                    departmentListDTO.setNameUzLatin(department.getNameUzLatin());
                    departmentListDTO.setNameUzCyrillic(department.getNameUzCyrillic());
                    departmentListDTO.setNameRu(department.getNameRu());
                    departmentListDTO.setStatus(department.getStatus());
                    if (department.getParentId() != null) {
                        departmentListDTO.setParent(department.getParent().toCommonDTO());
                    }
                    departmentListDTO.setBranch(department.getBranch().toCommonDTO());
                    departmentListDTO.setCompany(department.getBranch().getCompany().toCommonDTO());
                    return departmentListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public DepartmentDetailAdminDTO getForAdmin(Long id) {
        return departmentRepository.findById(id).map(department -> {
            DepartmentDetailAdminDTO departmentDetailTO = new DepartmentDetailAdminDTO();
            departmentDetailTO.setId(department.getId());
            departmentDetailTO.setStatus(department.getStatus());
            departmentDetailTO.setNameUzLatin(department.getNameUzLatin());
            departmentDetailTO.setNameUzCyrillic(department.getNameUzCyrillic());
            departmentDetailTO.setNameRu(department.getNameRu());
            if (department.getParentId() != null) {
                departmentDetailTO.setParent(department.getParent().toCommonDTO());
            }
            departmentDetailTO.setBranch(department.getBranch().toCommonDTO());
            departmentDetailTO.setCompany(department.getBranch().getCompany().toCommonDTO());
            return departmentDetailTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(DEPARTMENT));
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return departmentRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        departmentRepository
                .findById(id)
                .map(department -> {
                    department.setStatus(status);
                    departmentRepository.save(department);
                    return department.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(DEPARTMENT));
    }

    @Override
    public List<CommonTreeDTO> treeList(BaseFilter filter) {
        return recursion(departmentRepository.findAllByBranchIdAndStatusAndDeletedIsFalse(filter.getBranchId(), CommonStatus.ACTIVE));
    }

    private List<CommonTreeDTO> recursion(List<Department> departments) {
        Map<Long, CommonTreeDTO> departmentsMap = departments.stream()
                .collect(Collectors.toMap(Department::getId, department -> new CommonTreeDTO(department.getId(), department.getName())));

        List<CommonTreeDTO> result = new ArrayList<>();
        departments.forEach(department -> {
            CommonTreeDTO departmentTreeDTO = departmentsMap.get(department.getId());
            if (department.getParentId() == null) {
                result.add(departmentTreeDTO);
            } else {
                CommonTreeDTO parentDepartmentTreeDTO = departmentsMap.get(department.getParentId());
                if (parentDepartmentTreeDTO != null) {
                    parentDepartmentTreeDTO.getChildren().add(departmentTreeDTO);
                }
            }
        });
        return result;
    }
}
