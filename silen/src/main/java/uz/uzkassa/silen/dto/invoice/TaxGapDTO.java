package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class TaxGapDTO implements Serializable {
    BigDecimal taxGap;
}
