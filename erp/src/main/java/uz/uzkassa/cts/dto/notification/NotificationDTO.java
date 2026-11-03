package uz.uzkassa.cts.dto.notification;

import lombok.*;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO implements Serializable {

    Long id;

    String messageRu;

    String messageUzLatin;

    String messageUzCyrillic;

    Long companyId;

    Long receiverId;

    Long entityId;

    EntityTypeEnum entityType;
}
