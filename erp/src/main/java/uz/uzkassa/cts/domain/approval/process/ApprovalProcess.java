package uz.uzkassa.cts.domain.approval.process;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "approval_process",
        indexes = {
                @Index(columnList = "company_id", name = "approval_process_company_id_idx"),
                @Index(columnList = "entity_type", name = "approval_process_entity_type_idx"),
                @Index(columnList = "company_id, entity_type", name = "approval_process_company_id_entity_type_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE approval_process SET deleted = 'true' WHERE id=?")
public class ApprovalProcess extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1093447543L;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @OneToMany(mappedBy = "approvalProcess", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ApprovalProcessStep> steps = new ArrayList<>();
}
