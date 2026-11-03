package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Notification;
import uz.uzkassa.cts.domain.NotificationRead;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.notification.NotificationDTO;
import uz.uzkassa.cts.dto.notification.NotificationListDTO;
import uz.uzkassa.cts.filter.NotificationFilter;
import uz.uzkassa.cts.repository.NotificationReadRepository;
import uz.uzkassa.cts.repository.NotificationRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.NotificationService;
import uz.uzkassa.cts.service.PushKassaNotificationService;

import java.util.List;
import java.util.Optional;

import static uz.uzkassa.cts.constants.Constants.PUSH_NOTIFICATION_COUNT;
import static uz.uzkassa.cts.constants.Constants.PUSH_SERVICE_TAG;

@Transactional(readOnly = true)
@Service
public class NotificationServiceImpl extends BaseServiceImpl<Notification, Long> implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationReadRepository notificationReadRepository;
    private final PushKassaNotificationService pushKassaNotificationService;

    public NotificationServiceImpl(NotificationRepository notificationRepository, NotificationReadRepository notificationReadRepository, PushKassaNotificationService pushKassaNotificationService) {
        super(notificationRepository);
        this.notificationRepository = notificationRepository;
        this.notificationReadRepository = notificationReadRepository;
        this.pushKassaNotificationService = pushKassaNotificationService;
    }

    @Override
    @Transactional
    public Long create(NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        notification.setCompanyId(Optional.ofNullable(notificationDTO.getCompanyId()).orElseGet(BaseServiceImpl::getCompanyId));
        notification.setReceiverId(notificationDTO.getReceiverId());
        notification.setMessageRu(notificationDTO.getMessageRu());
        notification.setMessageUzLatin(notificationDTO.getMessageUzLatin());
        notification.setMessageUzCyrillic(notificationDTO.getMessageUzCyrillic());
        notification.setEntityId(notificationDTO.getEntityId());
        notification.setEntityType(notificationDTO.getEntityType());
        notificationRepository.save(notification);
        Long countUnreadNotifications = notificationRepository.countUnreadNotificationsByReceiverIdAndDeletedIsFalse(notificationDTO.getReceiverId());
        Thread.startVirtualThread(() -> pushKassaNotificationService.sendNotificationToSession(countUnreadNotifications.toString(), PUSH_SERVICE_TAG, PUSH_NOTIFICATION_COUNT, notificationDTO.getReceiverId().toString()));
        return notification.getId();
    }

    @Override
    public Page<NotificationListDTO> getList(NotificationFilter filter) {
        filter.setReceiverId(getCurrentUserId());
        ResultList<Notification> resultList = notificationRepository.getResultList(filter);
        List<NotificationListDTO> result = resultList.getList().stream().map(notification -> {
            NotificationListDTO notificationListDTO = new NotificationListDTO();
            notificationListDTO.setId(notification.getId());
            notificationListDTO.setMessage(notification.getMessage());
            notificationListDTO.setRead(notificationReadRepository.existsByNotificationIdAndUserId(notification.getId(), Optional.ofNullable(filter.getReceiverId()).orElseGet(SecurityUtils::getCurrentUserId)));
            notificationListDTO.setEntityId(notification.getEntityId());
            notificationListDTO.setEntityType(notification.getEntityType());
            notificationListDTO.setCreator(notification.getCreator().toCommonDTO());
            notificationListDTO.setCreatedDate(notification.getCreatedDate());
            return notificationListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void read(Long id) {
        NotificationRead notificationRead = notificationReadRepository.findFirstByNotificationIdAndUserId(id, getCurrentUserId()).orElseGet(NotificationRead::new);
        if (notificationRead.getId() == null) {
            notificationRead.setNotificationId(id);
            notificationRead.setUserId(getCurrentUserId());
        }
        notificationReadRepository.save(notificationRead);
    }

    @Override
    @Transactional
    public void readAll() {
        notificationReadRepository.saveAll(notificationRepository.findAllUnreadNotificationsByReceiverIdAndDeletedIsFalse(getCurrentUserId()).stream().map(notification -> {
            NotificationRead notificationRead = new NotificationRead();
            notificationRead.setUserId(getCurrentUserId());
            notificationRead.setNotificationId(notification.getId());
            return notificationRead;
        }).toList());
    }

    @Override
    public Long countUnreadNotifications() {
        return notificationRepository.countUnreadNotificationsByReceiverIdAndDeletedIsFalse(getCurrentUserId());
    }
}
