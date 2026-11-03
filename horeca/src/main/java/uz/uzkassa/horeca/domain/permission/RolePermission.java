package uz.uzkassa.horeca.domain.permission;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.enums.Role;

import java.io.Serial;
import java.io.Serializable;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@Table(name = "role_permission",
        indexes = {
                @Index(columnList = "role", name = "role_permission_role_idx"),
                @Index(columnList = "permission_id", name = "role_permission_per_id_idx")
        })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermission extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 58L;

    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    Role role;

    @Column(name = "permission_id", nullable = false)
    Long permissionId;

    @ManyToOne(cascade = PERSIST, fetch = LAZY)
    @JoinColumn(name = "permission_id", updatable = false, insertable = false)
    Permission permission;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RolePermission)) {
            return false;
        }
        return id != null && id.equals(((RolePermission) o).id);
    }
}
