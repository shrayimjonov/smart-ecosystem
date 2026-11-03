package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.hrms.Dependent;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.dependent.DependentDTO;
import uz.uzkassa.cts.dto.dependent.DependentDetailAdminDTO;
import uz.uzkassa.cts.dto.dependent.DependentListAdminDTO;
import uz.uzkassa.cts.filter.DependentFilter;
import uz.uzkassa.cts.repository.DependentRepository;
import uz.uzkassa.cts.repository.EmployeeRepository;
import uz.uzkassa.cts.repository.ReferenceRepository;
import uz.uzkassa.cts.service.DependentService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class DependentServiceImpl extends BaseServiceImpl<Dependent, Long> implements DependentService {

    private final DependentRepository dependentRepository;
    private final ReferenceRepository referenceRepository;
    private final EmployeeRepository employeeRepository;
    private final ValidationContext validationContext;

    public DependentServiceImpl(
            DependentRepository dependentRepository,
            ReferenceRepository referenceRepository,
            EmployeeRepository employeeRepository, ValidationContext validationContext) {
        super(dependentRepository);
        this.dependentRepository = dependentRepository;
        this.referenceRepository = referenceRepository;
        this.employeeRepository = employeeRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(DependentDTO dependentDTO) {
        validationContext.validate(dependentDTO);
        Dependent dependent = new Dependent();
        dependent.setFirstName(dependentDTO.getFirstName());
        dependent.setLastName(dependentDTO.getLastName());
        dependent.setPatronymic(dependentDTO.getPatronymic());
        dependent.setGender(dependentDTO.getGender());
        dependent.setBirthDate(dependentDTO.getBirthDate());
        dependent.setRelationshipId(dependentDTO.getRelationshipId());
        dependent.setEmployeeId(dependentDTO.getEmployeeId());
        return dependentRepository.save(dependent).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, DependentDTO dependentDTO) {
        dependentDTO.setId(id);
        validationContext.validate(dependentDTO);
        return dependentRepository.findById(id).map(dependent -> {
            dependent.setFirstName(dependentDTO.getFirstName());
            dependent.setLastName(dependentDTO.getLastName());
            dependent.setPatronymic(dependentDTO.getPatronymic());
            dependent.setGender(dependentDTO.getGender());
            dependent.setBirthDate(dependentDTO.getBirthDate());
            dependent.setRelationshipId(dependentDTO.getRelationshipId());
            dependent.setRelationship(referenceRepository.findById(dependentDTO.getRelationshipId()).orElseThrow(validationService.entityNotFoundExceptionThrow(REFERENCE)));
            dependent.setEmployeeId(dependentDTO.getEmployeeId());
            dependent.setEmployee(employeeRepository.findById(dependentDTO.getEmployeeId()).orElseThrow(validationService.entityNotFoundExceptionThrow(USER)));
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(DEPENDENT));
    }

    @Override
    public Page<DependentListAdminDTO> getListForAdmin(DependentFilter filter) {
        ResultList<Dependent> resultList = dependentRepository.getResultList(filter);
        List<DependentListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(dependent -> {
                    DependentListAdminDTO dependentListDTO = new DependentListAdminDTO();
                    dependentListDTO.setId(dependent.getId());
                    dependentListDTO.setFirstName(dependent.getFirstName());
                    dependentListDTO.setLastName(dependent.getLastName());
                    dependentListDTO.setPatronymic(dependent.getPatronymic());
                    dependentListDTO.setGender(dependent.getGender());
                    dependentListDTO.setBirthDate(dependent.getBirthDate());
                    dependentListDTO.setEmployee(dependent.getEmployee().toCommonDTO());
                    dependentListDTO.setRelationship(dependent.getRelationship().toCommonDTO());
                    dependentListDTO.setAge(LocalDate.now().getYear() - dependent.getBirthDate().getYear());
                    return dependentListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public DependentDetailAdminDTO getForAdmin(Long id) {
        return dependentRepository.findById(id).map(dependent -> {
            DependentDetailAdminDTO dependentDetailDTO = new DependentDetailAdminDTO();
            dependentDetailDTO.setId(dependent.getId());
            dependentDetailDTO.setFirstName(dependent.getFirstName());
            dependentDetailDTO.setLastName(dependent.getLastName());
            dependentDetailDTO.setPatronymic(dependent.getPatronymic());
            dependentDetailDTO.setGender(dependent.getGender());
            dependentDetailDTO.setBirthDate(dependent.getBirthDate());
            dependentDetailDTO.setRelationship(dependent.getRelationship().toCommonDTO());
            dependentDetailDTO.setEmployee(dependent.getEmployee().toCommonDTO());
            dependentDetailDTO.setAge(LocalDate.now().getYear() - dependent.getBirthDate().getYear());
            return dependentDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(DEPENDENT));
    }

    @Override
    public List<CommonDTO> lookUp(DependentFilter filter) {
        return dependentRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }
}
