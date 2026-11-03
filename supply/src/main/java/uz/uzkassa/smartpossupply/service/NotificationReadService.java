package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.notification.NotificationRead;
import uz.uzkassa.smartpossupply.dto.payload.NotificationReadPayload;
import uz.uzkassa.smartpossupply.repository.NotificationReadRepository;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotificationReadService extends BaseService {

    NotificationReadRepository notificationReadRepository;

    @Transactional
    public void create(NotificationReadPayload payload) {
        NotificationRead notificationRead = new NotificationRead();
        notificationRead.setUserId(payload.getUserId());
        notificationRead.setNotificationId(payload.getNotificationId());
        notificationReadRepository.save(notificationRead);
    }
}
