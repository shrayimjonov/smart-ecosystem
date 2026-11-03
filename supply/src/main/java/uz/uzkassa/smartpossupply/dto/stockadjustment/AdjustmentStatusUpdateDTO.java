package uz.uzkassa.smartpossupply.dto.stockadjustment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.AdjustmentRejectReason;
import uz.uzkassa.smartpossupply.enums.StockAdjustmentStatus;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdjustmentStatusUpdateDTO implements Serializable {

    StockAdjustmentStatus status;

    AdjustmentRejectReason reason;

    String otherReason;
}
