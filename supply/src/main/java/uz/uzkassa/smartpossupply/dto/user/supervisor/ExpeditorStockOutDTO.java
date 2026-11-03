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
public class ExpeditorStockOutDTO implements Serializable {
    Long totalStocks;
    Long completedStocks;

    BigDecimal totalBrutto;
    BigDecimal totalNetto;
    BigDecimal totalQty;
    BigDecimal totalQtyInPackage;
}
