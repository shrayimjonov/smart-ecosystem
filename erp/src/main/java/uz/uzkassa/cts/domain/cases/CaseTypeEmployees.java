package uz.uzkassa.cts.domain.cases;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.CaseType;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "case_type_employees",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"case_type", "employee_id"})})
public class CaseTypeEmployees extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 93878764577L;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type", nullable = false)
    CaseType caseType;

    @Column(name = "employee_id")
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    User employee;
}
