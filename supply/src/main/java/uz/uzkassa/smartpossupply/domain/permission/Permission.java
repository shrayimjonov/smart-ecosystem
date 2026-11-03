package uz.uzkassa.smartpossupply.domain.permission;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.enums.PermissionType;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "permission",
        indexes = {
                @Index(columnList = "name", name = "permission_name_idx"),
                @Index(columnList = "code", name = "permission_code_idx"),
                @Index(columnList = "parent_Id", name = "permission_parent_id_idx"),
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 3894845988L;

    @Column(name = "name")
    String name;

    @Column(name = "code")
    String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type")
    PermissionType permissionType;

    Integer position;

    @Column(name = "section", columnDefinition = "boolean default false")
    boolean section = false;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Permission parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Permission> children = new HashSet<>();
}
