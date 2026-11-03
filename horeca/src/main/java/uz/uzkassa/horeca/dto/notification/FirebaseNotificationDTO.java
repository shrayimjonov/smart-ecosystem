package uz.uzkassa.horeca.dto.notification;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.Role;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FirebaseNotificationDTO implements Serializable {

    Long departmentId;

    Role role;

    Long userId;

    String message;

    String logoUrl;

    Map<String, String> additionalProperties = new HashMap<>();

    public void addAdditionalProperty(String key, String value) {
        additionalProperties.put(key, value);
    }
}
