package uz.uzkassa.cts.domain.permission;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.LocalizedEntity;
import uz.uzkassa.cts.enums.PermissionType;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permission",
        indexes = {
                @Index(columnList = "code", name = "permission_code_idx"),
                @Index(columnList = "type", name = "permission_type_idx"),
                @Index(columnList = "parent_id", name = "permission_parent_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE permission SET deleted = 'true' WHERE id=?")
public class Permission extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 107495935L;

    @NotNull
    @Column(name = "code", nullable = false)
    String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    PermissionType type;

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
    @SQLRestriction(value = "deleted = 'false'")
    Set<Permission> children = new HashSet<>();

}
