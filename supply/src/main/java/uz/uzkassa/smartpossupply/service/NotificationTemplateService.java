package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.notification.NotificationTemplate;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.template.NotificationTemplateDTO;
import uz.uzkassa.smartpossupply.dto.template.NotificationTemplateDetailDTO;
import uz.uzkassa.smartpossupply.dto.template.NotificationTemplateListDTO;
import uz.uzkassa.smartpossupply.filters.TemplateFilter;
import uz.uzkassa.smartpossupply.repository.NotificationTemplateRepository;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 12:27
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotificationTemplateService extends BaseService {

    NotificationTemplateRepository notificationTemplateRepository;
    ValidationContext validationContext;

    @Transactional
    public Long create(NotificationTemplateDTO notificationTemplateDTO) {
        validationContext.validate(notificationTemplateDTO);
        NotificationTemplate notificationTemplate = new NotificationTemplate();
        notificationTemplate.setSubjectUz(notificationTemplateDTO.getSubjectUz());
        notificationTemplate.setSubjectRu(notificationTemplateDTO.getSubjectRu());
        notificationTemplate.setSubjectCyrillic(notificationTemplateDTO.getSubjectCyrillic());
        notificationTemplate.setDescriptionUz(notificationTemplateDTO.getDescriptionUz());
        notificationTemplate.setDescriptionRu(notificationTemplateDTO.getDescriptionRu());
        notificationTemplate.setDescriptionCyrillic(notificationTemplateDTO.getDescriptionCyrillic());
        notificationTemplate.setTemplateType(notificationTemplateDTO.getTemplateType());
        notificationTemplate.setApplicationType(notificationTemplateDTO.getApplicationType());
        notificationTemplateRepository.save(notificationTemplate);
        return notificationTemplate.getId();
    }

    @Transactional
    public Long update(Long id, NotificationTemplateDTO notificationTemplateDTO) {
        validationContext.validate(notificationTemplateDTO);
        return notificationTemplateRepository.findById(id).map(notificationTemplate -> {
            notificationTemplate.setSubjectUz(notificationTemplateDTO.getSubjectUz());
            notificationTemplate.setSubjectRu(notificationTemplateDTO.getSubjectRu());
            notificationTemplate.setSubjectCyrillic(notificationTemplateDTO.getSubjectCyrillic());
            notificationTemplate.setDescriptionUz(notificationTemplateDTO.getDescriptionUz());
            notificationTemplate.setDescriptionRu(notificationTemplateDTO.getDescriptionRu());
            notificationTemplate.setDescriptionCyrillic(notificationTemplateDTO.getDescriptionCyrillic());
            notificationTemplate.setTemplateType(notificationTemplateDTO.getTemplateType());
            notificationTemplate.setApplicationType(notificationTemplateDTO.getApplicationType());
            notificationTemplateRepository.save(notificationTemplate);
            return notificationTemplate.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(NotificationTemplate.class.getSimpleName(), "id", id));
    }

    public NotificationTemplateDetailDTO get(Long id) {
        return notificationTemplateRepository.findById(id).map(notificationTemplate -> {
            NotificationTemplateDetailDTO notificationTemplateDetailDTO = new NotificationTemplateDetailDTO();
            notificationTemplateDetailDTO.setId(notificationTemplate.getId());
            notificationTemplateDetailDTO.setSubjectUz(notificationTemplate.getSubjectUz());
            notificationTemplateDetailDTO.setSubjectRu(notificationTemplate.getSubjectRu());
            notificationTemplateDetailDTO.setSubjectCyrillic(notificationTemplate.getSubjectCyrillic());
            notificationTemplateDetailDTO.setDescriptionUz(notificationTemplate.getDescriptionUz());
            notificationTemplateDetailDTO.setDescriptionRu(notificationTemplate.getDescriptionRu());
            notificationTemplateDetailDTO.setDescriptionCyrillic(notificationTemplate.getDescriptionCyrillic());
            notificationTemplateDetailDTO.setTemplateType(notificationTemplate.getTemplateType());
            notificationTemplateDetailDTO.setApplicationType(notificationTemplate.getApplicationType());
            return notificationTemplateDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(NotificationTemplate.class.getSimpleName(), "id", id));
    }

    public Page<NotificationTemplateListDTO> getList(TemplateFilter filter) {
        ResultList<NotificationTemplate> resultList = notificationTemplateRepository.getResultList(filter);
        List<NotificationTemplateListDTO> result = resultList
                .getList()
                .stream()
                .map(notificationTemplate -> {
                    NotificationTemplateListDTO notificationTemplateListDTO = new NotificationTemplateListDTO();
                    notificationTemplateListDTO.setId(notificationTemplate.getId());
                    notificationTemplateListDTO.setSubjectUz(notificationTemplate.getSubjectUz());
                    notificationTemplateListDTO.setSubjectRu(notificationTemplate.getSubjectRu());
                    notificationTemplateListDTO.setSubjectCyrillic(notificationTemplate.getSubjectCyrillic());
                    notificationTemplateListDTO.setTemplateType(notificationTemplate.getTemplateType());
                    notificationTemplateListDTO.setApplicationType(notificationTemplate.getApplicationType());
                    return notificationTemplateListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

}
