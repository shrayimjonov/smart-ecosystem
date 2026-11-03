package uz.uzkassa.horeca.dto.auth;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginDTO extends PhoneDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 303242671681722737L;

    @Size(max = 50, min = 4)
    String password;

    boolean rememberMe;
}
