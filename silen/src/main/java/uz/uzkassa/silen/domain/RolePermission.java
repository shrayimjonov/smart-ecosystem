package uz.uzkassa.silen.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.enumeration.UserRole;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "role_permissions")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermission implements Serializable {

    @Serial
    private static final long serialVersionUID = 42342348L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role", length = 30)
    @Enumerated(value = EnumType.STRING)
    UserRole role;

    @Column(name = "permission_code", length = 30)
    String permissionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_code",referencedColumnName = "code", updatable = false, insertable = false)
    Permission permission;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

