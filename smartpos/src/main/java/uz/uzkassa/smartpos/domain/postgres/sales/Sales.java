package uz.uzkassa.smartpos.domain.postgres.sales;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.receipt.SalesDetail;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.Source;
import uz.uzkassa.smartpos.enums.sales.SalesStatus;
import uz.uzkassa.smartpos.enums.sales.SalesType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@DynamicUpdate
public class Sales {
    @Id
    @GenericGenerator(
            name = "sequenceGenerator",
            type = SequenceStyleGenerator.class,
            parameters = {
                    @org.hibernate.annotations.Parameter(
                            name = "optimizer",
                            value = "pooled-lo"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "initial_value",
                            value = "1"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "increment_size",
                            value = "1"
                    )
            }
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sequenceGenerator"
    )
    private Long id;
    @Version
    private Long version;

    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;

    @Column(name = "receipt_date_time")
    private LocalDateTime receiptDateTime;

    @NotNull
    @Column(length = 50, unique = true, nullable = false)
    private String uid;
    @Column(length = 50, name = "origin_uid")
    private String originUid;

    @Column(name = "fiscal_sign", length = 50)
    private String fiscalSign;
    @Column(name = "fiscal_url", columnDefinition = "text")
    private String fiscalUrl;
    @Column(name = "shift_number")
    private Long shiftNumber;

    private Double lat;
    private Double lon;

    @NotNull
    @Column(name = "total_cost", precision = 25, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "total_paid", precision = 25, scale = 2)
    private BigDecimal totalPaid;

    @Column(name = "discount_percent")
    private BigDecimal discountPercent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SalesType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SalesStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 50, updatable = false)
    private Source source;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<PaymentType, BigDecimal> payments;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch branch;

    @NotNull
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;

    @NotNull
    @Column(name = "created_by_id")
    private Long createdById;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User createdBy;

    @Column(name = "terminal_sn")
    private String terminalSN;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_sn", referencedColumnName = "serial_number", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Device terminal;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private SalesAdditional additional;

    @Column(name = "factura_id")
    private String facturaId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sales", orphanRemoval = true)
    private Set<SalesDetail> salesDetails = new HashSet<>();

    @Column(name = "promotion_id")
    private Long promotionId;

    public boolean isFiscal() {
        return StringUtils.isNotBlank(fiscalUrl);
    }

    public void addChild(SalesDetail salesDetail) {
        salesDetail.setSales(this);
        salesDetails.add(salesDetail);
    }
}
