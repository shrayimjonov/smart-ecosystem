package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.NotificationRead;

import java.util.Optional;

@Repository
public interface NotificationReadRepository extends BaseRepository<NotificationRead, Long> {

    Optional<NotificationRead> findFirstByNotificationIdAndUserId(Long notificationId, Long userId);

    boolean existsByNotificationIdAndUserId(Long notificationId, Long userId);
}
