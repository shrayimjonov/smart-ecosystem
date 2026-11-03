package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.hrms.Candidate;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.candidate.CandidateDTO;
import uz.uzkassa.cts.dto.candidate.CandidateDetailAdminDTO;
import uz.uzkassa.cts.dto.candidate.CandidateListAdminDTO;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.enums.CandidateStatus;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.CandidateService;
import uz.uzkassa.cts.service.ContactService;
import uz.uzkassa.cts.service.SocialAddressService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uz.uzkassa.cts.constants.Constants.INCREMENT_NUMBER;
import static uz.uzkassa.cts.constants.Constants.NUMBER;

@Transactional(readOnly = true)
@Service
public class CandidateServiceImpl extends BaseServiceImpl<Candidate, Long> implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final SocialAddressService socialAddressService;
    private final ReferenceRepository referenceRepository;
    private final ContactService contactService;
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;
    private final VacancyRepository vacancyRepository;
    private final ValidationContext validationContext;
    private final NumberingContext numberingContext;

    public CandidateServiceImpl(
            CandidateRepository candidateRepository,
            SocialAddressService socialAddressService,
            ReferenceRepository referenceRepository,
            ContactService contactService,
            PositionRepository positionRepository,
            DepartmentRepository departmentRepository,
            VacancyRepository vacancyRepository,
            ValidationContext validationContext, NumberingContext numberingContext
    ) {
        super(candidateRepository);
        this.candidateRepository = candidateRepository;
        this.referenceRepository = referenceRepository;
        this.socialAddressService = socialAddressService;
        this.contactService = contactService;
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
        this.vacancyRepository = vacancyRepository;
        this.validationContext = validationContext;
        this.numberingContext = numberingContext;
    }

    @Override
    @Transactional
    public Long create(CandidateDTO candidateDTO) {
        validationContext.validate(candidateDTO);
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.CANDIDATE, getCompanyId());
        String phone = candidateDTO.getPhones().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
        String email = CollectionUtils.isEmpty(candidateDTO.getEmails()) ? null : candidateDTO.getEmails().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();

        Candidate candidate = new Candidate();
        candidate.setCandidateNumber(numberingMap.get(NUMBER));
        candidate.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        candidate.setFirstName(candidateDTO.getFirstName());
        candidate.setLastName(candidateDTO.getLastName());
        candidate.setPatronymic(candidateDTO.getPatronymic());
        candidate.setPhone(phone);
        candidate.setEmail(email);
        candidate.setPositionId(candidateDTO.getPositionId());
        candidate.setDepartmentId(candidateDTO.getDepartmentId());
        candidate.setVacancyId(candidateDTO.getVacancyId());
        candidate.setCompanyId(SecurityUtils.getCompanyId());
        candidateRepository.save(candidate);

        contactService.createBatch(candidate.getEntityType(), candidate.getEntityId(), candidateDTO.getPhones());
        if (!CollectionUtils.isEmpty(candidateDTO.getEmails())) {
            contactService.createBatch(candidate.getEntityType(), candidate.getEntityId(), candidateDTO.getEmails());
        }
        if (!CollectionUtils.isEmpty(candidateDTO.getSocialAddresses())) {
            socialAddressService.createBatch(candidate.getEntityType(), candidate.getEntityId(), candidateDTO.getSocialAddresses());
        }
        return candidate.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, CandidateDTO candidateDTO) {
        candidateDTO.setId(id);
        validationContext.validate(candidateDTO);
        return candidateRepository.findById(candidateDTO.getId()).map(candidate -> {
            candidate.setFirstName(candidateDTO.getFirstName());
            candidate.setLastName(candidateDTO.getLastName());
            candidate.setPatronymic(candidateDTO.getPatronymic());
            candidate.setPositionId(candidateDTO.getPositionId());
            candidate.setPosition(positionRepository.findById(candidateDTO.getPositionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(POSITION)));
            candidate.setDepartmentId(candidateDTO.getDepartmentId());
            candidate.setDepartment(departmentRepository.findById(candidateDTO.getDepartmentId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEPARTMENT)));
            candidate.setVacancyId(candidateDTO.getVacancyId());
            candidate.setVacancy(vacancyRepository.findById(candidateDTO.getVacancyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(VACANCY)));

            String phone = candidateDTO.getPhones().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
            String email = CollectionUtils.isEmpty(candidateDTO.getEmails()) ? null : candidateDTO.getEmails().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
            candidate.setPhone(phone);
            candidate.setEmail(email);

            contactService.updateBatch(candidate.getEntityType(), candidate.getEntityId(), candidateDTO.getPhones());
            if (!CollectionUtils.isEmpty(candidateDTO.getEmails())) {
                contactService.updateBatch(candidate.getEntityType(), candidate.getEntityId(), candidateDTO.getEmails());
            }
            if (!CollectionUtils.isEmpty(candidateDTO.getSocialAddresses())) {
                socialAddressService.updateBatch(candidate.getEntityType(), candidate.getEntityId(), candidateDTO.getSocialAddresses());
            }
            candidateRepository.save(candidate);
            return candidate.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CANDIDATE));
    }

    @Override
    public CandidateDetailAdminDTO getForAdmin(Long id) {
        return candidateRepository.findById(id).map(candidate -> {
            CandidateDetailAdminDTO candidateDetailDTO = new CandidateDetailAdminDTO();
            candidateDetailDTO.setId(candidate.getId());
            candidateDetailDTO.setCandidateNumber(candidate.getCandidateNumber());
            candidateDetailDTO.setFirstName(candidate.getFirstName());
            candidateDetailDTO.setLastName(candidate.getLastName());
            candidateDetailDTO.setPatronymic(candidate.getPatronymic());
            candidateDetailDTO.setPosition(candidate.getPosition().toCommonDTO());
            candidateDetailDTO.setDepartment(candidate.getDepartment().toCommonDTO());
            candidateDetailDTO.setBranch(candidate.getDepartment().getBranch().toCommonDTO());
            candidateDetailDTO.setCompany(candidate.getDepartment().getBranch().getCompany().toCommonDTO());
            candidateDetailDTO.setVacancy(candidate.getVacancy().toCommonDTO());

            List<Long> phoneTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.PHONE_TYPE);
            List<Long> emailTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.EMAIL_TYPE);

            candidateDetailDTO.setPhones(contactService.getList(candidate.getEntityType(), candidate.getEntityId(), phoneTypeIds));
            candidateDetailDTO.setEmails(contactService.getList(candidate.getEntityType(), candidate.getEntityId(), emailTypeIds));
            candidateDetailDTO.setSocialAddresses(socialAddressService.getByEntityTypeAndEntityId(candidate.getEntityType(), candidate.getEntityId()));
            return candidateDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CANDIDATE));
    }

    @Override
    public Page<CandidateListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<Candidate> resultList = candidateRepository.getResultList(filter);
        List<CandidateListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(candidate -> {
                    CandidateListAdminDTO candidateListDTO = new CandidateListAdminDTO();
                    candidateListDTO.setId(candidate.getId());
                    candidateListDTO.setCandidateNumber(candidate.getCandidateNumber());
                    candidateListDTO.setFirstName(candidate.getFirstName());
                    candidateListDTO.setLastName(candidate.getLastName());
                    candidateListDTO.setPatronymic(candidate.getPatronymic());
                    candidateListDTO.setPhone(candidate.getPhone());
                    candidateListDTO.setEmail(candidate.getEmail());
                    candidateListDTO.setPosition(candidate.getPosition().toCommonDTO());
                    candidateListDTO.setDepartment(candidate.getDepartment().toCommonDTO());
                    candidateListDTO.setBranch(candidate.getDepartment().getBranch().toCommonDTO());
                    candidateListDTO.setCompany(candidate.getDepartment().getBranch().getCompany().toCommonDTO());
                    candidateListDTO.setVacancy(candidate.getVacancy().toCommonDTO());
                    return candidateListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return candidateRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CandidateStatus status) {
        candidateRepository
                .findById(id)
                .ifPresentOrElse(candidate -> {
                    candidate.setStatus(status);
                    candidateRepository.save(candidate);
                }, () -> validationService.entityNotFoundExceptionThrow(CANDIDATE).get());
    }
}
