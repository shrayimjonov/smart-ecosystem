package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.ledger.response.BalanceListResponseDto;
import uz.uzkassa.cts.dto.ledger.response.LedBalanceDto;
import uz.uzkassa.cts.dto.ledger.response.LedgerBalanceDto;
import uz.uzkassa.cts.enums.ServiceType;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SqlResultSetMappings({
    @SqlResultSetMapping(
        name = "LedgerBalanceDtoMapper",
        classes = @ConstructorResult(
            targetClass = LedgerBalanceDto.class,
            columns = {

                @ColumnResult(name = "serId", type = Long.class),
                @ColumnResult(name = "serTransactionNumber", type = String.class),
                @ColumnResult(name = "serTin", type = String.class),
                @ColumnResult(name = "serLink", type = String.class),
                @ColumnResult(name = "serPaymentDate", type = String.class),
                @ColumnResult(name = "serCustomerName", type = String.class),
                @ColumnResult(name = "serAccountNumber", type = String.class),
                @ColumnResult(name = "serDebit", type = BigDecimal.class),
                @ColumnResult(name = "serCredit", type = BigDecimal.class),
                @ColumnResult(name = "serBalance", type = BigDecimal.class),
                @ColumnResult(name = "serServiceType", type = String.class)
            }
        )
    ),
    @SqlResultSetMapping(
        name = "LedgerBalanceMapper",
        classes = @ConstructorResult(
            targetClass = LedBalanceDto.class,
            columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "account_number", type = String.class),
                @ColumnResult(name = "balance", type = BigDecimal.class),
                @ColumnResult(name = "credit", type = BigDecimal.class),
                @ColumnResult(name = "customer_name", type = String.class),
                @ColumnResult(name = "debit", type = BigDecimal.class),
                @ColumnResult(name = "link", type = String.class),
                @ColumnResult(name = "payment_date", type = String.class),
                @ColumnResult(name = "tin", type = String.class),
                @ColumnResult(name = "transaction_number", type = String.class),
                @ColumnResult(name = "service_type", type = String.class)

            }
        )
    ),
    @SqlResultSetMapping(
        name = "BalanceListResponseDtoMapper",
        classes = @ConstructorResult(
            targetClass = BalanceListResponseDto.class,
            columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "companyName", type = String.class),
                @ColumnResult(name = "customerId", type = Long.class),
                @ColumnResult(name = "tin", type = String.class),
                @ColumnResult(name = "createdDate", type = LocalDateTime.class),
                @ColumnResult(name = "balance", type = BigDecimal.class),
                @ColumnResult(name = "prepaid", type = BigDecimal.class)
            }
        )
    )

})
@Entity
@Table(name = "ledger_detail", indexes = {
    @Index(columnList = "tin", name = "ledger_detail_tin_idx"),
    @Index(columnList = "customer_id", name = "ledger_detail_customer_id_idx")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE ledger_detail SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class LedgerDetail extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3417136678L;

    @NotNull
    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "transaction_number")
    private String transactionNumber;

    @NotNull
    @Column(name = "customer_name")
    private String customerName;

    @NotNull
    @Column(name = "tin")
    private String tin;

    @Column(name = "customer_id")
    private Long customerId;

    @NotNull
    @Column(name = "link")
    private String link;

    @NotNull
    @Column(name = "debit", precision = 15, scale = 2)
    private BigDecimal debit;

    @NotNull
    @Column(name = "credit", precision = 15, scale = 2)
    private BigDecimal credit;

    @NotNull
    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "service_type")
    @Enumerated(value = EnumType.STRING)
    private ServiceType serviceType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ledger_id")
    private Ledger ledger;
}
