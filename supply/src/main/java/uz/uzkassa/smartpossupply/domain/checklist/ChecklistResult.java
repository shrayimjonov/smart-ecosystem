package uz.uzkassa.smartpossupply.domain.checklist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;

import java.io.Serial;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "checklist_result")
public class ChecklistResult extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 546757454L;

    @NotNull
    @Column(name = "checklist_condition_id", nullable = false)
    Long checklistConditionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_condition_id", updatable = false, insertable = false)
    ChecklistCondition checklistCondition;

    @NotNull
    @Column(name = "checklist_item_id", nullable = false)
    Long checklistItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_item_id", updatable = false, insertable = false)
    ChecklistItem checklistItem;

    @Column(name = "checked")
    Boolean checked;
}
