package uz.uzkassa.cts.filter;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.KapitalbankPaymentStatus;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class KapitalBankFilter extends BaseFilter {

    private Integer transactionId;
    private BigDecimal amount;
    private KapitalbankPaymentStatus kapitalbankPaymentStatus;
}
