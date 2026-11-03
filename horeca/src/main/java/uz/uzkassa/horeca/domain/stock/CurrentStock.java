package uz.uzkassa.horeca.domain.stock;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.enums.CurrentStockStatus;
import uz.uzkassa.horeca.enums.EntityTypeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.11.2024
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "current_stock")
public class CurrentStock extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 56165123665L;

    @NotNull
    @Column(name = "menu_item_id", nullable = false)
    Long menuItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", insertable = false, updatable = false)
    MenuItem menuItem;

    @Column(name = "qty", scale = 2, precision = 25)
    BigDecimal qty = BigDecimal.ZERO;

    @Column(name = "sold_qty", scale = 2, precision = 25)
    BigDecimal soldQty = BigDecimal.ZERO;

    @Column(name = "recurring", columnDefinition = "boolean default false")
    boolean recurring = false;

    @Column(name = "transfer_previous_qty", columnDefinition = "boolean default false")
    boolean transferPreviousQty = false;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CurrentStockStatus status = CurrentStockStatus.OPEN;

    @Override
    public Long getEntityId() {
        return null;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CurrentStock)) {
            return false;
        }
        return id != null && id.equals(((CurrentStock) o).id);
    }
}
