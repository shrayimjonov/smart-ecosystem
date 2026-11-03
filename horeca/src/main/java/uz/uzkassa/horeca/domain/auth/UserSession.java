package uz.uzkassa.horeca.domain.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.base.SimpleEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.10.2024
 */
@Getter
@Setter
@Table(name = "user_session",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "fcm_token"})}
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSession extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 68465189L;

    @NotNull
    @Column(name = "user_id")
    Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;

    @NotNull
    @Column(name = "fcm_token", length = 500)
    String fcmToken;

    @Column(name = "last_updated_date")
    LocalDateTime lastUpdatedDate = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserSession)) {
            return false;
        }
        return id != null && id.equals(((UserSession) o).id);
    }
}
