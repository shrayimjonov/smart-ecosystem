package uz.uzkassa.horeca.domain.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.util.Objects;


@Getter
@Setter
@Entity
@Table(
    name = "partner",
    indexes = {
        @Index(columnList = "supplier_id", name = "partners_supplier_idx"),
        @Index(columnList = "customer_id", name = "partners_customer_idx"),
        @Index(columnList = "company_id", name = "partners_company_idx"),
    },
    uniqueConstraints = { @UniqueConstraint(columnNames = { "supplier_id", "customer_id", "company_id" }) }
)
public class Partner extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 4353454L;

    public Partner() {
        this(null, null, null);
    }

    public Partner(Long supplierId, Long customerId, Long companyId) {
        this.supplierId = supplierId;
        this.customerId = customerId;
        this.companyId = companyId;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @NotNull
    @Column(name = "supplier_id", nullable = false)
    Long supplierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", updatable = false, insertable = false)
    Company supplier;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Company customer;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partner that = (Partner) o;
        return (
            Objects.equals(companyId, that.companyId) &&
            Objects.equals(supplierId, that.supplierId) &&
            Objects.equals(customerId, that.customerId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, supplierId, customerId);
    }
}
