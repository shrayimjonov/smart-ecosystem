package uz.uzkassa.horeca.dto.stockadjustment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.StockAdjustmentStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentDetailDTO implements Serializable {

    Long id;

    LocalDateTime adjustmentDate;

    String adjustmentNumber;

    StockAdjustmentStatus status;

    CommonDTO branch;

    CommonDTO warehouse;

    String description;

    BigDecimal totalQty;

    List<StockAdjustmentItemDetailDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(StockAdjustmentItemDetailDTO stockAdjustmentItemDetailDTO) {
        items.add(stockAdjustmentItemDetailDTO);
    }
}
