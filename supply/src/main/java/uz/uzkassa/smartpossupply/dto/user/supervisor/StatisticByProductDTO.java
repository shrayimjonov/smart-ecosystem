package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class StatisticByProductDTO {
    Long productId;
    String productName;
    Long unitId;
    String unitName;
    BigDecimal totalBrutto;
    BigDecimal totalNetto;
    BigDecimal totalQty;
    Long totalQtyInPackage;
}
