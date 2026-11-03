package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.enums.CaseStatus;

import java.io.Serial;


@Getter
@Setter
@Table(name = "cases_status_log")
@Entity
@SQLDelete(sql = "UPDATE cases_status_log SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class CaseStatusLog extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 201001345480L;

    @Column(name = "case_id")
    Long caseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", updatable = false, insertable = false)
    Cases cases;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CaseStatus status;
}
