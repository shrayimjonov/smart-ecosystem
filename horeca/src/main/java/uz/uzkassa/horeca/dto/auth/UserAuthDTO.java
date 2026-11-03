package uz.uzkassa.horeca.dto.auth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserAuthDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4564533L;

    String login;

    String activationCode;

    String secretKey;

    String refreshToken;

    LocalDateTime expiryDate;
}
