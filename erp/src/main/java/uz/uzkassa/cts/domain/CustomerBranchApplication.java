package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.customerbranchapplication.base.CustomerBranchApplicationFieldDTO;
import uz.uzkassa.cts.enums.CustomerBranchApplicationStatus;

import java.io.Serial;
import java.util.List;

@Entity
@Table(name = "customer_branch_application",
        indexes = {
                @Index(columnList = "customer_branch_id", name = "customer_branch_application_cb_id_idx"),
                @Index(columnList = "manager_id", name = "customer_branch_application_manager_id_idx"),
                @Index(columnList = "status", name = "customer_branch_application_status_idx")
        })
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@SQLDelete(sql = "UPDATE customer_branch_application SET deleted = 'true' WHERE id=?")
public class CustomerBranchApplication extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 9584367682L;

    @NotNull
    @Column(name = "customer_branch_id", nullable = false)
    Long customerBranchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;

    @NotNull
    @Column(name = "manager_id", nullable = false)
    Long managerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", updatable = false, insertable = false)
    User manager;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CustomerBranchApplicationStatus status = CustomerBranchApplicationStatus.PENDING;

    @Column(name = "fields")
    @JdbcTypeCode(SqlTypes.JSON)
    List<CustomerBranchApplicationFieldDTO> fields;

    @Column(name = "reason_id")
    Long reasonId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id", updatable = false, insertable = false)
    Reference reason;
}
