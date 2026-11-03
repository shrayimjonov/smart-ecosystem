package uz.uzkassa.smartpos.integration.cto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class ModelPriceHistoryDetailDTO implements Serializable {
    private Integer period;
    private BigDecimal firsPayment;
    private BigDecimal monthlyPayment;
}
