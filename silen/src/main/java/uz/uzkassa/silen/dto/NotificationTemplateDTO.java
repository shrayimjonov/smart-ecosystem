package uz.uzkassa.silen.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.NotificationStatus;
import uz.uzkassa.silen.enumeration.NotificationTemplateType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.NotificationTemplate} entity.
 */
@Getter
@Setter
@ToString
public class NotificationTemplateDTO implements Serializable {
    private String id;
    private String name;
    private String description;
    private String contents;
    private NotificationTemplateType type;
    private String typeName;
    private NotificationStatus status;
    private String statusName;
    private LocalDateTime createdDate;
}
