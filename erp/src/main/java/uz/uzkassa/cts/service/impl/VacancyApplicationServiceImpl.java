package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.hrms.VacancyApplication;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.file.FileListDTO;
import uz.uzkassa.cts.dto.payload.NotificationPayload;
import uz.uzkassa.cts.dto.vacancy.VacancyDTO;
import uz.uzkassa.cts.dto.vacancyapplication.VacancyApplicationDTO;
import uz.uzkassa.cts.dto.vacancyapplication.admin.VacancyApplicationDetailAdminDTO;
import uz.uzkassa.cts.dto.vacancyapplication.admin.VacancyApplicationListAdminDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqNotificationProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.TemplateService;
import uz.uzkassa.cts.service.VacancyApplicationService;
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
public class VacancyApplicationServiceImpl extends BaseServiceImpl<VacancyApplication, Long> implements VacancyApplicationService {

    private final VacancyApplicationRepository vacancyApplicationRepository;
    private final VacancyService vacancyService;
    private final FileUploadService fileUploadService;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final ReferenceRepository referenceRepository;
    private final GroupRepository groupRepository;
    private final VacancyApplicationTemplateRepository vacancyApplicationTemplateRepository;
    private final RabbitMqNotificationProducer rabbitMqNotificationProducer;
    private final TemplateService templateService;
    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;
    private final NumberingContext numberingContext;
    private final ValidationContext validationContext;

    public VacancyApplicationServiceImpl(
            VacancyApplicationRepository vacancyApplicationRepository,
            VacancyService vacancyService,
            FileUploadService fileUploadService,
            DepartmentRepository departmentRepository,
            PositionRepository positionRepository,
            ReferenceRepository referenceRepository,
            GroupRepository groupRepository,
            VacancyApplicationTemplateRepository vacancyApplicationTemplateRepository,
            RabbitMqNotificationProducer rabbitMqNotificationProducer,
            TemplateService templateService,
            BranchRepository branchRepository,
            CompanyRepository companyRepository,
            NumberingContext numberingContext, ValidationContext validationContext) {
        super(vacancyApplicationRepository);
        this.vacancyApplicationRepository = vacancyApplicationRepository;
        this.vacancyService = vacancyService;
        this.fileUploadService = fileUploadService;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.referenceRepository = referenceRepository;
        this.groupRepository = groupRepository;
        this.vacancyApplicationTemplateRepository = vacancyApplicationTemplateRepository;
        this.rabbitMqNotificationProducer = rabbitMqNotificationProducer;
        this.templateService = templateService;
        this.branchRepository = branchRepository;
        this.companyRepository = companyRepository;
        this.numberingContext = numberingContext;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(VacancyApplicationDTO vacancyApplicationDTO) {
        validationContext.validate(vacancyApplicationDTO);
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.VACANCY_APPLICATION, vacancyApplicationDTO.getCompanyId());
        VacancyApplication vacancyApplication = new VacancyApplication();
        vacancyApplication.setVacancyApplicationNumber(numberingMap.get(NUMBER));
        vacancyApplication.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        vacancyApplication.setName(vacancyApplicationDTO.getName());
        vacancyApplication.setResponsibility(vacancyApplicationDTO.getResponsibility());
        vacancyApplication.setRequirement(vacancyApplicationDTO.getRequirement());
        vacancyApplication.setCondition(vacancyApplicationDTO.getCondition());
        vacancyApplication.setGradeId(vacancyApplicationDTO.getGradeId());
        vacancyApplication.setGroupId(vacancyApplicationDTO.getGroupId());
        vacancyApplication.setQty(vacancyApplicationDTO.getQty());
        vacancyApplication.setApproverId(vacancyApplicationDTO.getApproverId());
        vacancyApplication.setTemplateId(vacancyApplicationDTO.getTemplateId());
        vacancyApplication.setPositionId(vacancyApplicationDTO.getPositionId());
        vacancyApplication.setDepartmentId(vacancyApplicationDTO.getDepartmentId());
        vacancyApplication.setBranchId(vacancyApplicationDTO.getBranchId());
        vacancyApplication.setCompanyId(vacancyApplicationDTO.getCompanyId());
        vacancyApplicationRepository.save(vacancyApplication);
        this.sendNotification(vacancyApplication, vacancyApplication.getApproverId(), TemplateType.VACANCY_APPLICATION_CREATED);
        return vacancyApplication.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, VacancyApplicationDTO vacancyApplicationDTO) {
        vacancyApplicationDTO.setId(id);
        validationContext.validate(vacancyApplicationDTO);
        return vacancyApplicationRepository.findById(vacancyApplicationDTO.getId()).map(vacancyApplication -> {
            vacancyApplication.setName(vacancyApplicationDTO.getName());
            vacancyApplication.setResponsibility(vacancyApplicationDTO.getResponsibility());
            vacancyApplication.setRequirement(vacancyApplicationDTO.getRequirement());
            vacancyApplication.setCondition(vacancyApplicationDTO.getCondition());
            vacancyApplication.setQty(vacancyApplicationDTO.getQty());

            if (!Objects.equals(vacancyApplication.getCompanyId(), vacancyApplicationDTO.getCompanyId())) {
                vacancyApplication.setCompany(companyRepository.findById(vacancyApplicationDTO.getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY)));
                vacancyApplication.setCompanyId(vacancyApplicationDTO.getCompanyId());
            }
            if (!Objects.equals(vacancyApplication.getBranchId(), vacancyApplicationDTO.getBranchId())) {
                vacancyApplication.setBranch(branchRepository.findById(vacancyApplicationDTO.getBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH)));
                vacancyApplication.setBranchId(vacancyApplicationDTO.getBranchId());
            }
            if (!Objects.equals(vacancyApplication.getDepartmentId(), vacancyApplicationDTO.getDepartmentId())) {
                vacancyApplication.setDepartment(departmentRepository.findById(vacancyApplicationDTO.getDepartmentId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEPARTMENT)));
                vacancyApplication.setDepartmentId(vacancyApplicationDTO.getDepartmentId());
            }
            if (!Objects.equals(vacancyApplication.getPositionId(), vacancyApplicationDTO.getPositionId())) {
                vacancyApplication.setPosition(positionRepository.findById(vacancyApplicationDTO.getPositionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(POSITION)));
                vacancyApplication.setPositionId(vacancyApplicationDTO.getPositionId());
            }
            if (!Objects.equals(vacancyApplication.getGradeId(), vacancyApplicationDTO.getGradeId())) {
                vacancyApplication.setGrade(Objects.nonNull(vacancyApplicationDTO.getGradeId()) ? referenceRepository.findById(vacancyApplicationDTO.getGradeId()).orElseThrow(validationService.entityNotFoundExceptionThrow(REFERENCE)) : null);
                vacancyApplication.setGradeId(vacancyApplicationDTO.getGradeId());
            }
            if (!Objects.equals(vacancyApplication.getGroupId(), vacancyApplicationDTO.getGroupId())) {
                vacancyApplication.setGroup(Objects.nonNull(vacancyApplicationDTO.getGroupId()) ? groupRepository.findById(vacancyApplicationDTO.getGroupId()).orElseThrow(validationService.entityNotFoundExceptionThrow(GROUPS)) : null);
                vacancyApplication.setGroupId(vacancyApplicationDTO.getGroupId());
            }
            if (!Objects.equals(vacancyApplication.getTemplateId(), vacancyApplicationDTO.getTemplateId())) {
                vacancyApplication.setTemplate(Objects.nonNull(vacancyApplicationDTO.getTemplateId()) ? vacancyApplicationTemplateRepository.findById(vacancyApplicationDTO.getTemplateId()).orElseThrow(validationService.entityNotFoundExceptionThrow(VACANCY_APPLICATION_TEMPLATE)) : null);
                vacancyApplication.setTemplateId(vacancyApplicationDTO.getTemplateId());
            }
            if (!Objects.equals(vacancyApplication.getApproverId(), vacancyApplicationDTO.getApproverId())) {
                vacancyApplication.setApprover(Objects.nonNull(vacancyApplicationDTO.getApproverId()) ? userRepository.findById(vacancyApplicationDTO.getApproverId()).orElseThrow(validationService.entityNotFoundExceptionThrow(USER)) : null);
                vacancyApplication.setApproverId(vacancyApplicationDTO.getApproverId());
            }
            vacancyApplicationRepository.save(vacancyApplication);
            return vacancyApplication.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(VACANCY_APPLICATION));
    }

    @Override
    public VacancyApplicationDetailAdminDTO get(Long id) {
        return vacancyApplicationRepository.findById(id).map(vacancyApplication -> {
            VacancyApplicationDetailAdminDTO vacancyApplicationDetailAdminDTO = new VacancyApplicationDetailAdminDTO();
            vacancyApplicationDetailAdminDTO.setId(vacancyApplication.getId());
            vacancyApplicationDetailAdminDTO.setVacancyApplicationNumber(vacancyApplication.getVacancyApplicationNumber());
            vacancyApplicationDetailAdminDTO.setName(vacancyApplication.getName());
            vacancyApplicationDetailAdminDTO.setCompany(vacancyApplication.getCompany().toCommonDTO());
            vacancyApplicationDetailAdminDTO.setBranch(vacancyApplication.getBranch().toCommonDTO());
            vacancyApplicationDetailAdminDTO.setDepartment(vacancyApplication.getDepartment().toCommonDTO());
            vacancyApplicationDetailAdminDTO.setPosition(vacancyApplication.getPosition().toCommonDTO());
            vacancyApplicationDetailAdminDTO.setResponsibility(vacancyApplication.getResponsibility());
            vacancyApplicationDetailAdminDTO.setRequirement(vacancyApplication.getRequirement());
            vacancyApplicationDetailAdminDTO.setCondition(vacancyApplication.getCondition());
            vacancyApplicationDetailAdminDTO.setStatus(vacancyApplication.getStatus());
            vacancyApplicationDetailAdminDTO.setQty(vacancyApplication.getQty());
            vacancyApplicationDetailAdminDTO.setReason(vacancyApplication.getRejectReason());
            if (vacancyApplication.getGradeId() != null) {
                vacancyApplicationDetailAdminDTO.setGrade(vacancyApplication.getGrade().toCommonDTO());
            }
            if (vacancyApplication.getGroupId() != null) {
                vacancyApplicationDetailAdminDTO.setGroup(vacancyApplication.getGroup().toCommonDTO());
            }
            if (vacancyApplication.getTemplateId() != null) {
                vacancyApplicationDetailAdminDTO.setTemplate(vacancyApplication.getTemplate().toCommonDTO());
            }
            if (vacancyApplication.getApproverId() != null) {
                vacancyApplicationDetailAdminDTO.setApprover(vacancyApplication.getApprover().toCommonDTO());
            }
            return vacancyApplicationDetailAdminDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(VACANCY_APPLICATION));
    }

    @Override
    public Page<VacancyApplicationListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<VacancyApplication> resultList = vacancyApplicationRepository.getResultList(filter);
        List<VacancyApplicationListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(vacancyApplication -> {
                    VacancyApplicationListAdminDTO vacancyApplicationListAdminDTO = new VacancyApplicationListAdminDTO();
                    vacancyApplicationListAdminDTO.setId(vacancyApplication.getId());
                    vacancyApplicationListAdminDTO.setVacancyApplicationNumber(vacancyApplication.getVacancyApplicationNumber());
                    vacancyApplicationListAdminDTO.setName(vacancyApplication.getName());
                    vacancyApplicationListAdminDTO.setCompany(vacancyApplication.getCompany().toCommonDTO());
                    vacancyApplicationListAdminDTO.setBranch(vacancyApplication.getBranch().toCommonDTO());
                    vacancyApplicationListAdminDTO.setDepartment(vacancyApplication.getDepartment().toCommonDTO());
                    vacancyApplicationListAdminDTO.setPosition(vacancyApplication.getPosition().toCommonDTO());
                    vacancyApplicationListAdminDTO.setStatus(vacancyApplication.getStatus());
                    vacancyApplicationListAdminDTO.setCreatedDate(vacancyApplication.getCreatedDate());
                    vacancyApplicationListAdminDTO.setRequirement(vacancyApplication.getRequirement());
                    vacancyApplicationListAdminDTO.setQty(vacancyApplication.getQty());
                    vacancyApplicationListAdminDTO.setCreator(vacancyApplication.getCreator().toCommonDTO());
                    if (vacancyApplication.getFileId() != null) {
                        FileUpload fileUpload = vacancyApplication.getFile();
                        FileListDTO fileDTO = new FileListDTO();
                        fileDTO.setId(fileUpload.getId());
                        fileDTO.setName(fileUpload.getFileName());
                        fileDTO.setUrl(fileUpload.getDownloadUrl());
                        fileDTO.setDocumentType(fileUpload.getDocumentType());
                        vacancyApplicationListAdminDTO.setFile(fileDTO);
                    }
                    if (vacancyApplication.getGradeId() != null) {
                        vacancyApplicationListAdminDTO.setGrade(vacancyApplication.getGrade().toCommonDTO());
                    }
                    if (vacancyApplication.getGroupId() != null) {
                        vacancyApplicationListAdminDTO.setGroup(vacancyApplication.getGroup().toCommonDTO());
                    }
                    if (vacancyApplication.getApproverId() != null) {
                        vacancyApplicationListAdminDTO.setApprover(vacancyApplication.getApprover().toCommonDTO());
                    }
                    return vacancyApplicationListAdminDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(VacancyApplicationStatus.ACCEPTED.getCode());
        return vacancyApplicationRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, VacancyApplicationStatus status, String reason) {
        vacancyApplicationRepository
                .findById(id)
                .ifPresentOrElse(vacancyApplication -> {
                    vacancyApplication.setStatus(status);
                    vacancyApplicationRepository.save(vacancyApplication);
                    if (VacancyApplicationStatus.ACCEPTED == status) {
                        this.convert(id);
                        this.sendNotification(vacancyApplication, vacancyApplication.getCreatorId(), TemplateType.VACANCY_APPLICATION_ACCEPTED);
                    } else if (VacancyApplicationStatus.REJECTED == status) {
                        if (Objects.equals(SecurityUtils.getCurrentUserId(), vacancyApplication.getApproverId())) {
                            vacancyApplication.setRejectReason(reason);
                            vacancyApplicationRepository.save(vacancyApplication);
                            this.sendNotification(vacancyApplication, vacancyApplication.getCreatorId(), TemplateType.VACANCY_APPLICATION_REJECTED);
                        } else {
                            throw validationService.badRequestExceptionThrow(VACANCY_REJECTION_PERMISSION).get();
                        }
                    }
                }, () -> validationService.entityNotFoundExceptionThrow(VACANCY_APPLICATION).get());
    }

    @Override
    @Transactional
    public Long convert(Long id) {
        return vacancyApplicationRepository.findById(id).map(vacancyApplication -> {
            VacancyDTO vacancyDTO = new VacancyDTO();
            vacancyDTO.setCompanyId(vacancyApplication.getCompanyId());
            vacancyDTO.setBranchId(vacancyApplication.getBranchId());
            vacancyDTO.setDepartmentId(vacancyApplication.getDepartmentId());
            vacancyDTO.setPositionId(vacancyApplication.getPositionId());
            vacancyDTO.setGradeId(vacancyApplication.getGradeId());
            vacancyDTO.setName(vacancyApplication.getName());
            vacancyDTO.setApplicationId(id);
            vacancyDTO.setResponsibility(vacancyApplication.getResponsibility());
            vacancyDTO.setRequirement(vacancyApplication.getRequirement());
            vacancyDTO.setCondition(vacancyApplication.getCondition());
            return vacancyService.create(vacancyDTO);
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(VACANCY_APPLICATION));
    }

    @Override
    @Transactional
    public void uploadFile(Long id, MultipartFile file) {
        VacancyApplication vacancyApplication = findById(id);
        FileUpload fileUpload = fileUploadService.upload(file, EntityTypeEnum.VACANCY_APPLICATION, id, DocumentType.VACANCY_APPLICATION);
        vacancyApplication.setFileId(fileUpload.getId());
        vacancyApplication.setFile(fileUpload);
        save(vacancyApplication);
    }

    private void sendNotification(VacancyApplication vacancyApplication, Long receiverId, TemplateType templateType) {
        String message;
        switch (templateType) {
            case VACANCY_APPLICATION_CREATED ->
                    message = templateService.getVacanyApplicationCreateMessage(templateType, vacancyApplication);
            case VACANCY_APPLICATION_REJECTED ->
                    message = templateService.getVacanyApplicationRejectMessage(templateType, vacancyApplication);
            case VACANCY_APPLICATION_ACCEPTED ->
                    message = templateService.getVacanyApplicationAcceptedMessage(templateType, vacancyApplication);
            default -> message = "";
        }
        rabbitMqNotificationProducer.sendToQueue(NotificationPayload
                .builder()
                .companyId(SecurityUtils.getCompanyId())
                .entityId(vacancyApplication.getId())
                .entityType(EntityTypeEnum.VACANCY_APPLICATION)
                .receiverId(receiverId)
                .messageRu(message)
                .build());
    }
}
