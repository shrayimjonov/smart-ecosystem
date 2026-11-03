package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.NotificationTemplate;
import uz.uzkassa.silen.dto.filter.NotificationFilter;

public interface NotificationTemplateRepositoryCustom {
    Page<NotificationTemplate> findAllByFilter(NotificationFilter filter);
}
