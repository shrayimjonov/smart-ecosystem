package uz.uzkassa.cts.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.StockTransferStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9948938L;

    Long id;

    LocalDateTime transferDate;

    String transferNumber;

    StockTransferStatus status;

    CompanyCommonDTO company;

    CommonDTO fromBranch;

    CommonDTO toBranch;

    CommonDTO fromWarehouse;

    CommonDTO toWarehouse;

    BigDecimal totalQty;
}
