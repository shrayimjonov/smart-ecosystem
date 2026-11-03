package uz.uzkassa.smartpos.domain.postgres.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Table(name = "user_permission", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "permission_code"})
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPermission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    Long userId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    User user;

    @Column(name = "permission_code", nullable = false)
    String permissionCode;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_code", referencedColumnName = "code", updatable = false, insertable = false)
    Permission permission;

    @Column(name = "has_access", columnDefinition = "boolean default false")
    boolean hasAccess;
}
