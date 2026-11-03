package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.domain.permission.Permission;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "folder",
        indexes = {
                @Index(columnList = "permission_id", name = "folder_permission_id_idx"),
                @Index(columnList = "parent_id", name = "folder_parent_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE folder SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class Folder extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 7136931199L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "permission_id")
    Long permissionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", updatable = false, insertable = false)
    Permission permission;

    @Column(name = "parent_id")
    Long parentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Folder parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false'")
    @OrderBy("id")
    Set<Folder> children = new HashSet<>();
}
