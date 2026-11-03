package uz.uzkassa.cts.domain.permission;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "user_permission",
        indexes = {
                @Index(columnList = "user_id", name = "user_permission_user_id_idx"),
                @Index(columnList = "permission_id", name = "user_permission_permission_id_idx")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "permission_id"})
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPermission extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 2113868569L;

    @NotNull
    @Column(name = "user_id", nullable = false)
    Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    User user;

    @NotNull
    @Column(name = "permission_id", nullable = false)
    Long permissionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", updatable = false, insertable = false)
    Permission permission;
}
