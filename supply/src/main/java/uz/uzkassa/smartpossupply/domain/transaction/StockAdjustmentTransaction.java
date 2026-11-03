package uz.uzkassa.smartpossupply.domain.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustment;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "stock_adjustment_transaction")
@Entity
public class StockAdjustmentTransaction extends Transaction implements Auditable {

    @Serial
    private static final long serialVersionUID = 56764564L;

    @Column(name = "stock_adjustment_id")
    Long stockAdjustmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_adjustment_id", insertable = false, updatable = false)
    StockAdjustment stockAdjustment;

    @Override
    public Long getEntityId() {
        return stockAdjustmentId;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_ADJUSTMENT;
    }

    @Override
    public String getEntityName() {
        return stockAdjustment.getName();
    }
}
