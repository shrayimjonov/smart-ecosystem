package uz.uzkassa.horeca.domain.menu;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.dto.menu.MenuTreeDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.ReservationStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "menu",
        indexes = {
                @Index(columnList = "branch_id", name = "menu_branch_id_idx"),
                @Index(columnList = "parent_id", name = "menu_parent_id_idx")
        })
@SQLDelete(sql = "UPDATE menu set deleted='true' where id=?")
public class Menu extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4533227L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    Menu parent;

    @Column(name = "photo_id")
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    FileUpload photo;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted='false'")
    @OrderBy("id")
    List<MenuItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.IN_ACTIVE;

    public void addItem(MenuItem menuItem) {
        menuItem.setMenu(this);
        menuItem.setMenuId(this.id);
        items.add(menuItem);
    }

    public MenuTreeDTO toTreeDTO() {
        return new MenuTreeDTO(getId(), getName(), getParentId(), getStatus());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Menu)) {
            return false;
        }
        return id != null && id.equals(((Menu) o).id);
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.MENU;
    }
}
