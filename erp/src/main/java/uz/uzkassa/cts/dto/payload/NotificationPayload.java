package uz.uzkassa.cts.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class NotificationPayload implements Serializable {

    Long companyId;

    Long entityId;

    Long receiverId;

    EntityTypeEnum entityType;

    String messageRu;

    String messageUzLatin;

    String messageUzCyrillic;
}
