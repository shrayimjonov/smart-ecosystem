package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpeditorCancelledStockOutDTO extends ExpeditorStockOutDTO implements Serializable {
    BigDecimal cancelledBrutto;
    BigDecimal cancelledNetto;
    BigDecimal cancelledQty;
    BigDecimal cancelledQtyInPackage;

    public ExpeditorCancelledStockOutDTO(Long totalStocks,
                                         Long completedStocks,
                                         BigDecimal totalBrutto,
                                         BigDecimal totalNetto,
                                         BigDecimal totalQty,
                                         BigDecimal totalQtyInPackage,
                                         BigDecimal cancelledBrutto,
                                         BigDecimal cancelledNetto,
                                         BigDecimal cancelledQty,
                                         BigDecimal cancelledQtyInPackage) {
        super(totalStocks, completedStocks, totalBrutto, totalNetto, totalQty, totalQtyInPackage);
        this.cancelledBrutto = cancelledBrutto;
        this.cancelledNetto = cancelledNetto;
        this.cancelledQty = cancelledQty;
        this.cancelledQtyInPackage = cancelledQtyInPackage;

    }
}
