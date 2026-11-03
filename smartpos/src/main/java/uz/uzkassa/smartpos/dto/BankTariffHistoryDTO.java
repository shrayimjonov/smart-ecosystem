package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.enums.BankType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class BankTariffHistoryDTO implements Serializable {
    @NotNull
    String tin;
    String oldTin;
    @NotNull
    Long customerId;
    BigDecimal humoPercentage;
    BigDecimal visaPercentage;
    BigDecimal visaDomesticPercentage;
    BigDecimal uzcardPercentage;
    @NotNull
    BankType bankType;
    @NotNull
    CommonDTO xizmat;
    LocalDateTime date;
}
