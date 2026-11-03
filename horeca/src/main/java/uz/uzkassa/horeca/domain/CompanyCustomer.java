package uz.uzkassa.horeca.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.company.Company;

import java.io.Serial;
import java.util.Objects;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "company_customer",
        indexes = {
                @Index(columnList = "branch_id", name = "company_customer_branch_id_idx"),
                @Index(columnList = "customer_id", name = "company_customer_customer_id_idx"),
                @Index(columnList = "company_id", name = "company_customer_company_id_idx")
        },
        uniqueConstraints = {@UniqueConstraint(columnNames = {"branch_id", "customer_id", "company_id"})}
)
public class CompanyCustomer extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 93495984958934L;

    @NotNull
    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    User customer;

    @NotNull
    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CompanyCustomer that = (CompanyCustomer) o;
        return Objects.equals(companyId, that.companyId) &&
                Objects.equals(branchId, that.branchId) &&
                Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, branchId, customerId);
    }
}
