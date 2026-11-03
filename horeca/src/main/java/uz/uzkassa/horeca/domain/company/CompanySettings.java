package uz.uzkassa.horeca.domain.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.branch.Branch;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "company_settings",
        indexes = {
                @Index(columnList = "company_id", name = "company_settings_company_id_idx", unique = true),
                @Index(columnList = "branch_id", name = "company_settings_branch_id_idx")
        })
public class CompanySettings extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 974387583L;

    @Column(name = "fee_percent", scale = 2, precision = 25)
    BigDecimal feePercent;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    public boolean hasFee() {
        return getFeePercent() != null && getFeePercent().compareTo(BigDecimal.ZERO) != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompanySettings)) {
            return false;
        }
        return id != null && id.equals(((CompanySettings) o).id);
    }
}
