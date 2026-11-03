package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.NotificationTemplateType;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * example params for template
 * Map<String, Object> params = new HashMap<>();
 *     params.put("tin", organization.getTin());
 *     params.put("organizationName", organization.getName());
 *     params.put("date", LocalDateTime.now(ZoneId.of("Asia/Tashkent")));
 *     params.put("amount", "сумма платежа");
 *     params.put("serviceNumber", "Номер услуги");
 *     params.put("serviceName", "Название услуги");
 */
@Getter
@Setter
@ToString
public class NotificationCreateDTO implements Serializable {
    private Set<String> organizations;
    private String templateId;
    private Map<String, String> params;
    private NotificationTemplateType templateType;
}
