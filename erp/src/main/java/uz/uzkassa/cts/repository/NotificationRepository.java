package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Notification;
import uz.uzkassa.cts.repository.custom.NotificationRepositoryCustom;

import java.util.List;

@Repository
public interface NotificationRepository extends BaseRepository<Notification, Long>, NotificationRepositoryCustom {

    @Query(value = "select count(n.id) from Notification n where n.deleted = false and n.receiverId =:receiverId and n.id not in(select nr.notificationId from NotificationRead nr where nr.userId =:receiverId)")
    Long countUnreadNotificationsByReceiverIdAndDeletedIsFalse(@Param("receiverId") Long receiverId);

    @Query(value = "select n from Notification n where n.deleted = false and n.receiverId = :receiverId and n.id not in (select nr.notificationId from NotificationRead nr where nr.userId = :receiverId)")
    List<Notification> findAllUnreadNotificationsByReceiverIdAndDeletedIsFalse(@Param("receiverId") Long receiverId);
}
