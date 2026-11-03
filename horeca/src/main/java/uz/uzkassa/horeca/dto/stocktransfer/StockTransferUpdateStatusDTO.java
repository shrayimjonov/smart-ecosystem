package uz.uzkassa.horeca.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.StockTransferStatus;
import uz.uzkassa.horeca.enums.StockTransferType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferUpdateStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5656653L;

    StockTransferStatus status;

    String rejectReason;
}
