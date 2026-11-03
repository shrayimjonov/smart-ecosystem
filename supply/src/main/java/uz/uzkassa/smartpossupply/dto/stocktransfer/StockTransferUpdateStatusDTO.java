package uz.uzkassa.smartpossupply.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.StockTransferReason;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferUpdateStatusDTO implements Serializable {

    Long id;

    StockTransferStatus status;

    StockTransferReason reason;

    String otherReason;
}
