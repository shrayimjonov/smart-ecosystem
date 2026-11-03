package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Notification;

import java.util.List;

/**
 * Spring Data repository for the Stock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends JpaRepository<Notification, String>, NotificationRepositoryCustom {

    List<Notification> findAllBySeenIsFalse();

    List<Notification> findAllBySeenIsFalseAndOrganizationId(String organizationId);
}
