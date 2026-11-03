package uz.uzkassa.smartpossupply.domain.transaction;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serial;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 13.03.2023 15:38
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "stock_return_transaction")
@Entity
public class StockReturnTransaction extends Transaction {

    @Serial
    private static final long serialVersionUID = 3464564L;

    @Column(name = "stock_return_id")
    Long stockReturnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_return_id", insertable = false, updatable = false)
    Stock stockReturn;

    @Override
    public Long getEntityId() {
        return stockReturnId;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_RETURN;
    }

    @Override
    public String getEntityName() {
        return getStockReturn().getName();
    }

}
