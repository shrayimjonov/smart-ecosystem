package uz.uzkassa.cts.domain.stock;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.BaseEntity;
import uz.uzkassa.cts.domain.inventory.VariationOption;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "stock_item_variation_option",
        indexes = {
                @Index(columnList = "entity_id", name = "stock_item_variation_option_entity_id_idx"),
                @Index(columnList = "entity_type", name = "stock_item_variation_option_entity_type_idx"),
                @Index(columnList = "entity_id, entity_type", name = "stock_item_variation_option_entity_id_entity_type_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE stock_item_variation_option SET deleted='true' WHERE id=?")
public class StockItemVariationOption extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 4545673L;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    Long entityId;

    @Column(name = "entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @NotNull
    @Column(name = "variation_option_id", nullable = false)
    Long variationOptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variation_option_id", updatable = false, insertable = false)
    VariationOption variationOption;
}
