package uz.uzkassa.cts.dto.ledger.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BalanceListResponseDto {

    private Long id;
    private String companyName;
    private Long customerId;
    private String tin;
    private LocalDateTime createdDate;
    private BigDecimal balance;
    private BigDecimal prepayed;

    public BalanceListResponseDto(Long id, String companyName, Long customerId, String tin, LocalDateTime createdDate, BigDecimal balance, BigDecimal prepayed) {
        this.id = id;
        this.companyName = companyName;
        this.customerId = customerId;
        this.tin = tin;
        this.createdDate = createdDate;
        this.balance = balance;
        this.prepayed = prepayed;
    }
}
