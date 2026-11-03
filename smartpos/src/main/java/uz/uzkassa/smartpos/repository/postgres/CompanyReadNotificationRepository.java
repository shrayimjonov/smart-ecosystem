package uz.uzkassa.smartpos.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.CompanyReadNotification;

import java.util.Collection;
import java.util.List;

public interface CompanyReadNotificationRepository extends JpaRepository<CompanyReadNotification, Long> {
    List<CompanyReadNotification> findAllByCompanyIdAndNotificationIdIn(Long companyId, Collection<Long> notificationId);
}