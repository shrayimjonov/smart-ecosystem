package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.cron.CronDeviceSwitchDto;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "DeviceSwitchMapper",
                classes = @ConstructorResult(
                        targetClass = CronDeviceSwitchDto.class,
                        columns = {
                                @ColumnResult(name = "customerDeviceBranchId", type = Long.class),
                                @ColumnResult(name = "deleted", type = Boolean.class),
                                @ColumnResult(name = "customerBranchId", type = Long.class),
                                @ColumnResult(name = "deviceId", type = Long.class),
                                @ColumnResult(name = "status", type = String.class),
                                @ColumnResult(name = "dueDate", type = String.class),
                                @ColumnResult(name = "paid", type = Boolean.class),
                                @ColumnResult(name = "startDate", type = String.class),
                                @ColumnResult(name = "serviceAllowed", type = Boolean.class),
                                @ColumnResult(name = "tariffId", type = Long.class),
                                @ColumnResult(name = "tariffName", type = String.class),
                                @ColumnResult(name = "tariffPeriod", type = Integer.class),
                                @ColumnResult(name = "tariffPeriodType", type = String.class),
                                @ColumnResult(name = "tariffPrice", type = BigDecimal.class),
                                @ColumnResult(name = "tariffSpecialType", type = String.class),
                                @ColumnResult(name = "tariffFreeTrailPeriod", type = Integer.class),
                                @ColumnResult(name = "tariffFreeTrailPeriodType", type = String.class),
                                @ColumnResult(name = "customerId", type = Long.class),
                                @ColumnResult(name = "customerTin", type = String.class),
                        }
                )
        )
})
@Entity
@Table(name = "customer_branch_device",
        indexes = {
                @Index(columnList = "device_id", name = "customer_branch_device_device_id_idx"),
                @Index(columnList = "customer_branch_id", name = "customer_branch_device_cb_id_idx"),
                @Index(columnList = "status", name = "customer_branch_device_status_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE customer_branch_device SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class CustomerBranchDevice extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 4450060376L;

    @NotNull
    @Column(name = "device_id", nullable = false)
    Long deviceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

    @NotNull
    @Column(name = "customer_branch_id", nullable = false)
    Long customerBranchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    DeviceStatus status = DeviceStatus.REGISTERED; //Status can be REGISTERED, BLOCKED, UN_BLOCKED or DELETED

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentType")
    DevicePaymentType paymentType = DevicePaymentType.FULL_PAYMENT;

    @Column(name = "due_date")
    LocalDate dueDate = LocalDate.now();

    @Column(name = "start_date")
    LocalDate startDate = LocalDate.now();

    @Column(name = "initial_date")
    LocalDate initialDate = LocalDate.now();

    @Column(name = "is_free_trail_period", columnDefinition = "boolean default false")
    boolean freeTrailPeriod = false;

    @Column(name = "service_allowed", columnDefinition = "boolean default true")
    boolean serviceAllowed = true;

    @Column(name = "tariff_id")
    Long tariffId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", updatable = false, insertable = false)
    Tariff tariff;

    @Column(name = "contract_id")
    Long contractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", updatable = false, insertable = false)
    Contract contract;

    @Enumerated(EnumType.STRING)
    @Column(name = "suspend_status")
    DeviceSuspendStatus suspendStatus = DeviceSuspendStatus.ACTIVE;

    @Column(name = "suspend_date")
    LocalDate suspendDate;

    @Column(name = "free_period_date")
    LocalDate freePeriodDate;

    @Column(name = "device_type")
    @Enumerated(EnumType.STRING)
    DeviceType deviceType = DeviceType.FISCAL;

    @OneToMany(mappedBy = "customerBranchDevice", cascade = CascadeType.ALL, orphanRemoval = true)
    List<CustomerBranchDevicePaymentType> paymentTypes = new ArrayList<>();

    public void setPaymentType(DevicePaymentType paymentType) {
        addChangeLog("paymentType", this.paymentType != null ? this.paymentType.getName() : null, paymentType != null ? paymentType.getName() : null);
        this.paymentType = paymentType;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public String getEntityName() {
        return customerBranch.getName();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CUSTOMER_BRANCH_DEVICE;
    }
}
