package uz.uzkassa.horeca.domain.techcard;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.unit.Unit;
import uz.uzkassa.horeca.enums.CookingType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "tech_card_item",
        indexes = {
                @Index(columnList = "tech_card_id", name = "tech_card_item_tech_card_id_idx"),
                @Index(columnList = "ingredient_id", name = "tech_card_item_ingredient_id_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE tech_card_item SET deleted='true' WHERE id=?")
public class TechCardItem extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 23233458L;

    @NotNull
    @Column(name = "tech_card_id", nullable = false)
    Long techCardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_card_id", insertable = false, updatable = false)
    TechCard techCard;

    @NotNull
    @Column(name = "ingredient_id", nullable = false)
    Long ingredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", insertable = false, updatable = false)
    Product ingredient;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cooking_types")
    List<CookingType> cookingTypes;

    @NotNull
    @Column(name = "gross", scale = 2, precision = 25, nullable = false)
    BigDecimal gross;

    @Column(name = "net", scale = 2, precision = 25)
    BigDecimal net;


    @Column(name = "in_kg", scale = 2, precision = 25)
    BigDecimal inKg;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    Unit unit;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TechCardItem)) {
            return false;
        }
        return id != null && id.equals(((TechCardItem) o).id);
    }
}
