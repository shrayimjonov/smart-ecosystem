package uz.uzkassa.smartpossupply.domain.checklist;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;

import java.io.Serial;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "checklist_questionnaire",
        indexes = {
                @Index(columnList = "checklist_id", name = "checklist_questionnaire_checklist_id_idx"),
                @Index(columnList = "deleted", name = "checklist_questionnaire_deleted_idx")
        }
)
@SQLDelete(sql = "UPDATE checklist_questionnaire SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class ChecklistQuestionnaire extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 898492423L;

    @NotNull
    @Column(name = "checklist_id", nullable = false)
    Long checklistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", updatable = false, insertable = false)
    Checklist checklist;

    @NotNull
    @Column(name = "question", nullable = false)
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String question;

    @Column(name = "answer")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String answer;

    @Override
    public String getName() {
        return question;
    }
}
