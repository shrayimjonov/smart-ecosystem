package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.customer.AccountReceivableMapDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 08.01.2024
 */
@SqlResultSetMappings(
        @SqlResultSetMapping(
                name = "AccountReceivableReportMapper",
                classes = @ConstructorResult(
                        targetClass = AccountReceivableMapDTO.class,
                        columns = {
                                @ColumnResult(name = "operationId", type = Long.class),
                                @ColumnResult(name = "operationNumber", type = String.class),
                                @ColumnResult(name = "operationType", type = String.class),
                                @ColumnResult(name = "stockOutId", type = Long.class),
                                @ColumnResult(name = "stockOutNumber", type = String.class),
                                @ColumnResult(name = "operationDate", type = LocalDateTime.class),
                                @ColumnResult(name = "totalPrice", type = BigDecimal.class),
                                @ColumnResult(name = "customerId", type = Long.class),
                                @ColumnResult(name = "customerName", type = String.class),
                                @ColumnResult(name = "customerTin", type = String.class),
                                @ColumnResult(name = "customerBrand", type = String.class),
                                @ColumnResult(name = "planRouteId", type = Long.class),
                                @ColumnResult(name = "planRouteNumber", type = String.class),
                                @ColumnResult(name = "agentId", type = Long.class),
                                @ColumnResult(name = "agentFirstName", type = String.class),
                                @ColumnResult(name = "agentLastName", type = String.class),
                                @ColumnResult(name = "expeditorId", type = Long.class),
                                @ColumnResult(name = "expeditorFirstName", type = String.class),
                                @ColumnResult(name = "expeditorLastName", type = String.class),
                                @ColumnResult(name = "facturaId", type = Long.class),
                                @ColumnResult(name = "facturaNo", type = String.class),
                                @ColumnResult(name = "facturaStatus", type = String.class),
                                @ColumnResult(name = "facturaDate", type = LocalDate.class),
                        }
                )
        )
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "financial_operation",
        indexes = {
                @Index(columnList = "customer_id", name = "financial_operation_customer_id_idx"),
                @Index(columnList = "customer_branch_id", name = "financial_operation_customer_branch_id_idx"),
                @Index(columnList = "company_id", name = "financial_operation_company_id_idx"),
        }
)
public class FinancialOperation extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 6547654844L;

    @Column(name = "operation_number")
    String operationNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "operation_type")
    @Enumerated(EnumType.STRING)
    FinancialOperationType operationType;

    @Column(name = "amount", precision = 25, scale = 2)
    BigDecimal amount;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    PaymentType paymentType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Company customer;

    @Column(name = "customer_branch_id")
    Long customerBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "stock_out_id")
    Long stockOutId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_out_id", updatable = false, insertable = false)
    Stock stockOut;

    @Column(name = "factura_id")
    Long facturaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", updatable = false, insertable = false)
    Factura factura;

    @Column(name = "payment_received_date")
    LocalDateTime paymentReceivedDate;

    @Column(name = "foreign_price", precision = 25, scale = 2)
    BigDecimal foreignPrice;

    @Column(name = "currency_type")
    @Enumerated(value = EnumType.STRING)
    CurrencyType currencyType;

    @Column(name = "exchange_rate", precision = 25, scale = 2)
    BigDecimal exchangeRate;

    @Override
    public String getName() {
        return this.getOperationNumber();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.FINANCIAL_OPERATION;
    }

    @Override
    public String getEntityName() {
        return getName();
    }
}
