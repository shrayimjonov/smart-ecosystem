package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.BaseEntity;

import java.io.Serial;


@Entity
@Getter
@Setter
@Table(name = "leave_allowance",
        indexes = {
                @Index(columnList = "employee_id", name = "leave_allowance_employee_id_idx"),
                @Index(columnList = "year", name = "leave_allowance_year_idx"),
                @Index(columnList = "employee_id, year", name = "leave_allowance_employee_id_year_idx")
        })
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE leave_allowance SET deleted='true' WHERE id=?")
public class LeaveAllowance extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 3456788L;

    @Column(name = "employee_id", nullable = false)
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    User employee;

    @Column(name = "total_allowance")
    Integer totalAllowance;

    @Column(name = "remaining_allowance")
    Integer remainingAllowance;

    @Column(name = "year")
    Integer year;
}

