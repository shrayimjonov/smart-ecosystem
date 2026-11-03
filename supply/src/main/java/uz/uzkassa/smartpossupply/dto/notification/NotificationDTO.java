package uz.uzkassa.smartpossupply.dto.notification;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.Action;
import uz.uzkassa.smartpossupply.enums.ApplicationType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.TemplateType;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 16:58
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationDTO {

    Long entityId;

    EntityTypeEnum entityType;

    TemplateType templateType;

    ApplicationType applicationType;

    Action action;

}
