package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.time.LocalDate;

@Entity
@Table(name = "free_period_history")
@Getter
@Setter
@SQLDelete(sql = "UPDATE free_period_history SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class FreePeriodHistory extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 7023290573L;

    @Column(name = "free_period_date")
    private LocalDate freePeriodDate;
    @Column(name = "customer_branch_device_id", nullable = false)
    private Long customerBranchDeviceId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_device_id", updatable = false, insertable = false)
    CustomerBranchDevice customerBranchDevice;
    @Column(name = "customer_id")
    private Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;
}
