package uz.uzkassa.cts.dto.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivationFinishDTO implements Serializable {

    String username;

    String password;

    String confirmPassword;

    String secretKey;
}
