package uz.uzkassa.horeca.dto.stocktransfer;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.StockTransferType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5656653L;

    Long id;

    StockTransferType stockTransferType;

    String transferNumber;

    Long fromWarehouseId;

    Long toWarehouseId;

    Long fromBranchId;

    Long toBranchId;

    LocalDateTime transferDate;

    String description;

    List<StockTransferItemDTO> items;
}
