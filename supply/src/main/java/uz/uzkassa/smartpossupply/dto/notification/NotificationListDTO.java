package uz.uzkassa.smartpossupply.dto.notification;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 17:24
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationListDTO {

    Long id;

    String subject;

    String description;

    LocalDateTime createdDate;

    boolean read;
}
