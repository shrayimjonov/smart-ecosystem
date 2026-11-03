package uz.uzkassa.cts.dto.banktariff;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.BankType;
import uz.uzkassa.cts.enums.CustomerAppType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BankTariffDTO extends CommonDTO implements Serializable {
    BigDecimal humoPercentage;
    BigDecimal visaPercentage;
    BigDecimal visaDomesticPercentage;
    BigDecimal uzcardPercentage;
    Long xizmatId;
    String xizmatName;
    CustomerAppType appType;
    BankType bankType;
}
