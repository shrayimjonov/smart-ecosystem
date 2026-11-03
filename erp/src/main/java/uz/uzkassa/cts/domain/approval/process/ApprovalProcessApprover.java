package uz.uzkassa.cts.domain.approval.process;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.ApproveType;
import uz.uzkassa.cts.enums.Role;

import java.io.Serial;

@Entity
@Table(name = "approval_process_approver",
        indexes = {
                @Index(columnList = "approval_process_step_id", name = "approval_process_approver_step_id_idx"),
                @Index(columnList = "type", name = "approval_process_approver_type_idx"),
                @Index(columnList = "role", name = "approval_process_approver_role_idx"),
                @Index(columnList = "employee_id", name = "approval_process_approver_employee_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApprovalProcessApprover extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 14593447543L;

    @Column(name = "approval_process_step_id")
    Long approvalProcessStepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_process_step_id", updatable = false, insertable = false)
    ApprovalProcessStep approvalProcessStep;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    ApproveType type = ApproveType.ROLE;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    Role role;

    @Column(name = "employee_id")
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    User employee;

    @Column(name = "approve_for_all", columnDefinition = "boolean default false")
    boolean approveForAll = false;
}
