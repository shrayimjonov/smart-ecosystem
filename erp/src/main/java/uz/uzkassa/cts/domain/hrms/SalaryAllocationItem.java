package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;
import java.math.BigDecimal;


@Entity
@Table(name = "salary_allocation_item",
        indexes = {
                @Index(columnList = "salary_allocation_id", name = "salary_allocation_item_salary_allocation_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryAllocationItem extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 24327827834L;

    @Column(name = "salary_allocation_id", nullable = false)
    Long salaryAllocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_allocation_id", updatable = false, insertable = false)
    SalaryAllocation salaryAllocation;

    @Column(name = "position_id", nullable = false)
    Long positionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", updatable = false, insertable = false)
    Position position;

    @Column(name = "grade_id", nullable = false)
    Long gradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", updatable = false, insertable = false)
    Reference grade;

    @Column(name = "min_salary_amount", precision = 25, scale = 2, nullable = false)
    BigDecimal minSalaryAmount;

    @Column(name = "max_salary_amount", precision = 25, scale = 2, nullable = false)
    BigDecimal maxSalaryAmount;

    @Column(name = "min_employee_count")
    Integer minEmployeeCount;

    @Column(name = "max_employee_count")
    Integer maxEmployeeCount;

}
