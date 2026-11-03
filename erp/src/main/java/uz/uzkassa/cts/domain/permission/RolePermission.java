package uz.uzkassa.cts.domain.permission;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.Role;

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
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"role", "permission_id"})
        })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermission extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 126615483L;

    @NotNull
    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    Role role;

    @NotNull
    @Column(name = "permission_id", nullable = false)
    Long permissionId;

    @ManyToOne(cascade = PERSIST, fetch = LAZY)
    @JoinColumn(name = "permission_id", updatable = false, insertable = false)
    Permission permission;
}
