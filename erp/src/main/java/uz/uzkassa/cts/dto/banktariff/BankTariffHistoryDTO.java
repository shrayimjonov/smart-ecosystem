package uz.uzkassa.cts.dto.banktariff;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankTariffHistoryDTO implements Serializable {
    String tin;
    String oldTin;
    Long customerId;
    BigDecimal humoPercentage;
    BigDecimal visaPercentage;
    BigDecimal visaDomesticPercentage;
    BigDecimal uzcardPercentage;
    String bankType;
    LocalDateTime date;
    CommonDTO xizmat;
}
