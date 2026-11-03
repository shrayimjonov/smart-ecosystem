package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serial;

@Entity
@Table(name = "customer_branch_fiscal_module",
        indexes = {
                @Index(columnList = "fiscal_module_Id", name = "customer_branch_fiscal_module_fm_id_idx"),
                @Index(columnList = "customer_branch_id", name = "customer_branch_fiscal_module_cb_id_idx"),
                @Index(columnList = "status", name = "customer_branch_fiscal_module_status_idx")
        })
@Getter
@Setter
@SQLDelete(sql = "UPDATE customer_branch_fiscal_module SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class CustomerBranchFiscalModule extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 9043071086L;

    @NotNull
    @Column(name = "fiscal_module_Id", nullable = false)
    private Long fiscalModuleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiscal_module_Id", updatable = false, insertable = false)
    private FiscalModule fiscalModule;

    @NotNull
    @Column(name = "customer_branch_id", nullable = false)
    private Long customerBranchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    private CustomerBranch customerBranch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeviceStatus status = DeviceStatus.REGISTERED; // Status can be REGISTERED, FISCAL, FISCAL_ACTIVE or DELETED
}
