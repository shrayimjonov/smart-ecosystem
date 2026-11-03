package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.hrms.Vacancy;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.vacancy.VacancyDTO;
import uz.uzkassa.cts.dto.vacancy.VacancyDetailAdminDTO;
import uz.uzkassa.cts.dto.vacancy.VacancyListAdminDTO;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.enums.VacancyStatus;
import uz.uzkassa.cts.filter.VacancyFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.VacancyService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static uz.uzkassa.cts.constants.Constants.INCREMENT_NUMBER;
import static uz.uzkassa.cts.constants.Constants.NUMBER;

@Transactional(readOnly = true)
@Service
public class VacancyServiceImpl extends BaseServiceImpl<Vacancy, Long> implements VacancyService {

    private final VacancyRepository vacancyRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final ReferenceRepository referenceRepository;
    private final VacancyApplicationRepository vacancyApplicationRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final ValidationContext validationContext;
    private final NumberingContext numberingContext;

    public VacancyServiceImpl(
            VacancyRepository vacancyRepository,
            DepartmentRepository departmentRepository,
            PositionRepository positionRepository,
            ReferenceRepository referenceRepository,
            VacancyApplicationRepository vacancyApplicationRepository,
            CompanyRepository companyRepository, BranchRepository branchRepository,
            ValidationContext validationContext, NumberingContext numberingContext) {
        super(vacancyRepository);
        this.vacancyRepository = vacancyRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.referenceRepository = referenceRepository;
        this.vacancyApplicationRepository = vacancyApplicationRepository;
        this.companyRepository = companyRepository;
        this.branchRepository = branchRepository;
        this.validationContext = validationContext;
        this.numberingContext = numberingContext;
    }

    @Override
    @Transactional
    public Long create(VacancyDTO vacancyDTO) {
        validationContext.validate(vacancyDTO);
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.VACANCY, vacancyDTO.getCompanyId());
        Vacancy vacancy = new Vacancy();
        vacancy.setVacancyNumber(numberingMap.get(NUMBER));
        vacancy.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        vacancy.setCompanyId(vacancyDTO.getCompanyId());
        vacancy.setBranchId(vacancyDTO.getBranchId());
        vacancy.setDepartmentId(vacancyDTO.getDepartmentId());
        vacancy.setPositionId(vacancyDTO.getPositionId());
        vacancy.setGradeId(vacancyDTO.getGradeId());
        vacancy.setName(vacancyDTO.getName());
        vacancy.setResponsibility(vacancyDTO.getResponsibility());
        vacancy.setRequirement(vacancyDTO.getRequirement());
        vacancy.setCondition(vacancyDTO.getCondition());
        vacancy.setApplicationId(vacancyDTO.getApplicationId());
        vacancyRepository.save(vacancy);
        return vacancy.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, VacancyDTO vacancyDTO) {
        vacancyDTO.setId(id);
        validationContext.validate(vacancyDTO);
        return vacancyRepository.findById(vacancyDTO.getId()).map(vacancy -> {
            vacancy.setName(vacancyDTO.getName());
            vacancy.setResponsibility(vacancyDTO.getResponsibility());
            vacancy.setRequirement(vacancyDTO.getRequirement());
            vacancy.setCondition(vacancyDTO.getCondition());
            if (!Objects.equals(vacancy.getCompanyId(), vacancyDTO.getCompanyId())) {
                vacancy.setCompany(companyRepository.findById(vacancyDTO.getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY)));
                vacancy.setCompanyId(vacancyDTO.getCompanyId());
            }
            if (!Objects.equals(vacancy.getBranchId(), vacancyDTO.getBranchId())) {
                vacancy.setBranch(branchRepository.findById(vacancyDTO.getBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH)));
                vacancy.setBranchId(vacancyDTO.getBranchId());
            }
            if (!Objects.equals(vacancy.getDepartmentId(), vacancyDTO.getDepartmentId())) {
                vacancy.setDepartment(departmentRepository.findById(vacancyDTO.getDepartmentId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEPARTMENT)));
                vacancy.setDepartmentId(vacancyDTO.getDepartmentId());
            }
            if (!Objects.equals(vacancy.getPositionId(), vacancyDTO.getPositionId())) {
                vacancy.setPosition(positionRepository.findById(vacancyDTO.getPositionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(POSITION)));
                vacancy.setPositionId(vacancyDTO.getPositionId());
            }
            if (!Objects.equals(vacancy.getGradeId(), vacancyDTO.getGradeId())) {
                vacancy.setGrade(Objects.nonNull(vacancyDTO.getGradeId()) ? referenceRepository.findById(vacancyDTO.getGradeId()).orElseThrow(validationService.entityNotFoundExceptionThrow(REFERENCE)) : null);
                vacancy.setGradeId(vacancyDTO.getGradeId());
            }
            if (!Objects.equals(vacancy.getApplicationId(), vacancyDTO.getApplicationId())) {
                vacancy.setVacancyApplication(Objects.nonNull(vacancyDTO.getApplicationId()) ? vacancyApplicationRepository.findById(vacancyDTO.getApplicationId()).orElseThrow(validationService.entityNotFoundExceptionThrow(APPLICATION)) : null);
                vacancy.setApplicationId(vacancyDTO.getApplicationId());
            }
            vacancyRepository.save(vacancy);
            return vacancy.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(VACANCY));
    }

    @Override
    public VacancyDetailAdminDTO get(Long id) {
        return vacancyRepository.findById(id).map(vacancy -> {
            VacancyDetailAdminDTO vacancyDetailAdminDTO = new VacancyDetailAdminDTO();
            vacancyDetailAdminDTO.setId(vacancy.getId());
            vacancyDetailAdminDTO.setVacancyNumber(vacancy.getVacancyNumber());
            vacancyDetailAdminDTO.setName(vacancy.getName());
            vacancyDetailAdminDTO.setCompany(vacancy.getCompany().toCommonDTO());
            vacancyDetailAdminDTO.setBranch(vacancy.getBranch().toCommonDTO());
            vacancyDetailAdminDTO.setDepartment(vacancy.getDepartment().toCommonDTO());
            vacancyDetailAdminDTO.setPosition(vacancy.getPosition().toCommonDTO());
            vacancyDetailAdminDTO.setResponsibility(vacancy.getResponsibility());
            vacancyDetailAdminDTO.setRequirement(vacancy.getRequirement());
            vacancyDetailAdminDTO.setCondition(vacancy.getCondition());
            if (vacancy.getApplicationId() != null) {
                vacancyDetailAdminDTO.setApplication(vacancy.getApplication().toCommonDTO());
            }
            if (vacancy.getGradeId() != null) {
                vacancyDetailAdminDTO.setGrade(vacancy.getGrade().toCommonDTO());
            }
            if (vacancy.getHrManagerId() != null) {
                vacancyDetailAdminDTO.setHrManager(vacancy.getHrManager().toCommonDTO());
            }
            return vacancyDetailAdminDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(VACANCY));
    }

    @Override
    public Page<VacancyListAdminDTO> getListForAdmin(VacancyFilter filter) {
        ResultList<Vacancy> resultList = vacancyRepository.getResultList(filter);
        List<VacancyListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(vacancy -> {
                    VacancyListAdminDTO vacancyListAdminDTO = new VacancyListAdminDTO();
                    vacancyListAdminDTO.setId(vacancy.getId());
                    vacancyListAdminDTO.setVacancyNumber(vacancy.getVacancyNumber());
                    vacancyListAdminDTO.setName(vacancy.getName());
                    vacancyListAdminDTO.setCompany(vacancy.getCompany().toCommonDTO());
                    vacancyListAdminDTO.setBranch(vacancy.getBranch().toCommonDTO());
                    vacancyListAdminDTO.setDepartment(vacancy.getDepartment().toCommonDTO());
                    vacancyListAdminDTO.setPosition(vacancy.getPosition().toCommonDTO());
                    if (vacancy.getGradeId() != null) {
                        vacancyListAdminDTO.setGrade(vacancy.getGrade().toCommonDTO());
                    }
                    if (vacancy.getApplicationId() != null) {
                        vacancyListAdminDTO.setApplication(vacancy.getApplication().toCommonDTO());
                    }
                    if (vacancy.getHrManagerId() != null) {
                        vacancyListAdminDTO.setHrManager(vacancy.getHrManager().toCommonDTO());
                    }
                    vacancyListAdminDTO.setCreator(vacancy.getCreator().toCommonDTO());
                    vacancyListAdminDTO.setCreatedDate(vacancy.getCreatedDate());
                    vacancyListAdminDTO.setStatus(vacancy.getStatus());
                    return vacancyListAdminDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(VacancyFilter filter) {
        filter.setStatus(VacancyStatus.OPEN.getCode());
        return vacancyRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, VacancyStatus status) {
        vacancyRepository
                .findById(id)
                .ifPresentOrElse(vacancy -> {
                    vacancy.setStatus(status);
                    vacancyRepository.save(vacancy);
                }, () -> validationService.entityNotFoundExceptionThrow(VACANCY).get());
    }

    @Override
    @Transactional
    public void assignVacancyToHr(Long id, Long hrId) {
        vacancyRepository
                .findById(id)
                .ifPresentOrElse(vacancy -> {
                    vacancy.setHrManagerId(hrId);
                    vacancyRepository.save(vacancy);
                }, () -> validationService.entityNotFoundExceptionThrow(VACANCY).get());
    }

    /*@Override
    public HashMap<String, String> getNumberingMap(Long companyId) {
        Long incrementNumber = vacancyRepository.getMaxIncrementNumber(companyId) + 1L;
        String stockNumber = Prefix.VACANCY
                .getName()
                .concat(DateUtil.formatYearLastTwoDigit(LocalDate.now()))
                .concat("-")
                .concat(COMMON_NUMBERING_FORMAT.format(incrementNumber));

        HashMap<String, String> numberMap = new HashMap<>();
        numberMap.put(INCREMENT_NUMBER, String.valueOf(incrementNumber));
        numberMap.put(NUMBER, stockNumber);
        return numberMap;
    }*/
}
