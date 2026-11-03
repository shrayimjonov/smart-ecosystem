package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.notification.Notification;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.NotificationFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 17:29
 */
@Repository
public interface NotificationRepositoryCustom {

    ResultList<Notification> getResultList(NotificationFilter filter);

    Long getCount(NotificationFilter filter);

}
