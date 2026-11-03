package uz.uzkassa.horeca.domain.billing;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.domain.company.Company;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "usage_plan", uniqueConstraints = {@UniqueConstraint(columnNames = {"company_id", "service_type"})},
        indexes = {
                @Index(columnList = "company_id", name = "usage_plan_company_id_idx"),
                @Index(columnList = "service_type", name = "usage_plan_service_type_idx")
        })
public class UsagePlan extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 457834L;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "service_type")
    String serviceType;

    @Column(name = "qty")
    int qty;

    @Column(name = "start_date")
    LocalDateTime startDate;

    @Column(name = "end_date")
    LocalDateTime endDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UsagePlan)) {
            return false;
        }
        return id != null && id.equals(((UsagePlan) o).id);
    }


}
