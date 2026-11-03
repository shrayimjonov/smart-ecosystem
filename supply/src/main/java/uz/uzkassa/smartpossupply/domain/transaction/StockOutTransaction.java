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
@Table(name = "stock_out_transaction")
@Entity
public class StockOutTransaction extends Transaction {

    @Serial
    private static final long serialVersionUID = 23664L;

    @Column(name = "stock_out_id")
    Long stockOutId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_out_id", insertable = false, updatable = false)
    Stock stockOut;

    @Override
    public Long getEntityId() {
        return stockOutId;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_OUT;
    }

    @Override
    public String getEntityName() {
        return stockOut.getName();
    }

}
