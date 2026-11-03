package uz.uzkassa.smartpos.dto.terminal.sales;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.sales.Transaction;
import uz.uzkassa.smartpos.domain.postgres.sales.TransactionInfo;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.TransactionOperation;
import uz.uzkassa.smartpos.enums.TransactionStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for {@link Transaction}
 */
@Getter
@Setter
public final class TransactionCreateDto implements Serializable {
    private @NotNull String uid;
    private String originUid;
    private @NotNull String terminalSN;
    private @NotNull Long branchId;
    private @NotNull Long companyId;
    private Long userId;
    private PaymentType paymentType;
    private BigDecimal amount;
    private String currency;
    private TransactionOperation operation;
    private TransactionStatus status;
    private LocalDateTime dateTime;
    private TransactionInfo info;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionCreateDto that = (TransactionCreateDto) o;
        return Objects.equals(uid, that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid);
    }
}