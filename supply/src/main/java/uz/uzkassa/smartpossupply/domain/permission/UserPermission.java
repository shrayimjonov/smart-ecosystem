package uz.uzkassa.smartpossupply.domain.permission;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;

@Entity
@Table(name = "user_permission",indexes = {
        @Index(columnList = "user_id", name = "user_permission_user_id_idx"),
        @Index(columnList = "permission_id", name = "user_permission_permission_id_idx"),
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPermission extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 68958341L;

    @Column(name = "user_id")
    Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    User user;

    @Column(name = "permission_id")
    Long permissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", updatable = false, insertable = false)
    Permission permission;

    @Column(name = "has_access", columnDefinition = "boolean default true")
    boolean hasAccess = true;

    @Column(name = "manual", columnDefinition = "boolean default false")
    boolean manual = false;
}
