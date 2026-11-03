package uz.uzkassa.smartpos.repository.postgres;

import org.springframework.data.domain.Page;
import uz.uzkassa.smartpos.dto.notification.AdminNotificationDto;
import uz.uzkassa.smartpos.dto.notification.CabinetNotificationDto;
import uz.uzkassa.smartpos.filter.NotificationFilter;

import java.util.List;

public interface NotificationRepositoryCustom {
    Page<AdminNotificationDto> findAllByFilterForAdmin(NotificationFilter filter);

    List<CabinetNotificationDto> findAllByFilter(NotificationFilter filter);
}
