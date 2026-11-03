package uz.uzkassa.silen.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.config.DbConstants;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "user_permission")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPermission implements Serializable {

    @Serial
    private static final long serialVersionUID = 42342348L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 30)
    String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id",insertable = false, updatable = false)
    User user;

    @Column(name = "permission_id", length = 30)
    Long permissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", referencedColumnName = "id", updatable = false, insertable = false)
    Permission permission;

    public UserPermission() {
    }

    public UserPermission(String userId, Long permissionId) {
        this.userId = userId;
        this.permissionId = permissionId;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

