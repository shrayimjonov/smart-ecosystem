package uz.uzkassa.horeca.domain.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.Department;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "warehouse",
        indexes = {
                @Index(columnList = "name", name = "warehouse_name_idx"),
                @Index(columnList = "status", name = "warehouse_status_idx"),
                @Index(columnList = "department_id", name = "warehouse_department_id_idx"),
                @Index(columnList = "manager_id", name = "warehouse_manager_id_idx"),
                @Index(columnList = "branch_id", name = "warehouse_branch_id_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE warehouse SET deleted='true' WHERE id=?")
public class Warehouse extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 33411553L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "department_id")
    Long departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", updatable = false, insertable = false)
    Department department;

    @Column(name = "manager_id")
    Long managerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", updatable = false, insertable = false)
    User manager;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Warehouse)) {
            return false;
        }
        return id != null && id.equals(((Warehouse) o).id);
    }
}
