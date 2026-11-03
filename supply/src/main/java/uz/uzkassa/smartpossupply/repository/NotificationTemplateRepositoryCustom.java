package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.notification.NotificationTemplate;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.TemplateFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 14:20
 */
@Repository
public interface NotificationTemplateRepositoryCustom {
    ResultList<NotificationTemplate> getResultList(TemplateFilter filter);
}
