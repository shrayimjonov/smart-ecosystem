package uz.uzkassa.cts.dto.stockadjustment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7368572643L;

    Long id;

    Long companyId;

    Long branchId;

    Long warehouseId;

    String description;

    LocalDateTime adjustmentDate;

    String adjustmentNumber;

    List<StockAdjustmentItemDTO> items = new ArrayList<>();
}
