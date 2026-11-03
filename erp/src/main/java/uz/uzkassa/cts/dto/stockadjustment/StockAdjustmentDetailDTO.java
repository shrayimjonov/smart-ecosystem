package uz.uzkassa.cts.dto.stockadjustment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.StockStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 86483628544L;

    Long id;

    String adjustmentNumber;

    LocalDateTime adjustmentDate;

    StockStatus status;

    String description;

    CommonDTO company;

    CommonDTO branch;

    CommonDTO warehouse;

    BigDecimal totalQty;

    List<StockAdjustmentItemDetailDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(StockAdjustmentItemDetailDTO item) {
        items.add(item);
    }
}
