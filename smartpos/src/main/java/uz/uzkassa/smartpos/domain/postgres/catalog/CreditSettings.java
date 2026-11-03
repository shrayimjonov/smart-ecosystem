package uz.uzkassa.smartpos.domain.postgres.catalog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditSettings implements Serializable {
    private Integer month;
    private BigDecimal price;
    private BigDecimal percent;
}
