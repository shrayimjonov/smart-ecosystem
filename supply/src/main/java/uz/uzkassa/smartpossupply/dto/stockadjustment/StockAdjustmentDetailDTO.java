package uz.uzkassa.smartpossupply.dto.stockadjustment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.AdjustmentReason;
import uz.uzkassa.smartpossupply.enums.AdjustmentRejectReason;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.StockAdjustmentStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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

    BigDecimal totalPrice;

    BigDecimal totalForeignPrice;

    AdjustmentDocumentDTO document;

    AdjustmentReason adjustmentReason;

    AdjustmentRejectReason rejectReason;

    String rejectOtherReason;

    LocalDateTime lastModifiedDate;

    CurrencyType currencyType;

    BigDecimal exchangeRate;

    List<StockAdjustmentProductDetailDTO> products = new ArrayList<>();

    @JsonIgnore
    public void addProduct(StockAdjustmentProductDetailDTO stockAdjustmentProductDTO) {
        products.add(stockAdjustmentProductDTO);
    }
}
