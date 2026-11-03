package uz.uzkassa.smartpos.domain.postgres.receipt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.enums.PaymentStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "payment")
@Getter
@Setter
@DynamicUpdate
public class Payment extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_code", nullable = false)
    private String accountCode;

    @Column(name = "ext_trs_id")
    private String ext_trs_id;

    @Column(name = "description")
    private String description;

    @Column(name = "receipt_uid", nullable = false)
    private String receiptUid;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status = PaymentStatus.PENDING;
}
