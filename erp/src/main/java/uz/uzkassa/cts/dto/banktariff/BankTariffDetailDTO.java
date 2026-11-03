package uz.uzkassa.cts.dto.banktariff;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.BankType;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.CustomerAppType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankTariffDetailDTO implements Serializable {
    Long id;
    String name;
    CommonDTO xizmat;
    CustomerAppType appType;
    BigDecimal humoPercentage;
    BigDecimal visaPercentage;
    BigDecimal visaDomesticPercentage;
    BigDecimal uzcardPercentage;
    BankType bankType;
    CommonStatus status;
}
