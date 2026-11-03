package uz.uzkassa.horeca.domain.techcard;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.unit.Unit;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "tech_card",
        indexes = {
                @Index(columnList = "product_id", name = "tech_card_product_id_idx"),
                @Index(columnList = "unit_id", name = "tech_card_unit_id_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE tech_card SET deleted='true' WHERE id=?")
public class TechCard extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 98983497L;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    Product product;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Column(name = "price", scale = 2, precision = 25)
    BigDecimal price;

    @NotNull
    @Column(name = "unit_id", nullable = false)
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    Unit unit;

    @NotNull
    @Column(name = "size", nullable = false)
    Double size;

    @OneToMany(mappedBy = "techCard", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    List<TechCardItem> items = new ArrayList<>();

    public void addItem(TechCardItem techCardItem) {
        techCardItem.setTechCard(this);
        techCardItem.setTechCardId(this.id);
        items.add(techCardItem);
    }

    @Override
    public String getName() {
        return getSize() + " " + getUnit().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TechCard)) {
            return false;
        }
        return id != null && id.equals(((TechCard) o).id);
    }
}
