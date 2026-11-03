package uz.uzkassa.horeca.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.DepartmentType;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "department",
        indexes = {
                @Index(columnList = "name", name = "department_name_idx"),
                @Index(columnList = "branch_id", name = "department_branch_id_idx"),
                @Index(columnList = "status", name = "department_status_idx"),
                @Index(columnList = "type", name = "department_type_idx")
        })
@SQLDelete(sql = "UPDATE department set deleted='false' WHERE id=?")
public class Department extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 8264L;

    @NotNull
    @Column(name = "unique_id", nullable = false)
    private UUID uniqueId;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @NotNull
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    DepartmentType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Department)) {
            return false;
        }
        return id != null && id.equals(((Department) o).id);
    }
}
