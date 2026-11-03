package uz.uzkassa.horeca.domain.unit;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.domain.techcard.TechCard;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "multi_unit",
        indexes = {
                @Index(columnList = "menu_item_id", name = "menu_item_id_idx"),
                @Index(columnList = "tech_card_id", name = "tech_card_id_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE multi_unit set deleted='true' where id=?")
public class MultiUnit extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5099453L;

    @NotNull
    @Column(name = "weight", nullable = false)
    Double weight;

    @NotNull
    @Column(name = "price", scale = 2, precision = 25, nullable = false)
    BigDecimal price;

    @Column(name = "menu_item_id", nullable = false)
    Long menuItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", insertable = false, updatable = false)
    MenuItem menuItem;

    @Column(name = "tech_card_id")
    Long techCardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_card_id", insertable = false, updatable = false)
    TechCard techCard;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultiUnit)) {
            return false;
        }
        return id != null && id.equals(((MultiUnit) o).id);
    }
}
