package uz.uzkassa.cts.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.time.LocalDate;
import java.time.Month;

@Entity
@Table(name = "device_suspend_history")
@Getter
@Setter
@SQLDelete(sql = "UPDATE device_suspend_history SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class DeviceSuspendHistory extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 5247233186L;

    @NotNull
    @Column(name = "customer_branch_device_id", nullable = false)
    private Long customerBranchDeviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_device_id", updatable = false, insertable = false)
    private CustomerBranchDevice customerBranchDevice;

    @NotNull
    @Column(name = "customer_branch_id", nullable = false)
    Long customerBranchId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;

    @NotNull
    @Column(name = "tariff_id", nullable = false)
    private Long tariffId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", updatable = false, insertable = false)
    private Tariff tariff;

    @NotNull
    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Contract contract;

    @Column(name = "device_serial_number")
    private String deviceSerialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "month", nullable = false)
    private Month month;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "active", columnDefinition = "boolean default false")
    private boolean active = true;
}
