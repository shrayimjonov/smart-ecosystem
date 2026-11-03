package uz.uzkassa.cts.domain.approval.process;

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
import uz.uzkassa.cts.domain.Approver;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ApproverStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serial;

@Entity
@Table(name = "approval_process_history",
        indexes = {
                @Index(columnList = "entity_id", name = "approval_process_history_entity_idx"),
                @Index(columnList = "entity_type", name = "approval_process_history_entity_type_idx"),
                @Index(columnList = "entity_id, entity_type", name = "approval_process_history_entity_id_entity_type_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE approval_process_history SET deleted = 'true' WHERE id=?")
public class ApprovalProcessHistory extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 23443447543L;

    @Column(name = "entity_id")
    Long entityId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @Column(name = "approver_id")
    Long approverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", updatable = false, insertable = false)
    User approver;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ApproverStatus status;
}
