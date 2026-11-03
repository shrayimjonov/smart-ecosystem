package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.hrms.Groups;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.group.GroupDTO;
import uz.uzkassa.cts.dto.group.admin.GroupDetailAdminDTO;
import uz.uzkassa.cts.dto.group.admin.GroupListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.DepartmentRepository;
import uz.uzkassa.cts.repository.GroupRepository;
import uz.uzkassa.cts.service.GroupService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class GroupServiceImpl extends BaseServiceImpl<Groups, Long> implements GroupService {

    private final GroupRepository groupRepository;
    private final DepartmentRepository departmentRepository;
    private final ValidationContext validationContext;

    public GroupServiceImpl(GroupRepository groupRepository, DepartmentRepository departmentRepository, ValidationContext validationContext) {
        super(groupRepository);
        this.groupRepository = groupRepository;
        this.departmentRepository = departmentRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(GroupDTO groupDTO) {
        validationContext.validate(groupDTO);
        Groups group = new Groups();
        group.setName(groupDTO.getName());
        group.setDepartmentId(groupDTO.getDepartmentId());
        groupRepository.save(group);
        return group.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, GroupDTO groupDTO) {
        groupDTO.setId(id);
        validationContext.validate(groupDTO);
        return groupRepository.findById(id).map(group -> {
            group.setName(groupDTO.getName());
            if (!Objects.equals(group.getDepartmentId(), groupDTO.getDepartmentId())) {
                group.setDepartmentId(groupDTO.getDepartmentId());
                group.setDepartment(departmentRepository.findById(groupDTO.getDepartmentId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEPARTMENT)));
            }
            groupRepository.save(group);
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(GROUPS));
    }

    @Override
    public Page<GroupListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<Groups> resultList = groupRepository.getResultList(filter);
        List<GroupListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(group -> {
                    GroupListAdminDTO groupListDTO = new GroupListAdminDTO();
                    groupListDTO.setId(group.getId());
                    groupListDTO.setName(group.getName());
                    groupListDTO.setStatus(group.getStatus());
                    groupListDTO.setDepartment(group.getDepartment().toCommonDTO());
                    groupListDTO.setBranch(group.getDepartment().getBranch().toCommonDTO());
                    groupListDTO.setCompany(group.getDepartment().getBranch().getCompany().toCommonDTO());
                    return groupListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public GroupDetailAdminDTO getForAdmin(Long id) {
        return groupRepository.findById(id).map(group -> {
            GroupDetailAdminDTO groupDetailDTO = new GroupDetailAdminDTO();
            groupDetailDTO.setId(group.getId());
            groupDetailDTO.setName(group.getName());
            groupDetailDTO.setStatus(group.getStatus());
            groupDetailDTO.setDepartment(group.getDepartment().toCommonDTO());
            groupDetailDTO.setBranch(group.getDepartment().getBranch().toCommonDTO());
            groupDetailDTO.setCompany(group.getDepartment().getBranch().getCompany().toCommonDTO());
            return groupDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(GROUPS));
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return groupRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        groupRepository
                .findById(id)
                .ifPresentOrElse(group -> {
                    group.setStatus(status);
                    groupRepository.save(group);
                }, () -> validationService.entityNotFoundExceptionThrow(GROUPS).get());
    }
}
