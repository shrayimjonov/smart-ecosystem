package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class TerminalReceiptDetailDTO extends TerminalReceiptDetailGeneralDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5645634L;

    String committentTin;
    Long categoryId;
    @Size(max = 255)
    String categoryName;
    Long receiptId;
    Long branchId;
    Long promotionId;
    Long hitCount;
    Double discountByPromotionPercent;
    BigDecimal discountAmountForPromotion;
    TerminalReceiptExtraDetailDTO extraDetail;

    Long menuItemId;
    Long multiUnitId;

    public boolean sendRegister() {
        return productId != null && unitId != null && qty != null;
    }
}
