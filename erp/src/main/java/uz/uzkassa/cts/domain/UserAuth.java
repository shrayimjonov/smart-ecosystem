package uz.uzkassa.cts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_auth")
public class UserAuth extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 19846516L;

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
