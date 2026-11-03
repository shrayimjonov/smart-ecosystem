package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.NotificationStatus;
import uz.uzkassa.silen.enumeration.NotificationTemplateType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Reference} entity.
 */
@Getter
@Setter
@ToString
public class NotificationDTO implements Serializable {
    private String id;
    private String organizationId;
    private String organizationName;
    private NotificationStatus status;
    private String statusName;
    private boolean seen;
    private LocalDateTime createdDate;

    private String name;
    private String description;
    private String contents;
    private NotificationTemplateType type;
    private String typeName;
}
