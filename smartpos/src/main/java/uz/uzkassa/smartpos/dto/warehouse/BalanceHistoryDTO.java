package uz.uzkassa.smartpos.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.SelectItem;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceHistoryDTO {
    private BigDecimal balanceStart;
    private BigDecimal income;
    private BigDecimal sale;
    private BigDecimal back;
    private BigDecimal transferIn;
    private BigDecimal transferOut;
    private BigDecimal stockAdjustment;
    private BigDecimal balanceEnd;
    private LocalDate balanceDate;
    private SelectItem product;
    private SelectItem unit;
    private SelectItem branch;
}
