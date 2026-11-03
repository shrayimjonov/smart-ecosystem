package uz.uzkassa.silen.dto.mq;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.NotificationStatus;
import uz.uzkassa.silen.enumeration.NotificationTemplateType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class NotificationPayload implements Serializable {

    private NotificationStatus status = NotificationStatus.INFO;

    private String userId;

    private String organizationId;

    private NotificationTemplateType type;

    private Map<String, String> params = new HashMap<>();

    public NotificationPayload addParams(String key, String value) {
        if (params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(key, value);
        return this;
    }
}
