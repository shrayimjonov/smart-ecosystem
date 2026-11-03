package uz.uzkassa.horeca.domain.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.base.BaseEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "user_auth")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAuth extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 59846L;

    @Column(name = "login")
    String login;

    @Size(max = 36, min = 36)
    @Column(name = "secret_key", length = 36)
    String secretKey;

    @Size(max = 6, min = 6)
    @Column(name = "activation_code", length = 6)
    String activationCode;

    @Size(max = 36, min = 36)
    @Column(name = "refresh_token", length = 36)
    String refreshToken;

    @Column(name = "expiry_date")
    LocalDateTime expiryDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAuth)) {
            return false;
        }
        return id != null && id.equals(((UserAuth) o).id);
    }
}
