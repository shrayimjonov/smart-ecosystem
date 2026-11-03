package uz.uzkassa.smartpossupply.domain.company;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "partners",
        indexes = {
                @Index(columnList = "supplier_id", name = "partners_supplier_idx"),
                @Index(columnList = "customer_id", name = "partners_customer_idx"),
                @Index(columnList = "company_id", name = "partners_company_idx")
        },
        uniqueConstraints = {@UniqueConstraint(columnNames = {"supplier_id", "customer_id", "company_id"})}
)
public class Partners extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 9349083958934L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CompanyStatus status = CompanyStatus.ACTIVE;

    @Column(name = "supplier_id")
    Long supplierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", updatable = false, insertable = false)
    Company supplier;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Company customer;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "limit_balance", precision = 25, scale = 2)
    BigDecimal limitBalance;

    @Column(name = "current_balance", precision = 25, scale = 2)
    BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(name = "has_limit", columnDefinition = "boolean default false")
    boolean hasLimit;

    @Column(name = "external_id")
    String externalId;

    public Partners() {
        this(null, null, null);
    }

    public Partners(Long supplierId, Long customerId, Long companyId) {
        this(supplierId, customerId, companyId, CompanyStatus.ACTIVE);
    }

    public Partners(Long supplierId, Long customerId, Long companyId, CompanyStatus status) {
        this(supplierId, customerId, companyId,status, null);
    }

    public Partners(Long supplierId, Long customerId, Long companyId, CompanyStatus status, String externalId) {
        this.supplierId = supplierId;
        this.customerId = customerId;
        this.companyId = companyId;
        this.status = status;
        this.externalId = externalId;
    }

    @Override
    public Long getEntityId() {
        return getId();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.PARTNER;
    }

    @Override
    public String getEntityName() {
        return getCompany() != null ? getCompany().getName() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Partners that = (Partners) o;
        return Objects.equals(companyId, that.companyId) &&
                Objects.equals(supplierId, that.supplierId) &&
                Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, supplierId, customerId);
    }
}
