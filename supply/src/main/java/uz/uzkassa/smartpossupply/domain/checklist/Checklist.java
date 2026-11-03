package uz.uzkassa.smartpossupply.domain.checklist;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.ChecklistStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "checklist",
    indexes = {
        @Index(columnList = "entity_id", name = "checklist_entity_id_idx"),
        @Index(columnList = "entity_type", name = "checklist_entity_type_idx"),
        @Index(columnList = "entity_id, entity_type", name = "checklist_entity_id_type_idx"),
        @Index(columnList = "agent_id", name = "checklist_agent_id_idx"),
        @Index(columnList = "creator_id", name = "checklist_creator_id_idx"),
        @Index(columnList = "deleted", name = "checklist_deleted_idx")
    }
)
@SQLDelete(sql = "UPDATE checklist SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class Checklist extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 893845934L;

    @Column(name = "name")
    String name;

    @Column(name = "checklist_date")
    LocalDateTime checklistDate;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    Long entityId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    EntityTypeEnum entityType;

    @Column(name = "supervisor_id")
    Long supervisorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", updatable = false, insertable = false)
    User supervisor;

    @Column(name = "agent_id")
    Long agentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", updatable = false, insertable = false)
    User agent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ChecklistStatus status = ChecklistStatus.CREATED;

    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("id")
    List<ChecklistItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("id")
    List<ChecklistCondition> conditions = new ArrayList<>();

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("id")
    List<ChecklistQuestionnaire> questions = new ArrayList<>();
}
