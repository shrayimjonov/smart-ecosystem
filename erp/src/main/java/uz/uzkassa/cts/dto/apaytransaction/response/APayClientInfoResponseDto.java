package uz.uzkassa.cts.dto.apaytransaction.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class APayClientInfoResponseDto {
    private Long deviceCount;
    private String tin;
    private String customerName;
    private BigDecimal needAmount;
    private BigDecimal balance;
}
