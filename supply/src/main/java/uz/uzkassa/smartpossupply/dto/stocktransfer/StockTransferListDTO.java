package uz.uzkassa.smartpossupply.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferListDTO implements Serializable {

    Long id;

    String transferNumber;

    LocalDateTime transferDate;

    CommonDTO fromBranch;

    CommonDTO toBranch;

    CommonDTO fromWarehouse;

    CommonDTO toWarehouse;

    StockTransferStatus status;

    BigDecimal totalQty;

    CommonDTO stock;

    CommonDTO trip;
}
