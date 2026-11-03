package uz.uzkassa.smartpossupply.dto.stockadjustment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.AdjustmentReason;
import uz.uzkassa.smartpossupply.enums.AdjustmentRejectReason;
import uz.uzkassa.smartpossupply.enums.CurrencyType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentDTO implements Serializable {

    Long id;

    String adjustmentNumber;

    Long branchId;

    Long warehouseId;

    String description;

    LocalDateTime adjustmentDate;

    AdjustmentRejectReason rejectReason;

    String rejectOtherReason;

    @NotEmpty
    List<StockAdjustmentProductDTO> products = new ArrayList<>();

    AdjustmentDocumentDTO document;

    AdjustmentReason adjustmentReason;

    CurrencyType currencyType;

    BigDecimal exchangeRate;

    @JsonIgnore
    public void addProduct(StockAdjustmentProductDTO stockAdjustmentProductDTO) {
        products.add(stockAdjustmentProductDTO);
    }
}
