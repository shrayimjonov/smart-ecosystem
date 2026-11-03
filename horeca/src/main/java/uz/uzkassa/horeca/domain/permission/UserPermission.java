package uz.uzkassa.horeca.domain.permission;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.BaseEntity;

import java.io.Serial;
import java.io.Serializable;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "user_permission",
        indexes = {
                @Index(columnList = "user_id", name = "user_permission_user_id_idx"),
                @Index(columnList = "permission_id", name = "user_permission_per_id_idx"),
                @Index(columnList = "has_access", name = "user_permission_has_access_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPermission extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 68958341L;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @ManyToOne(cascade = PERSIST, fetch = LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    User user;

    @Column(name = "permission_id", nullable = false)
    Long permissionId;

    @ManyToOne(cascade = PERSIST, fetch = LAZY)
    @JoinColumn(name = "permission_id", updatable = false, insertable = false)
    Permission permission;

    @Column(name = "has_access", columnDefinition = "boolean default true")
    boolean hasAccess;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserPermission)) {
            return false;
        }
        return id != null && id.equals(((UserPermission) o).id);
    }
}
