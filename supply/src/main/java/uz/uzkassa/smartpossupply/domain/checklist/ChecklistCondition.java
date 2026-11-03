package uz.uzkassa.smartpossupply.domain.checklist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "checklist_condition")
@SQLDelete(sql = "UPDATE checklist_condition SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class ChecklistCondition extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 56757L;

    @Column(name = "checklist_id", nullable = false)
    Long checklistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", updatable = false, insertable = false)
    Checklist checklist;

    @Column(name = "condition_id", nullable = false)
    Long conditionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_id", updatable = false, insertable = false)
    Condition condition;

    @Override
    public String getName() {
        return condition.getName();
    }
}
