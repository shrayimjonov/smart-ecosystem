package uz.uzkassa.smartpossupply.domain.transaction;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransfer;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "stock_transfer_transaction")
@Entity
public class StockTransferTransaction extends Transaction {

    @Serial
    private static final long serialVersionUID = 2225645L;

    @Column(name = "stock_transfer_id")
    Long stockTransferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_id", insertable = false, updatable = false)
    StockTransfer stockTransfer;

    @Override
    public Long getEntityId() {
        return stockTransferId;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_TRANSFER;
    }

    @Override
    public String getEntityName() {
        return getStockTransfer().getName();
    }
}
