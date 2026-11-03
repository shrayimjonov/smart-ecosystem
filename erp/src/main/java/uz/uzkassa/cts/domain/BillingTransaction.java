package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 **/

@Entity
@Table(name = "billing_transaction")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingTransaction extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 750232591L;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @NotNull
    @Column(name = "transaction_date", nullable = false)
    LocalDateTime transactionDate;

    @NotNull
    @Column(name = "transaction_id", nullable = false)
    String transactionId;

    @Column(name = "ledger_id")
    String ledgerId;

    @NotNull
    @Column(name = "amount", nullable = false)
    BigDecimal amount;

    @Column(name = "calculated", columnDefinition = "boolean default false")
    boolean calculated = false;

    @Column(name = "remainder")
    BigDecimal remainder;
}
