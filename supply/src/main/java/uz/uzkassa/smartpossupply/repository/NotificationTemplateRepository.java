package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.notification.NotificationTemplate;
import uz.uzkassa.smartpossupply.enums.TemplateType;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 10:24
 */
@Repository
public interface NotificationTemplateRepository extends BaseRepository<NotificationTemplate, Long>, NotificationTemplateRepositoryCustom {

    Optional<NotificationTemplate> findFirstByTemplateType(TemplateType templateType);
}
