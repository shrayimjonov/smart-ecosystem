package uz.uzkassa.smartpos.dto.fashion.balance;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FashionBalanceHistoryDto {
    private CommonDTO product;
    private LocalDate balanceDate;
    private BigDecimal balanceStart;
    private BigDecimal balanceEnd;
    private BigDecimal income;
    private BigDecimal sale;
    private BigDecimal back;
    private BigDecimal transferIn;
    private BigDecimal transferOut;
    private BigDecimal stockAdjustment;

}
