package uz.uzkassa.smartpossupply.dto.notification;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.ApplicationType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.TemplateType;

import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 17:43
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationDetailDTO {

    Long id;

    String subject;

    String description;

    Long entityId;

    EntityTypeEnum entityType;

    LocalDateTime createdDate;

    ApplicationType applicationType;

    TemplateType templateType;

    CommonDTO creator;
}
