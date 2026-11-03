package uz.uzkassa.smartpossupply.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpeditorStockTransferStatisticsDTO implements Serializable {

    Long orderTotal;

    Long stockOutTotal;

    Long invoiceTotal;

    Long receiptTotal;
}
