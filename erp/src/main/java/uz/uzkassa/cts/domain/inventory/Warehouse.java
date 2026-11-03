package uz.uzkassa.cts.domain.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.domain.hrms.Department;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serial;

@Entity
@Table(name = "warehouse", indexes = {
        @Index(columnList = "name", name = "warehouse_name_idx"),
        @Index(columnList = "branch_id", name = "warehouse_branch_id_idx"),
        @Index(columnList = "manager_id", name = "warehouse_manager_id_idx"),
        @Index(columnList = "region_id", name = "warehouse_region_id_idx"),
        @Index(columnList = "district_id", name = "warehouse_district_id_idx"),
        @Index(columnList = "neighborhood_id", name = "warehouse_neighborhood_id_idx"),
        @Index(columnList = "department_id", name = "warehouse_department_id_idx"),
        @Index(columnList = "status", name = "warehouse_status_idx")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE warehouse SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Warehouse extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 5003873412L;

    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @NotNull
    @Column(name = "manager_id", nullable = false)
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

    @Column(name = "department_id")
    Long departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", updatable = false, insertable = false)
    Department department;

    @Embedded
    BaseAddress address;
}
