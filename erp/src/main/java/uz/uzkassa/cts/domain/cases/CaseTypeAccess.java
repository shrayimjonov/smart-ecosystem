package uz.uzkassa.cts.domain.cases;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CaseType;

import java.io.Serial;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "case_type_access",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"case_type", "employee_id"})})
public class CaseTypeAccess extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 93401239L;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type", nullable = false)
    CaseType caseType;

    @Column(name = "employee_id", nullable = false)
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    User employee;
}
