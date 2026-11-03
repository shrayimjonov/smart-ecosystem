package uz.uzkassa.smartpos.dto.fashion.balance;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FashionColorGroupHistoryDto {
    private Long colorGroupId;
    private String colorGroupCode;
    private String colorGroupName;
    private String printName;
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
