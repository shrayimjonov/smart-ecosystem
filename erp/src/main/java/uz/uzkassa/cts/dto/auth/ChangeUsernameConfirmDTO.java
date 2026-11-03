package uz.uzkassa.cts.dto.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeUsernameConfirmDTO extends UsernameDTO implements Serializable {

    String activationCode;

    String secretKey;

    public ChangeUsernameConfirmDTO(String username) {
        super(username);
    }
}
