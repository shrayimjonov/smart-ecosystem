package uz.uzkassa.silen.dto.mq;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class VatRatePayload {
    Long id;
    String code;
    BigDecimal oldAmount;
    BigDecimal newAmount;
}
