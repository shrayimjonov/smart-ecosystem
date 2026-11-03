package uz.uzkassa.horeca.dto.notification;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FirebaseNotificationSendDTO implements Serializable {

    String title;
    String message;
    String logoUrl;
    String recipientToken;
    Map<String, String> additionalProperties;
}
