package uz.uzkassa.cts.domain.cases;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CaseMessageType;

import java.io.Serial;

@Entity
@Table(name = "case_note",
        indexes = {
                @Index(columnList = "case_id", name = "case_note_case_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE case_note SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class CaseNote extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 9899339L;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "message")
    String message;

    @Column(name = "case_id")
    Long caseId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", updatable = false, insertable = false)
    Cases cases;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    CaseMessageType messageType;
}
