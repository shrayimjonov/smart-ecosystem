package uz.uzkassa.horeca.domain.menu;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.dto.menu.MenuItemDetailItemDTO;
import uz.uzkassa.horeca.dto.product.ProductCommonDTO;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "menu_item",
        indexes = {
                @Index(columnList = "product_id", name = "menu_item_product_id_idx"),
                @Index(columnList = "menu_id", name = "menu_item_menu_id_idx")
        })
@SQLDelete(sql = "UPDATE menu_item set deleted='true' where id=?")
public class MenuItem extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 3423665L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    Product product;

    @Column(name = "photo_id")
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    FileUpload photo;

    @NotNull
    @Column(name = "menu_id", nullable = false)
    Long menuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", insertable = false, updatable = false)
    Menu menu;

    @Column(name = "favourite", columnDefinition = "boolean default false")
    boolean favourite = false;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuItem)) {
            return false;
        }
        return id != null && id.equals(((MenuItem) o).id);
    }
}
