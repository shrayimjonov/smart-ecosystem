package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Notification;
import uz.uzkassa.silen.domain.NotificationTemplate;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.dto.NotificationCreateDTO;
import uz.uzkassa.silen.dto.NotificationDTO;
import uz.uzkassa.silen.dto.filter.NotificationFilter;
import uz.uzkassa.silen.dto.mq.NotificationPayload;
import uz.uzkassa.silen.repository.pg.NotificationRepository;
import uz.uzkassa.silen.repository.pg.NotificationTemplateRepository;
import uz.uzkassa.silen.utils.Utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Notification}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService extends BaseService {
    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final RabbitTemplate rabbitTemplate;


    public Page<NotificationDTO> findAllByFilter(NotificationFilter filter) {
        return notificationRepository.findAllByFilter(filter).map(Notification::toDto);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void seeNotification(String id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setSeen(true);
            notificationRepository.save(notification);
        });
    }


    @Transactional
    public Set<String> createNotificationFromTemplate(NotificationCreateDTO dto) {
        NotificationTemplate template = notificationTemplateRepository.findById(dto.getTemplateId()).orElseThrow(badRequest("Шаблон уведомления не найден"));

        List<Organization> organizationList = organizationRepository.findAllById(dto.getOrganizations());
        for (Organization organization : organizationList) {
            Map<String, String> params = Optional.ofNullable(dto.getParams()).orElseGet(() -> {
                Map<String, String> tmp = new HashMap<>();
                tmp.put("organizationTin", organization.getTin());
                tmp.put("organizationName", organization.getName());
                tmp.put("time", LocalDateTime.now(ZoneId.of("Asia/Tashkent")).toString());
                tmp.put("serviceAmount", "сумма платежа");
                tmp.put("serviceNumber", "Номер услуги");
                tmp.put("serviceName", "Название услуги");
                tmp.put("userName", getCurrentUserLogin());
                return tmp;
            });

            getNotification(template, params, organization.getId());
            dto.getOrganizations().remove(organization.getId());
        }
        return dto.getOrganizations();
    }

    @Transactional
    public Set<String> createNotificationFromBilling(NotificationCreateDTO dto) {
        NotificationTemplate template = notificationTemplateRepository.getByTypeAndDeletedIsFalse(dto.getTemplateType()).orElseThrow(badRequest("Шаблон уведомления не найден"));
        // by default be added
        dto.getParams().put("time", LocalDateTime.now().toString());

        List<Organization> organizationList = organizationRepository.findAllByTinInAndDeletedIsFalse(dto.getOrganizations());
        for (Organization organization : organizationList) {
            getNotification(template, dto.getParams(), organization.getId());
            dto.getOrganizations().remove(organization.getId());
        }
        return dto.getOrganizations();
    }

    /**
     * ИНН {organizationTin}, {organizationName} -
     * Пользователь {userName} IP:{userIp} - {notificationType}
     * {time}
     * <p>
     * Линия {lineNumber}, Шкаф {deviceId}, ИНН {organizationTin}, {organizationName} - Режим работы "{modes}"
     * <p>
     * ИНН {organizationTin}, {organizationName} - Создан запрос на добавление продукции {productName}
     */
    @Transactional
    public void createNotification(final NotificationPayload payload) {
        if (payload.getOrganizationId() == null && payload.getUserId() == null) {
            return;
        }
        final NotificationTemplate template = notificationTemplateRepository.getByTypeAndDeletedIsFalse(payload.getType()).orElseThrow(badRequest("Шаблон уведомления не найден"));

        payload.addParams("time", LocalDateTime.now().toString());
        payload.addParams("type", payload.getType().getText());

        if (payload.getOrganizationId() != null) {
            final Organization organization = organizationRepository.getReferenceById(payload.getOrganizationId());
            payload.addParams("organizationName", organization.getName())
                    .addParams("organizationTin", organization.getTin());

            Notification notify = getNotification(template, payload.getParams(), organization.getId());

            rabbitTemplate.convertAndSend("/topic/org_" + payload.getOrganizationId(), notify);
        } else {
            createNotificationForOrgIsNull(payload);
        }
    }

    @NotNull
    private Notification getNotification(final NotificationTemplate template, final Map<String, String> params, final String organizationId) {
        Notification notify = new Notification();
        notify.setName(template.getName());
        notify.setDescription(paramsReplace(template.getDescription(), params));
        notify.setContents(paramsReplace(template.getContents(), params));
        notify.setSeen(false);
        notify.setStatus(template.getStatus());
        notify.setType(template.getType());
        notify.setOrganizationId(organizationId);
        notify = notificationRepository.save(notify);
        return notify;
    }

    @Transactional
    public void createNotificationForOrgIsNull(final NotificationPayload payload) {
        final NotificationTemplate template = notificationTemplateRepository.getByTypeAndDeletedIsFalse(payload.getType()).orElseThrow(badRequest("Шаблон уведомления не найден"));
        payload.addParams("type", payload.getType().getText());

        Notification notify = getNotification(template, payload.getParams(), null);

        rabbitTemplate.convertAndSend("/topic/org_" + payload.getUserId(), notify);
    }

    public void notReadNotification() {
        List<NotificationDTO> notify = notificationRepository.findAllBySeenIsFalseAndOrganizationId(getCurrentOrganizationId())
                .stream().map(Notification::toDto)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(notify)) {
            rabbitTemplate.convertAndSend("/topic/org_" + getCurrentOrganizationId(), notify);
        }
    }

    public String paramsReplace(final String content, final Map<String, String> params) {
        String contents = Optional.ofNullable(content).orElse("");
        Matcher m = Utils.getMatcher("\\{(\\w+)\\}", contents);

        while (m.find()) {
            if (params.containsKey(m.group(1))) {
                contents = contents.replace(m.group(), params.get(m.group(1)));
            } else log.debug("Params not found for match by {}", m.group(1));
        }
        return contents;
    }

}
