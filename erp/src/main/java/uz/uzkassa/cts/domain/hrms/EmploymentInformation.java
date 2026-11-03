package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.EmploymentType;

import java.io.Serial;
import java.time.LocalDate;


@Entity
@Table(name = "employment_information", indexes = {
        @Index(columnList = "employee_id", name = "employment_information_employee_id_idx")
}
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE employment_information SET deleted = 'true' WHERE id=?")
public class EmploymentInformation extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 56568945L;

    @Column(name = "employee_id", nullable = false, unique = true)
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    User employee;

    @Column(name = "employee_number")
    String employeeNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "grade_id")
    Long gradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", updatable = false, insertable = false)
    Reference grade;

    @Column(name = "supervisor_id")
    Long supervisorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", updatable = false, insertable = false)
    User supervisor;

    @Column(name = "hire_date")
    LocalDate hireDate;

    @Column(name = "resignation_date")
    LocalDate resignationDate;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    EmploymentType type;

}
