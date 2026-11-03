package uz.uzkassa.smartpossupply.dto.integration;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.DebitOrCredit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class LedgerDetailResponseDto implements Serializable {

    private String id;
    private String tin;
    private String accountNumber;
    private String customerName;
    private String description;
    private BigDecimal amount;
    private DebitOrCredit debitOrCredit;
    private String ledgerId;
    private LocalDateTime createdDate;
}
