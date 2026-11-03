package uz.uzkassa.cts.dto.notification;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.PushKassaNotificationType;

import java.io.Serializable;

/**
 * Created date time: 24.01.2025 15:52
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PushKassaNotificationDTO implements Serializable {

    String message;

    String serviceKey;

    String serviceTag;

    String sessionId;

    PushKassaNotificationType type;
}
