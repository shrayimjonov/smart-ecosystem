package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.hrms.VacancyApplicationTemplate;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.vacancyapplicationtemplate.VacancyApplicationTemplateDTO;
import uz.uzkassa.cts.dto.vacancyapplicationtemplate.admin.VacancyApplicationTemplateDetailAdminDTO;
import uz.uzkassa.cts.dto.vacancyapplicationtemplate.admin.VacancyApplicationTemplateListAdminDTO;
import uz.uzkassa.cts.filter.VacancyApplicationTemplateFilter;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.VacancyApplicationTemplateService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class VacancyApplicationTemplateServiceImpl extends BaseServiceImpl<VacancyApplicationTemplate, Long> implements VacancyApplicationTemplateService {

    private final VacancyApplicationTemplateRepository vacancyApplicationTemplateRepository;
    private final DepartmentRepository departmentRepository;
    private final GroupRepository groupRepository;
    private final PositionRepository positionRepository;
    private final ReferenceRepository referenceRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final ValidationContext validationContext;

    public VacancyApplicationTemplateServiceImpl(
            VacancyApplicationTemplateRepository vacancyApplicationTemplateRepository,
            DepartmentRepository departmentRepository,
            GroupRepository groupRepository,
            PositionRepository positionRepository,
            ReferenceRepository referenceRepository,
            CompanyRepository companyRepository, BranchRepository branchRepository, ValidationContext validationContext) {
        super(vacancyApplicationTemplateRepository);
        this.vacancyApplicationTemplateRepository = vacancyApplicationTemplateRepository;
        this.departmentRepository = departmentRepository;
        this.groupRepository = groupRepository;
        this.positionRepository = positionRepository;
        this.referenceRepository = referenceRepository;
        this.companyRepository = companyRepository;
        this.branchRepository = branchRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(VacancyApplicationTemplateDTO vacancyApplicationTemplateDTO) {
        validationContext.validate(vacancyApplicationTemplateDTO);
        VacancyApplicationTemplate vacancyApplicationTemplate = new VacancyApplicationTemplate();
        vacancyApplicationTemplate.setName(vacancyApplicationTemplateDTO.getName());
        vacancyApplicationTemplate.setResponsibility(vacancyApplicationTemplateDTO.getResponsibility());
        vacancyApplicationTemplate.setRequirement(vacancyApplicationTemplateDTO.getRequirement());
        vacancyApplicationTemplate.setCondition(vacancyApplicationTemplateDTO.getCondition());
        vacancyApplicationTemplate.setCompanyId(vacancyApplicationTemplateDTO.getCompanyId());
        vacancyApplicationTemplate.setBranchId(vacancyApplicationTemplateDTO.getBranchId());
        vacancyApplicationTemplate.setDepartmentId(vacancyApplicationTemplateDTO.getDepartmentId());
        vacancyApplicationTemplate.setPositionId(vacancyApplicationTemplateDTO.getPositionId());
        vacancyApplicationTemplate.setGradeId(vacancyApplicationTemplateDTO.getGradeId());
        vacancyApplicationTemplate.setGroupId(vacancyApplicationTemplateDTO.getGroupId());
        vacancyApplicationTemplateRepository.save(vacancyApplicationTemplate);
        return vacancyApplicationTemplate.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, VacancyApplicationTemplateDTO vacancyApplicationTemplateDTO) {
        vacancyApplicationTemplateDTO.setId(id);
        validationContext.validate(vacancyApplicationTemplateDTO);
        return vacancyApplicationTemplateRepository.findById(vacancyApplicationTemplateDTO.getId()).map(vacancyApplicationTemplate -> {
            vacancyApplicationTemplate.setName(vacancyApplicationTemplateDTO.getName());
            vacancyApplicationTemplate.setResponsibility(vacancyApplicationTemplateDTO.getResponsibility());
            vacancyApplicationTemplate.setRequirement(vacancyApplicationTemplateDTO.getRequirement());
            vacancyApplicationTemplate.setCondition(vacancyApplicationTemplateDTO.getCondition());

            if (!Objects.equals(vacancyApplicationTemplate.getCompanyId(), vacancyApplicationTemplateDTO.getCompanyId())) {
                vacancyApplicationTemplate.setCompany(companyRepository.findById(vacancyApplicationTemplateDTO.getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY)));
                vacancyApplicationTemplate.setDepartmentId(vacancyApplicationTemplateDTO.getCompanyId());
            }
            if (!Objects.equals(vacancyApplicationTemplate.getBranchId(), vacancyApplicationTemplateDTO.getBranchId())) {
                vacancyApplicationTemplate.setBranch(branchRepository.findById(vacancyApplicationTemplateDTO.getBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH)));
                vacancyApplicationTemplate.setBranchId(vacancyApplicationTemplateDTO.getBranchId());
            }
            if (!Objects.equals(vacancyApplicationTemplate.getDepartmentId(), vacancyApplicationTemplateDTO.getDepartmentId())) {
                vacancyApplicationTemplate.setDepartment(departmentRepository.findById(vacancyApplicationTemplateDTO.getDepartmentId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEPARTMENT)));
                vacancyApplicationTemplate.setDepartmentId(vacancyApplicationTemplateDTO.getDepartmentId());
            }
            if (!Objects.equals(vacancyApplicationTemplate.getPositionId(), vacancyApplicationTemplateDTO.getPositionId())) {
                vacancyApplicationTemplate.setPosition(positionRepository.findById(vacancyApplicationTemplateDTO.getPositionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(POSITION)));
                vacancyApplicationTemplate.setPositionId(vacancyApplicationTemplate.getPositionId());
            }
            if (!Objects.equals(vacancyApplicationTemplate.getGroupId(), vacancyApplicationTemplateDTO.getGroupId())) {
                vacancyApplicationTemplate.setGroup(Objects.nonNull(vacancyApplicationTemplateDTO.getGroupId()) ? groupRepository.findById(vacancyApplicationTemplateDTO.getGroupId()).orElseThrow(validationService.entityNotFoundExceptionThrow(GROUPS)) : null);
                vacancyApplicationTemplate.setGroupId(vacancyApplicationTemplateDTO.getGroupId());
            }
            if (!Objects.equals(vacancyApplicationTemplate.getGradeId(), vacancyApplicationTemplateDTO.getGradeId())) {
                vacancyApplicationTemplate.setGrade(referenceRepository.findById(vacancyApplicationTemplateDTO.getGradeId()).orElseThrow(validationService.entityNotFoundExceptionThrow(REFERENCE)));
                vacancyApplicationTemplate.setGradeId(vacancyApplicationTemplateDTO.getGradeId());
            }
            vacancyApplicationTemplateRepository.save(vacancyApplicationTemplate);
            return vacancyApplicationTemplate.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(VACANCY_APPLICATION_TEMPLATE));
    }

    @Override
    public VacancyApplicationTemplateDetailAdminDTO get(Long id) {
        return vacancyApplicationTemplateRepository.findById(id).map(vacancyApplicationTemplate -> {
            VacancyApplicationTemplateDetailAdminDTO vacancyApplicationTemplateDetailAdminDTO = new VacancyApplicationTemplateDetailAdminDTO();
            vacancyApplicationTemplateDetailAdminDTO.setId(vacancyApplicationTemplate.getId());
            vacancyApplicationTemplateDetailAdminDTO.setName(vacancyApplicationTemplate.getName());
            vacancyApplicationTemplateDetailAdminDTO.setResponsibility(vacancyApplicationTemplate.getResponsibility());
            vacancyApplicationTemplateDetailAdminDTO.setRequirement(vacancyApplicationTemplate.getRequirement());
            vacancyApplicationTemplateDetailAdminDTO.setCondition(vacancyApplicationTemplate.getCondition());
            vacancyApplicationTemplateDetailAdminDTO.setPosition(vacancyApplicationTemplate.getPosition().toCommonDTO());
            vacancyApplicationTemplateDetailAdminDTO.setCompany(vacancyApplicationTemplate.getCompany().toShortDTO());
            if (vacancyApplicationTemplate.getDepartmentId() != null) {
                vacancyApplicationTemplateDetailAdminDTO.setDepartment(vacancyApplicationTemplate.getDepartment().toCommonDTO());
                vacancyApplicationTemplateDetailAdminDTO.setBranch(vacancyApplicationTemplate.getDepartment().getBranch().toCommonDTO());
            }
            if (vacancyApplicationTemplate.getGradeId() != null) {
                vacancyApplicationTemplateDetailAdminDTO.setGrade(vacancyApplicationTemplate.getGrade().toCommonDTO());
            }
            if (vacancyApplicationTemplate.getGroupId() != null) {
                vacancyApplicationTemplateDetailAdminDTO.setGroup(vacancyApplicationTemplate.getGroup().toCommonDTO());
            }
            return vacancyApplicationTemplateDetailAdminDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(VACANCY_APPLICATION_TEMPLATE));
    }

    @Override
    public Page<VacancyApplicationTemplateListAdminDTO> getListForAdmin(VacancyApplicationTemplateFilter filter) {
        ResultList<VacancyApplicationTemplate> resultList = vacancyApplicationTemplateRepository.getResultList(filter);
        List<VacancyApplicationTemplateListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(vacancyApplicationTemplate -> {
                    VacancyApplicationTemplateListAdminDTO vacancyApplicationTemplateListAdminDTO = new VacancyApplicationTemplateListAdminDTO();
                    vacancyApplicationTemplateListAdminDTO.setId(vacancyApplicationTemplate.getId());
                    vacancyApplicationTemplateListAdminDTO.setName(vacancyApplicationTemplate.getName());
                    vacancyApplicationTemplateListAdminDTO.setCreatedDate(vacancyApplicationTemplate.getCreatedDate());
                    vacancyApplicationTemplateListAdminDTO.setCreator(vacancyApplicationTemplate.getCreator().toCommonDTO());
                    vacancyApplicationTemplateListAdminDTO.setCompany(vacancyApplicationTemplate.getCompany().toShortDTO());
                    vacancyApplicationTemplateListAdminDTO.setPosition(vacancyApplicationTemplate.getPosition().toCommonDTO());
                    if (vacancyApplicationTemplate.getDepartmentId() != null) {
                        vacancyApplicationTemplateListAdminDTO.setDepartment(vacancyApplicationTemplate.getDepartment().toCommonDTO());
                        vacancyApplicationTemplateListAdminDTO.setBranch(vacancyApplicationTemplate.getDepartment().getBranch().toCommonDTO());
                    }
                    if (vacancyApplicationTemplate.getGradeId() != null) {
                        vacancyApplicationTemplateListAdminDTO.setGrade(vacancyApplicationTemplate.getGrade().toCommonDTO());
                    }
                    if (vacancyApplicationTemplate.getGroupId() != null) {
                        vacancyApplicationTemplateListAdminDTO.setGroup(vacancyApplicationTemplate.getGroup().toCommonDTO());
                    }
                    return vacancyApplicationTemplateListAdminDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(VacancyApplicationTemplateFilter filter) {
        return vacancyApplicationTemplateRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }
}
