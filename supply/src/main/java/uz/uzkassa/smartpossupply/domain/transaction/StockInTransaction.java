package uz.uzkassa.smartpossupply.domain.transaction;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "stock_in_transaction")
@Entity
public class StockInTransaction extends Transaction {

    @Serial
    private static final long serialVersionUID = 346456754L;

    @Column(name = "stock_in_id")
    Long stockInId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_in_id", insertable = false, updatable = false)
    Stock stockIn;

    @Override
    public Long getEntityId() {
        return stockInId;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_IN;
    }

    @Override
    public String getEntityName() {
        return stockIn.getName();
    }
}
