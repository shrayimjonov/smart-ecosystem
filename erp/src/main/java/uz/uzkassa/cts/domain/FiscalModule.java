package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
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
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "fiscal_module",
        indexes = {
                @Index(columnList = "serial_number", name = "fiscal_module_serial_number_idx"),
                @Index(columnList = "branch_id", name = "fiscal_module_branch_id_idx"),
                @Index(columnList = "customer_id", name = "fiscal_module_customer_id_idx"),
                @Index(columnList = "customer_branch_id", name = "fiscal_module_customer_branch_id_idx"),
                @Index(columnList = "status", name = "fiscal_module_status_idx"),
                @Index(columnList = "company_id", name = "fiscal_module_company_id_idx")
        })
@Getter
@Setter
@SQLDelete(sql = "UPDATE fiscal_module SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FiscalModule extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 6711479173L;

    @NotNull
    @Column(name = "serial_number", unique = true)
    String serialNumber;

    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price;

    @Column(name = "branch_id")
    Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "customer_id")
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @Column(name = "customer_branch_id")
    Long customerBranchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    DeviceStatus status = DeviceStatus.NOT_REGISTERED; //Status can be NOT_REGISTERED, REGISTERED, or UNREGISTERED

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Override
    public String getName() {
        return getSerialNumber();
    }

    public SerialNumberDTO toSerialNumberDTO() {
        return new SerialNumberDTO(getId(), getSerialNumber());
    }
}
