package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.PersonalIdentityInformation;
import uz.uzkassa.cts.dto.personalidentityinformation.PersonalIdentityInformationDTO;
import uz.uzkassa.cts.repository.PersonalIdentityInformationRepository;
import uz.uzkassa.cts.service.PersonalIdentityInformationService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonalIdentityInformationServiceImpl extends BaseServiceImpl<PersonalIdentityInformation, Long> implements PersonalIdentityInformationService {

    PersonalIdentityInformationRepository personalIdentityInformationRepository;
    ValidationContext validationContext;

    public PersonalIdentityInformationServiceImpl(PersonalIdentityInformationRepository personalIdentityInformationRepository, ValidationContext validationContext) {
        super(personalIdentityInformationRepository);
        this.personalIdentityInformationRepository = personalIdentityInformationRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(Long employeeId, PersonalIdentityInformationDTO personalIdentityInformationDTO) {
        personalIdentityInformationDTO.setEmployeeId(employeeId);
        validationContext.validate(personalIdentityInformationDTO);
        PersonalIdentityInformation personalIdentityInformation = new PersonalIdentityInformation();
        personalIdentityInformation.setEmployeeId(employeeId);
        personalIdentityInformation.setSerial(personalIdentityInformationDTO.getSerial());
        personalIdentityInformation.setNumber(personalIdentityInformationDTO.getNumber());
        personalIdentityInformation.setIssueDate(personalIdentityInformationDTO.getIssueDate());
        personalIdentityInformation.setExpiryDate(personalIdentityInformationDTO.getExpiryDate());
        personalIdentityInformation.setIssueBy(personalIdentityInformationDTO.getIssueBy());
        personalIdentityInformation.setPinfl(personalIdentityInformationDTO.getPinfl());
        personalIdentityInformation.setCategories(personalIdentityInformationDTO.getCategories());
        personalIdentityInformation.setType(personalIdentityInformationDTO.getType());
        return personalIdentityInformationRepository.save(personalIdentityInformation).getId();
    }

    @Override
    @Transactional
    public Long update(Long employeeId, PersonalIdentityInformationDTO personalIdentityInformationDTO) {
        personalIdentityInformationDTO.setEmployeeId(employeeId);
        validationContext.validate(personalIdentityInformationDTO);
        return personalIdentityInformationRepository.findByEmployeeIdAndTypeAndDeletedIsFalse(employeeId, personalIdentityInformationDTO.getType()).map(personalIdentityInformation -> {
            personalIdentityInformation.setEmployeeId(employeeId);
            personalIdentityInformation.setSerial(personalIdentityInformationDTO.getSerial());
            personalIdentityInformation.setNumber(personalIdentityInformationDTO.getNumber());
            personalIdentityInformation.setIssueDate(personalIdentityInformationDTO.getIssueDate());
            personalIdentityInformation.setExpiryDate(personalIdentityInformationDTO.getExpiryDate());
            personalIdentityInformation.setIssueBy(personalIdentityInformationDTO.getIssueBy());
            personalIdentityInformation.setPinfl(personalIdentityInformationDTO.getPinfl());
            personalIdentityInformation.setCategories(personalIdentityInformationDTO.getCategories());
            return personalIdentityInformationRepository.save(personalIdentityInformation).getId();
        }).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(PersonalIdentityInformation.class.getSimpleName()).get());
    }

    @Override
    public List<PersonalIdentityInformationDTO> getList(Long employeeId) {
        return personalIdentityInformationRepository.findAllByEmployeeIdAndDeletedIsFalse(employeeId).stream().map(personalIdentityInformation -> {
            PersonalIdentityInformationDTO personalIdentityInformationListDTO = new PersonalIdentityInformationDTO();
            personalIdentityInformationListDTO.setId(personalIdentityInformation.getId());
            personalIdentityInformationListDTO.setSerial(personalIdentityInformation.getSerial());
            personalIdentityInformationListDTO.setNumber(personalIdentityInformation.getNumber());
            personalIdentityInformationListDTO.setIssueDate(personalIdentityInformation.getIssueDate());
            personalIdentityInformationListDTO.setExpiryDate(personalIdentityInformation.getExpiryDate());
            personalIdentityInformationListDTO.setIssueBy(personalIdentityInformation.getIssueBy());
            personalIdentityInformationListDTO.setPinfl(personalIdentityInformation.getPinfl());
            personalIdentityInformationListDTO.setCategories(personalIdentityInformation.getCategories());
            personalIdentityInformationListDTO.setType(personalIdentityInformation.getType());
            return personalIdentityInformationListDTO;
        }).toList();
    }

    @Override
    public PersonalIdentityInformationDTO get(Long employeeId) {
        return personalIdentityInformationRepository.findById(employeeId).map(personalIdentityInformation -> {
            PersonalIdentityInformationDTO personalIdentityInformationDetailDTO = new PersonalIdentityInformationDTO();
            personalIdentityInformationDetailDTO.setId(personalIdentityInformation.getId());
            personalIdentityInformationDetailDTO.setSerial(personalIdentityInformation.getSerial());
            personalIdentityInformationDetailDTO.setNumber(personalIdentityInformation.getNumber());
            personalIdentityInformationDetailDTO.setIssueDate(personalIdentityInformation.getIssueDate());
            personalIdentityInformationDetailDTO.setExpiryDate(personalIdentityInformation.getExpiryDate());
            personalIdentityInformationDetailDTO.setIssueBy(personalIdentityInformation.getIssueBy());
            personalIdentityInformationDetailDTO.setPinfl(personalIdentityInformation.getPinfl());
            personalIdentityInformationDetailDTO.setCategories(personalIdentityInformation.getCategories());
            personalIdentityInformationDetailDTO.setType(personalIdentityInformation.getType());
            return personalIdentityInformationDetailDTO;
        }).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(PersonalIdentityInformation.class.getSimpleName()).get());
    }
}
