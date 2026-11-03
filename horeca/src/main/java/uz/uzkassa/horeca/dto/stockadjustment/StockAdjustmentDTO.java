package uz.uzkassa.horeca.dto.stockadjustment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentDTO implements Serializable {

    Long id;

    String adjustmentNumber;

    Long branchId;

    Long warehouseId;

    String description;

    LocalDateTime adjustmentDate;

    List<StockAdjustmentItemDTO> items = new ArrayList<>();
}
