package uz.uzkassa.smartpossupply.domain.permission;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.enums.Role;

import java.io.Serial;

@Entity
@Table(name = "role_permission")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermission extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 48L;

    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    Role role;

    @Column(name = "permission_id")
    Long permissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", updatable = false, insertable = false)
    Permission permission;
}
