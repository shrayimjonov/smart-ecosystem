package uz.uzkassa.horeca.domain.permission;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.enums.PermissionType;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permission",
        indexes = {
                @Index(columnList = "code", name = "permission_code_idx"),
                @Index(columnList = "permission_type", name = "permission_type_idx"),
                @Index(columnList = "parent_id", name = "permission_parent_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 3894845988L;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "code", nullable = false)
    String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type", nullable = false)
    PermissionType permissionType;

    @Column(name = "position")
    Integer position;

    @Column(name = "section", columnDefinition = "boolean default false")
    boolean section = false;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Permission parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Permission> children = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permission)) {
            return false;
        }
        return id != null && id.equals(((Permission) o).id);
    }
}
