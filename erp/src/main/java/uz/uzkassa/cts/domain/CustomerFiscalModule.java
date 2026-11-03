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
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serial;

@Entity
@Table(name = "customer_fiscal_module",
        indexes = {
                @Index(columnList = "fiscal_module_id", name = "customer_fiscal_module_fm_id_idx"),
                @Index(columnList = "customer_id", name = "customer_fiscal_module_customer_id_idx"),
                @Index(columnList = "status", name = "customer_fiscal_module_status_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE customer_fiscal_module SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class CustomerFiscalModule extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 8321875307L;

    @NotNull
    @Column(name = "fiscal_module_id", nullable = false)
    Long fiscalModuleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiscal_module_id", updatable = false, insertable = false)
    FiscalModule fiscalModule;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    DeviceStatus status = DeviceStatus.REGISTERED; // Status can be TRANSFERRED, FISCAL or DELETED
}
