package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.notification.Notification;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 17:08
 */

@Repository
public interface NotificationRepository extends BaseRepository<Notification, Long>, NotificationRepositoryCustom {

}
