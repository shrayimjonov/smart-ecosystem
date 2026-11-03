package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.NotificationTemplate;
import uz.uzkassa.silen.enumeration.NotificationTemplateType;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, String>, NotificationTemplateRepositoryCustom {
    Optional<NotificationTemplate> getByTypeAndDeletedIsFalse(NotificationTemplateType notificationTemplateType);

    Boolean findFirstByTypeAndDeletedIsFalse(NotificationTemplateType type);
}
