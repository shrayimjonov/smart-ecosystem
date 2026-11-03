package uz.uzkassa.smartpossupply.domain.supervisour;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "supervisor_employees")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupervisorEmployee extends AbstractAuditingEntity {

    @Column(name = "supervisor_id", nullable = false)
    Long supervisorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", insertable = false, updatable = false)
    User supervisor;

    @Column(name = "employee_id", nullable = false)
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    User employee;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SupervisorEmployee that = (SupervisorEmployee) o;
        return Objects.equals(supervisorId, that.supervisorId) && Objects.equals(employeeId, that.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), supervisorId, employeeId);
    }
}