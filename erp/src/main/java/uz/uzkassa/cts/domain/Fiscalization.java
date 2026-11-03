package uz.uzkassa.cts.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.Reason;

import java.io.Serial;

@Entity
@Table(name = "fiscalization",
        indexes = {
                @Index(columnList = "device_id", name = "fiscalization_device_id_idx"),
                @Index(columnList = "fiscal_module_id", name = "fiscalization_fiscal_module_id_idx"),
                @Index(columnList = "hash_key", name = "fiscalization_hash_key_idx"),
                @Index(columnList = "status", name = "fiscalization_status_idx"),
                @Index(columnList = "branch_id", name = "fiscalization_branch_id_idx"),
                @Index(columnList = "customer_id", name = "fiscalization_customer_id_idx"),
                @Index(columnList = "customer_branch_id", name = "fiscalization_customer_branch_id_idx"),
                @Index(columnList = "ofd_application_id", name = "fiscalization_ofd_application_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE fiscalization SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Fiscalization extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1817972398L;

    @NotNull
    @Column(name = "device_id", nullable = false)
    Long deviceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

    @NotNull
    @Column(name = "fiscal_module_id", nullable = false)
    Long fiscalModuleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiscal_module_id", updatable = false, insertable = false)
    FiscalModule fiscalModule;

    @NotNull
    @NotEmpty
    @Column(name = "hash_key", nullable = false, unique = true)
    String hashKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    DeviceStatus status = DeviceStatus.FISCAL; //status can be FISCAL, FISCAL_ACTIVE or DELETED

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    Reason reason;

    @Column(name = "other_reason")
    String otherReason;

    @Column(name = "paid", columnDefinition = "boolean default false")
    boolean paid = false;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @NotNull
    @Column(name = "customer_branch_id", nullable = false)
    Long customerBranchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;

    @Column(name = "ofd_application_id")
    Long ofdApplicationId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ofd_application_id", updatable = false, insertable = false)
    OfdApplication ofdApplication;

    @Column(name = "synced_smartpos", columnDefinition = "boolean default false")
    private boolean syncedSmartpos = false;
}
