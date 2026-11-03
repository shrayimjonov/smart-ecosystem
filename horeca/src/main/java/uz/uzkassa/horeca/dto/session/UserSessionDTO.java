package uz.uzkassa.horeca.dto.session;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

/**
 * Author: Shuxratjon Rayimjonov
 * Date: 30.10.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSessionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4689615L;

    String fcmToken;
}
