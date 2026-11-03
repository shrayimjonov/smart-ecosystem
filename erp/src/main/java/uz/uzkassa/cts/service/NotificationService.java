package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Notification;
import uz.uzkassa.cts.dto.notification.NotificationDTO;
import uz.uzkassa.cts.dto.notification.NotificationListDTO;
import uz.uzkassa.cts.filter.NotificationFilter;

public interface NotificationService extends BaseService<Notification, Long> {

    Long create(NotificationDTO notificationDTO);

    Page<NotificationListDTO> getList(NotificationFilter filter);

    void read(Long id);

    void readAll();

    Long countUnreadNotifications();
}
