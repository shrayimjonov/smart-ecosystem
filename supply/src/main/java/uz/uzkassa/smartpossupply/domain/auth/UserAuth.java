package uz.uzkassa.smartpossupply.domain.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 09.02.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_auth",
indexes = {
        @Index(columnList = "username", name = "user_auth_username_idx")
})
public class UserAuth extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 19846516L;

    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @Column(name = "username", length = 50)
    String username;

    @Column(name = "expire_date")
    LocalDateTime expiryDate;

    @Column(name = "activation_code")
    String activationCode;

    @Column(name = "secret_key")
    String secretKey;
}
