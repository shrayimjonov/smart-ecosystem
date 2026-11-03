package uz.uzkassa.cts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.approval.process.ApprovalProcessStep;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ApproverStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serial;

@Entity
@Table(name = "approver",
        indexes = {
                @Index(columnList = "entity_id", name = "approver_entity_id_idx"),
                @Index(columnList = "entity_type", name = "approver_entity_type_idx"),
                @Index(columnList = "employee_id", name = "approver_employee_idx"),
                @Index(columnList = "step", name = "approver_step_idx"),
                @Index(columnList = "entity_id, entity_type", name = "approver_entity_id_entity_type_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE approver SET deleted = 'true' WHERE id=?")
public class Approver extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 141125840L;

    @Column(name = "entity_id")
    Long entityId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @Column(name = "employee_id")
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    User employee;

    @Column(name = "step")
    Integer step;

    @Column(name = "approval_process_step_id")
    Long approvalProcessStepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_process_step_id", updatable = false, insertable = false)
    ApprovalProcessStep approvalProcessStep;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ApproverStatus status = ApproverStatus.WAITING_FOR_APPROVAL;

    @Column(name = "current")
    boolean current = false;
}
