package uz.uzkassa.silen.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AmountDTO implements Serializable {
    private BigDecimal productionAmount;
    private BigDecimal realizedAmount;
    private BigDecimal balanceAmount;

    public AmountDTO(GeneralDTO generalDTO) {
        this.productionAmount = generalDTO.getProductionAmount();
        this.realizedAmount = generalDTO.getRealizedAmount();
        this.balanceAmount = generalDTO.getBalanceAmount();
    }
}
