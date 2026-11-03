package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Notification;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.NotificationFilter;

@Repository
public interface NotificationRepositoryCustom {

    ResultList<Notification> getResultList(NotificationFilter filter);
}
