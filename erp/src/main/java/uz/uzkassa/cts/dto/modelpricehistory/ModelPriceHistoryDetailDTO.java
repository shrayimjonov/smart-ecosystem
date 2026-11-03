package uz.uzkassa.cts.dto.modelpricehistory;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModelPriceHistoryDetailDTO implements Serializable {
    Integer period;
    BigDecimal firsPayment;
    BigDecimal monthlyPayment;
}
