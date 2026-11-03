package uz.uzkassa.cts.service.impl;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.hrms.VacancyApplication;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.base.LocalizedMessageDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.template.TemplateDTO;
import uz.uzkassa.cts.dto.template.admin.TemplateDetailAdminDTO;
import uz.uzkassa.cts.dto.template.admin.TemplateListAdminDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.TemplateFilter;
import uz.uzkassa.cts.interfaces.TemplateAttributeConstants;
import uz.uzkassa.cts.repository.ContactRepository;
import uz.uzkassa.cts.repository.ReferenceRepository;
import uz.uzkassa.cts.repository.TemplateRepository;
import uz.uzkassa.cts.service.TemplateService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class TemplateServiceImpl extends BaseServiceImpl<Template, Long> implements TemplateService, TemplateAttributeConstants {

    private final TemplateRepository templateRepository;
    private final ReferenceRepository referenceRepository;
    private final ContactRepository contactRepository;
    private final ValidationContext validationContext;

    public TemplateServiceImpl(TemplateRepository templateRepository, ReferenceRepository referenceRepository, ContactRepository contactRepository, ValidationContext validationContext) {
        super(templateRepository);
        this.templateRepository = templateRepository;
        this.referenceRepository = referenceRepository;
        this.contactRepository = contactRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(TemplateDTO templateDTO) {
        validationContext.validate(templateDTO);
        Template template = new Template();
        template.setSubjectUzLatin(templateDTO.getSubjectUzLatin());
        template.setSubjectUzCyrillic(templateDTO.getSubjectUzCyrillic());
        template.setSubjectRu(templateDTO.getSubjectRu());
        template.setMessageUzLatin(templateDTO.getMessageUzLatin());
        template.setMessageUzCyrillic(templateDTO.getMessageUzCyrillic());
        template.setMessageRu(templateDTO.getMessageRu());
        template.setType(templateDTO.getType());
        template.setChannelType(templateDTO.getChannelType());
        return templateRepository.save(template).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, TemplateDTO templateDTO) {
        templateDTO.setId(id);
        validationContext.validate(templateDTO);
        return templateRepository.findById(templateDTO.getId()).map(template -> {
            template.setSubjectUzLatin(templateDTO.getSubjectUzLatin());
            template.setSubjectUzCyrillic(templateDTO.getSubjectUzCyrillic());
            template.setSubjectRu(templateDTO.getSubjectRu());
            template.setMessageUzLatin(templateDTO.getMessageUzLatin());
            template.setMessageUzCyrillic(templateDTO.getMessageUzCyrillic());
            template.setMessageRu(templateDTO.getMessageRu());
            template.setChannelType(templateDTO.getChannelType());
            template.setType(templateDTO.getType());
            return templateRepository.save(template).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(TEMPLATE));
    }

    @Override
    public TemplateDetailAdminDTO getForAdmin(Long id) {
        return templateRepository.findById(id).map(template -> {
            TemplateDetailAdminDTO templateDetailDTO = new TemplateDetailAdminDTO();
            templateDetailDTO.setId(template.getId());

            templateDetailDTO.setSubjectUzLatin(template.getSubjectUzLatin());
            templateDetailDTO.setSubjectUzCyrillic(template.getSubjectUzCyrillic());
            templateDetailDTO.setSubjectRu(template.getSubjectRu());

            templateDetailDTO.setMessageUzLatin(template.getMessageUzLatin());
            templateDetailDTO.setMessageUzCyrillic(template.getMessageUzCyrillic());
            templateDetailDTO.setMessageRu(template.getMessageRu());

            templateDetailDTO.setChannelType(template.getChannelType());
            templateDetailDTO.setType(template.getType());
            return templateDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(TEMPLATE));
    }

    @Override
    public Page<TemplateListAdminDTO> getListForAdmin(TemplateFilter filter) {
        ResultList<Template> resultList = templateRepository.getResultList(filter);
        List<TemplateListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(template -> {
                    TemplateListAdminDTO templateListDTO = new TemplateListAdminDTO();
                    templateListDTO.setId(template.getId());
                    templateListDTO.setSubject(template.getSubject());
                    templateListDTO.setChannelType(template.getChannelType());
                    templateListDTO.setType(template.getType());
                    return templateListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonCodeDTO> lookUp(TemplateFilter filter) {
        return templateRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonCodeDTO).collect(Collectors.toList());
    }

    @Override
    public String getActivationMessage(String activationCode, ChannelType channelType, TemplateType templateType) {
        return templateRepository.findFirstByTypeAndChannelTypeAndDeletedIsFalse(templateType, channelType).map(template -> {
            Map<String, String> attributeValuesMap = new HashMap<>();
            attributeValuesMap.put(ACTIVATION_CODE, activationCode);
            if (TemplateType.ACTIVATION == templateType) {
                attributeValuesMap.put(EXPIRE_TIME, String.valueOf(2));
            }
            return formatMessage(template.getMessage(), attributeValuesMap);
        }).orElse(activationCode);
    }

    @Override
    public String getChangePasswordMessage(User user, String activationCode, ChannelType channelType) {
        return templateRepository.findFirstByTypeAndChannelTypeAndDeletedIsFalse(TemplateType.CHANGE_PASSWORD, channelType).map(template -> {
            Map<String, String> attributeValuesMap = new HashMap<>();
            attributeValuesMap.put(USER_NAME, user.getName());
            attributeValuesMap.put(ACTIVATION_CODE, activationCode);
            attributeValuesMap.put(EXPIRE_TIME, String.valueOf(2));
            return formatMessage(template.getMessage(), attributeValuesMap);
        }).orElse(activationCode);
    }

    @Override
    public String getChangeUsernameMessage(User user, String activationCode, ChannelType channelType) {
        return templateRepository.findFirstByTypeAndChannelTypeAndDeletedIsFalse(TemplateType.CHANGE_USERNAME, channelType).map(template -> {
            Map<String, String> attributeValuesMap = new HashMap<>();
            attributeValuesMap.put(USER_NAME, user.getName());
            attributeValuesMap.put(ACTIVATION_CODE, activationCode);
            attributeValuesMap.put(EXPIRE_TIME, String.valueOf(2));
            return formatMessage(template.getMessage(), attributeValuesMap);
        }).orElse(activationCode);
    }

    @Override
    public LocalizedMessageDTO getBirthdayGreeting(User user) {
        return templateRepository.findFirstByTypeAndChannelTypeAndDeletedIsFalse(TemplateType.BIRTHDAY, ChannelType.NOTIFICATION).map(template -> {
            Map<String, String> attributeValuesMap = new HashMap<>();
            attributeValuesMap.put(TemplateAttributeConstants.FIRST_NAME, user.getFirstName());
            attributeValuesMap.put(TemplateAttributeConstants.LAST_NAME, user.getLastName());
            return LocalizedMessageDTO
                    .builder()
                    .messageRu(formatMessage(template.getMessage(Language.ru), attributeValuesMap))
                    .messageUzLatin(formatMessage(template.getMessage(Language.uz_latin), attributeValuesMap))
                    .messageUzCyrillic(formatMessage(template.getMessage(Language.uz_cyrillic), attributeValuesMap))
                    .build();
        }).orElse(LocalizedMessageDTO
                .builder()
                .messageRu("С днем рождения!")
                .messageUzLatin("Tug'ilgan kuningiz bilan!")
                .messageUzCyrillic("Туғилган кунингиз билан!")
                .build());
    }

    @Override
    public String getLeadMessage(Long templateId, Language language) {
        Company company = getCurrentUserDomain().getCompany();
        Reference phoneType = referenceRepository.findFirstByTypeAndCodeAndDeletedIsFalse(ReferenceType.PHONE_TYPE, "CALL_CENTER");
        Contact contact = contactRepository.findFirstByEntityTypeAndEntityIdAndTypeId(company.getEntityType(), company.getEntityId(), phoneType.getId());
        return templateRepository.findById(templateId).map(template -> {
            Map<String, String> attributeValuesMap = new HashMap<>();
            attributeValuesMap.put(TemplateAttributeConstants.PHONE, contact.getName());
            return formatMessage(template.getMessage(language), attributeValuesMap);
        }).orElse("");
    }

    @Override
    public String getSubject(TemplateType templateType) {
        return templateRepository.findFirstByTypeAndChannelTypeAndDeletedIsFalse(templateType, ChannelType.EMAIL).map(Template::getSubject).orElseThrow(validationService.entityNotFoundExceptionThrow(TEMPLATE));
    }

    @Override
    public String getVacanyApplicationCreateMessage(TemplateType templateType, VacancyApplication vacancyApplication) {
        return templateRepository.findFirstByTypeAndChannelTypeAndDeletedIsFalse(TemplateType.VACANCY_APPLICATION_CREATED, ChannelType.NOTIFICATION).map(template -> {
            Map<String, String> attributeValuesMap = new HashMap<>();
            attributeValuesMap.put(TemplateAttributeConstants.NUMBER, vacancyApplication.getVacancyApplicationNumber());
            return formatMessage(template.getMessage(), attributeValuesMap);
        }).orElse("Vacancy application created!");
    }

    @Override
    public String getVacanyApplicationRejectMessage(TemplateType templateType, VacancyApplication vacancyApplication) {
        return templateRepository.findFirstByTypeAndChannelTypeAndDeletedIsFalse(TemplateType.VACANCY_APPLICATION_REJECTED, ChannelType.NOTIFICATION).map(template -> {
            Map<String, String> attributeValuesMap = new HashMap<>();
            attributeValuesMap.put(TemplateAttributeConstants.NUMBER, vacancyApplication.getVacancyApplicationNumber());
            if (vacancyApplication.getApprover() != null) {
                attributeValuesMap.put(TemplateAttributeConstants.FIRST_NAME, vacancyApplication.getApprover().getFirstName());
                attributeValuesMap.put(TemplateAttributeConstants.LAST_NAME, vacancyApplication.getApprover().getLastName());
            }
            return formatMessage(template.getMessage(), attributeValuesMap);
        }).orElse("Vacancy application rejected!");
    }

    @Override
    public String getVacanyApplicationAcceptedMessage(TemplateType templateType, VacancyApplication vacancyApplication) {
        return templateRepository.findFirstByTypeAndChannelTypeAndDeletedIsFalse(TemplateType.VACANCY_APPLICATION_ACCEPTED, ChannelType.NOTIFICATION).map(template -> {
            Map<String, String> attributeValuesMap = new HashMap<>();
            attributeValuesMap.put(TemplateAttributeConstants.NUMBER, vacancyApplication.getVacancyApplicationNumber());
            if (vacancyApplication.getApprover() != null) {
                attributeValuesMap.put(TemplateAttributeConstants.FIRST_NAME, vacancyApplication.getApprover().getFirstName());
                attributeValuesMap.put(TemplateAttributeConstants.LAST_NAME, vacancyApplication.getApprover().getLastName());
            }
            return formatMessage(template.getMessage(), attributeValuesMap);
        }).orElse("Vacancy application accepted!");
    }

    @Override
    public LocalizedMessageDTO getApprovalProcessMessageForCreator(Approver approver, ApproverStatus status) {
        return templateRepository.findFirstByTypeAndChannelTypeAndDeletedIsFalse(TemplateType.APPROVAL_PROCESS_TEMPLATE_FOR_CREATOR, ChannelType.NOTIFICATION).map(template -> {
            Map<String, String> attributeValuesMap = new HashMap<>();
            attributeValuesMap.put(TemplateAttributeConstants.ENTITY_TYPE, approver.getEntityType().getName());
            attributeValuesMap.put(TemplateAttributeConstants.STATUS, status.getName());
            attributeValuesMap.put(TemplateAttributeConstants.APPROVER, approver.getEmployee().getName());
            return LocalizedMessageDTO
                    .builder()
                    .messageRu(formatMessage(template.getMessage(Language.ru), attributeValuesMap))
                    .messageUzLatin(formatMessage(template.getMessage(Language.uz_latin), attributeValuesMap))
                    .messageUzCyrillic(formatMessage(template.getMessage(Language.uz_cyrillic), attributeValuesMap))
                    .build();
        }).orElse(null);
    }

    @Override
    public LocalizedMessageDTO getApprovalProcessMessageForApprover(Approver approver, User user) {
        return templateRepository.findFirstByTypeAndChannelTypeAndDeletedIsFalse(TemplateType.APPROVAL_PROCESS_TEMPLATE_FOR_APPROVER, ChannelType.NOTIFICATION).map(template -> {
            Map<String, String> attributeValuesMap = new HashMap<>();
            attributeValuesMap.put(TemplateAttributeConstants.ENTITY_TYPE, approver.getEntityType().getName());
            attributeValuesMap.put(TemplateAttributeConstants.CREATOR, user.getName());
            return LocalizedMessageDTO
                    .builder()
                    .messageRu(formatMessage(template.getMessage(Language.ru), attributeValuesMap))
                    .messageUzLatin(formatMessage(template.getMessage(Language.uz_latin), attributeValuesMap))
                    .messageUzCyrillic(formatMessage(template.getMessage(Language.uz_cyrillic), attributeValuesMap))
                    .build();
        }).orElse(null);
    }

    private static String formatMessage(String message, Map<String, String> attributeValuesMap) {
        if (CollectionUtils.isEmpty(attributeValuesMap)) {
            return message;
        }
        VelocityContext velocityContext = new VelocityContext();
        for (Map.Entry<String, String> entry : attributeValuesMap.entrySet()) {
            velocityContext.put(entry.getKey().substring(2, entry.getKey().length() - 1), entry.getValue());
        }
        StringWriter writer = new StringWriter();
        Velocity.evaluate(velocityContext, writer, "Template LOG", message);
        return writer.toString();
    }
}
