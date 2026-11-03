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
public class ExpeditorCompletedStockOutDTO extends ExpeditorStockOutDTO implements Serializable {
    BigDecimal completedTotalBrutto;
    BigDecimal completedTotalNetto;
    BigDecimal completedTotalQty;
    BigDecimal completedTotalQtyInPackage;

    public ExpeditorCompletedStockOutDTO(Long totalStocks,
                                         Long completedStocks,
                                         BigDecimal totalBrutto,
                                         BigDecimal totalNetto,
                                         BigDecimal totalQty,
                                         BigDecimal totalQtyInPackage,
                                         BigDecimal completedTotalBrutto,
                                         BigDecimal completedTotalNetto,
                                         BigDecimal completedTotalQty,
                                         BigDecimal completedTotalQtyInPackage) {
        super(totalStocks, completedStocks, totalBrutto, totalNetto, totalQty, totalQtyInPackage);
        this.completedTotalBrutto = completedTotalBrutto;
        this.completedTotalNetto = completedTotalNetto;
        this.completedTotalQty = completedTotalQty;
        this.completedTotalQtyInPackage = completedTotalQtyInPackage;
    }
}