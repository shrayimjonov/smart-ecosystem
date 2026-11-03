package uz.uzkassa.smartpos.dto.notification;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.Notification;
import uz.uzkassa.smartpos.dto.CommonFileDTO;
import uz.uzkassa.smartpos.dto.UserShortDto;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.enums.BillingType;
import uz.uzkassa.smartpos.enums.NotificationCategory;
import uz.uzkassa.smartpos.enums.NotificationTarget;
import uz.uzkassa.smartpos.enums.NotificationType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Notification}
 */
@Getter
@Setter
public final class AdminNotificationDto implements Serializable {
    private Long id;
    private LocalDateTime createdAt;
    private UserShortDto createdBy;
    private LocalDateTime lastModifiedAt;
    private UserShortDto lastModifiedBy;
    private String titleUz;
    private String titleRu;
    private String titleCyrillic;
    private String messageUz;
    private String messageRu;
    private String messageCyrillic;
    private NotificationType type;
    private BillingType billingType;
    private NotificationTarget target;
    private String buttonUz;
    private String buttonRu;
    private String buttonCyrillic;
    private String buttonLink;
    private NotificationCategory category;
    private LocalDateTime dateOfPublication;
    private CompanyBaseDTO company;
    private boolean read;
    private CommonFileDTO image;
}