package uz.uzkassa.cts.dto.bonus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BonusStatisticsDTO implements Serializable {

    BigDecimal totalAmount;

    BigDecimal totalBonusAmount;
}
