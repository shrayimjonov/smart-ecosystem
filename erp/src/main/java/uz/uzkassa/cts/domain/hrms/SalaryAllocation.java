package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "salary_allocation",
        indexes = {
                @Index(columnList = "department_id", name = "salary_allocation_department_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE salary_allocation SET deleted = 'true' WHERE id=?")
public class SalaryAllocation extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 347827834L;

    @Column(name = "department_id", nullable = false)
    Long departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", updatable = false, insertable = false)
    Department department;

    @Column(name = "min_salary_total_amount", precision = 25, scale = 2)
    BigDecimal minSalaryTotalAmount;

    @Column(name = "max_salary_total_amount", precision = 25, scale = 2)
    BigDecimal maxSalaryTotalAmount;

    @Column(name = "min_employee_total_count")
    Integer minEmployeeTotalCount;

    @Column(name = "max_employee_total_count")
    Integer maxEmployeeTotalCount;

    @OneToMany(mappedBy = "salaryAllocation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    List<SalaryAllocationItem> items = new ArrayList<>();

    public void addItem(SalaryAllocationItem salaryAllocationItem) {
        salaryAllocationItem.setSalaryAllocation(this);
        salaryAllocationItem.setSalaryAllocationId(this.id);
        items.add(salaryAllocationItem);
    }

}
