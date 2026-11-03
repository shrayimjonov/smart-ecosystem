package uz.uzkassa.horeca.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.user.UserDTO;
import uz.uzkassa.horeca.enums.StockTransferStatus;
import uz.uzkassa.horeca.enums.StockTransferType;

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

    StockTransferType transferType;

    LocalDateTime transferDate;

    String transferNumber;

    StockTransferStatus status;

    String description;

    CommonDTO fromBranch;

    CommonDTO toBranch;

    CommonDTO fromWarehouse;

    CommonDTO toWarehouse;

    BigDecimal totalPrice;

    BigDecimal totalQty;

    CommonDTO createdBy;

    String comment;
}
