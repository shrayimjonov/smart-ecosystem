package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Notification;
import uz.uzkassa.silen.dto.filter.NotificationFilter;

public interface NotificationRepositoryCustom {
    Page<Notification> findAllByFilter(NotificationFilter filter);
}
