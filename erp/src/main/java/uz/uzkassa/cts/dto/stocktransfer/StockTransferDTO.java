package uz.uzkassa.cts.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferDTO implements Serializable {

    Long id;

    String transferNumber;

    Long fromWarehouseId;

    Long toWarehouseId;

    Long fromBranchId;

    Long toBranchId;

    Long companyId;

    LocalDateTime transferDate;

    String description;

    List<StockTransferItemDTO> items;
}
