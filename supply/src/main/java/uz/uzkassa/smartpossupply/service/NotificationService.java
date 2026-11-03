package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.notification.Notification;
import uz.uzkassa.smartpossupply.domain.notification.NotificationTemplate;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.notification.NotificationDetailDTO;
import uz.uzkassa.smartpossupply.dto.notification.NotificationListDTO;
import uz.uzkassa.smartpossupply.dto.payload.NotificationPayload;
import uz.uzkassa.smartpossupply.enums.TemplateType;
import uz.uzkassa.smartpossupply.enums.TemplateTypeAlias;
import uz.uzkassa.smartpossupply.filters.NotificationFilter;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqNotificationProducer;
import uz.uzkassa.smartpossupply.repository.NotificationReadRepository;
import uz.uzkassa.smartpossupply.repository.NotificationRepository;
import uz.uzkassa.smartpossupply.repository.NotificationTemplateRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static uz.uzkassa.smartpossupply.utils.SecurityUtils.getCompanyId;

/**
 * Powered by: Shuxratjon Rayimjonov
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService extends BaseService {

    NotificationRepository notificationRepository;
    NotificationTemplateRepository notificationTemplateRepository;
    RabbitMqNotificationProducer notificationProducer;
    NotificationReadRepository notificationReadRepository;

    public Long getCount(NotificationFilter filter) {
        filter.setCompanyId(getCompanyId());
        filter.setRead(Boolean.FALSE);
        return notificationRepository.getCount(filter);
    }

    public Page<NotificationListDTO> getList(NotificationFilter filter) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        ResultList<Notification> resultList = notificationRepository.getResultList(filter);
        List<NotificationListDTO> result = resultList.getList().stream()
                .map(notification -> {
                    NotificationListDTO notificationListDTO = new NotificationListDTO();
                    notificationListDTO.setId(notification.getId());
                    notificationListDTO.setSubject(notification.getSubject());
                    notificationListDTO.setDescription(notification.getDescription());
                    notificationListDTO.setCreatedDate(notification.getCreatedDate());
                    notificationListDTO.setRead(notificationReadRepository.existsByNotificationIdAndUserId(notification.getId(), currentUserId));
                    return notificationListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getPageable(), resultList.getCount());
    }

    public NotificationDetailDTO get(Long id) {
        return notificationRepository.findById(id).map(notification -> {
            NotificationDetailDTO notificationDetailDTO = new NotificationDetailDTO();
            notificationDetailDTO.setId(notification.getId());
            notificationDetailDTO.setSubject(notification.getSubject());
            notificationDetailDTO.setDescription(notification.getDescription());
            notificationDetailDTO.setCreatedDate(notification.getCreatedDate());
            notificationProducer.read(notification.getId(), SecurityUtils.getCurrentUserId());
            return notificationDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Notification.class.getSimpleName(), "id", id));

    }

    @Transactional
    public void send(NotificationPayload payload) {
        notificationTemplateRepository.findFirstByTemplateType(payload.getTemplateType()).ifPresent(template -> {
            Notification notification = new Notification();
            notification.setCompanyId(payload.getCompanyId());
            notification.setApplicationType(template.getApplicationType());
            notification.setTemplateType(template.getTemplateType());
            notification.setCreatedDate(LocalDateTime.now());

            notification.setSubjectUz(this.process(template.getSubjectUz(), payload.getTemplateType(), payload.getParams()));
            notification.setSubjectRu(this.process(template.getSubjectRu(), payload.getTemplateType(), payload.getParams()));
            notification.setSubjectCyrillic(this.process(template.getSubjectCyrillic(), payload.getTemplateType(), payload.getParams()));
            notification.setDescriptionUz(this.process(template.getDescriptionUz(), payload.getTemplateType(), payload.getParams()));
            notification.setDescriptionRu(this.process(template.getDescriptionRu(), payload.getTemplateType(), payload.getParams()));
            notification.setDescriptionCyrillic(this.process(template.getDescriptionCyrillic(), payload.getTemplateType(), payload.getParams()));

            notificationRepository.saveAndFlush(notification);
        });
    }

    private String process(String text, TemplateType templateType, Map<TemplateTypeAlias, Object> params) {
        validate(templateType, params);
        if (text == null) {
            return null;
        }

        for (var entry : params.entrySet()) {
            String placeholder = "{" + entry.getKey().name() + "}";
            String value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
            text = text.replace(placeholder, value);
        }
        return text;
    }

    private void validate(TemplateType templateType, Map<TemplateTypeAlias, Object> params) {
        for (TemplateTypeAlias required : templateType.getTypes()) {
            if (!params.containsKey(required)) {
                throw new IllegalArgumentException(
                        "Missing template param: " + required
                );
            }
        }
    }

}
