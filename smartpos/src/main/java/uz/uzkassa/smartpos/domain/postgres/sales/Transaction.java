package uz.uzkassa.smartpos.domain.postgres.sales;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.Source;
import uz.uzkassa.smartpos.enums.TransactionOperation;
import uz.uzkassa.smartpos.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "terminal_transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_seq")
    @SequenceGenerator(name = "transaction_id_seq", allocationSize = 10)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String uid;

    private String originUid;

    @NotNull
    @Column(name = "terminal_sn")
    private String deviceSN;

    @NotNull
    @Column(name = "branch_id")
    private Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch branch;

    @NotNull
    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;

    @Column(name = "user_id")
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private BigDecimal amount;

    private String currency;

    @Enumerated(EnumType.STRING)
    private TransactionOperation operation;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private LocalDateTime dateTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private TransactionInfo info;

    @Enumerated(EnumType.STRING)
    private Source source;
}
