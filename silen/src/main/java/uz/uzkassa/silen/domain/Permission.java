package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.enumeration.PermissionType;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(
    schema = DbConstants.CURRENT_SCHEMA, name = "permissions",
    indexes = {
        @Index(columnList = "name", name = "permission_name_idx"),
        @Index(columnList = "parent_Id", name = "permission_parent_id_idx"),
    }
)
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission extends BaseIdentityEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 3894845988L;

    @Column(name = "name")
    String name;

    @Column(name = "code", unique = true)
    String code;

    Integer position;

    @Column(name = "section", columnDefinition = "boolean default false")
    boolean section = false;

    @Column(name = "parent_id", updatable = false, insertable = false)
    Long parentId;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    Permission parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    Set<Permission> children = new HashSet<>();

    @Column(name = "permission_type")
    @Enumerated(EnumType.STRING)
    PermissionType permissionType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permission)) {
            return false;
        }
        return this.getId() != null && this.getId().equals(((Permission) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
