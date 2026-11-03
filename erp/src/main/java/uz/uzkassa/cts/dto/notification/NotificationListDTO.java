package uz.uzkassa.cts.dto.notification;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationListDTO implements Serializable {

    Long id;

    String message;

    boolean read;

    Long entityId;

    EntityTypeEnum entityType;

    CommonDTO creator;

    LocalDateTime createdDate;
}
