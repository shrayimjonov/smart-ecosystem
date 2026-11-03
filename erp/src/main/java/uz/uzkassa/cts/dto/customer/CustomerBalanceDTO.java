package uz.uzkassa.cts.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.soliq.CompanyBankInfoDTO;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBalanceDTO implements Serializable {

    String tin;

    BigDecimal terminalCost;

    BigDecimal customerBalance;

    Integer unpaidCount;

    Integer fiscalizationCount;

    String formula;

    CompanyBankInfoDTO bank;

}
