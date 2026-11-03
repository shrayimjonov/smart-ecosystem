package uz.uzkassa.cts.domain.approval.process;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.BaseEntity;
import uz.uzkassa.cts.enums.ApproveAction;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "approval_process_step",
        indexes = {
                @Index(columnList = "approval_process_id", name = "approval_process_step_approval_process_id_idx"),
                @Index(columnList = "step", name = "approval_process_step_step_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE approval_process SET deleted = 'true' WHERE id=?")
public class ApprovalProcessStep extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 23443447543L;

    @NotNull
    @Column(name = "approval_process_id", nullable = false)
    Long approvalProcessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_process_id", updatable = false, insertable = false)
    ApprovalProcess approvalProcess;

    @Column(name = "step", columnDefinition = "integer default 1")
    Integer step = 1;

    @Column(name = "approve_action")
    @Enumerated(EnumType.STRING)
    ApproveAction approveAction;

    @Column(name = "reject_action")
    @Enumerated(EnumType.STRING)
    ApproveAction rejectAction;

    @OneToMany(mappedBy = "approvalProcessStep", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ApprovalProcessApprover> approvalProcessApprovers = new ArrayList<>();
}
