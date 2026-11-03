package uz.uzkassa.smartpossupply.dto.reconciliationact;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CalculationDTO {
    BigDecimal deliveryAmount;
    BigDecimal paymentAmount;
    BigDecimal totalQty;

    public CalculationDTO(double v, double l) {
        deliveryAmount = BigDecimal.valueOf(v);
        totalQty = BigDecimal.valueOf(l);
    }
}
