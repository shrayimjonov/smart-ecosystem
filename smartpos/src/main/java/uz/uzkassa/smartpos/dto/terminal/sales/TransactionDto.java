package uz.uzkassa.smartpos.dto.terminal.sales;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.sales.TransactionInfo;
import uz.uzkassa.smartpos.dto.UserShortDto;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.TransactionOperation;
import uz.uzkassa.smartpos.enums.TransactionStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.sales.Transaction}
 */
@Getter
@Setter
public class TransactionDto implements Serializable {
    private Long id;
    @NotNull
    private String uid;
    private CommonDTO branch;
    private CommonDTO company;
    private UserShortDto user;
    private PaymentType paymentType;
    private BigDecimal amount;
    private String currency;
    private TransactionOperation operation;
    private TransactionStatus status;
    private LocalDateTime dateTime;
    private TransactionInfo info;
}