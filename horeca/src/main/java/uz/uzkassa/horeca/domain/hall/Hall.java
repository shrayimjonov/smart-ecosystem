package uz.uzkassa.horeca.domain.hall;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.EntityTypeEnum;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "hall",
        indexes = {
                @Index(columnList = "branch_id", name = "hall_branch_id_idx"),
                @Index(columnList = "status", name = "hall_status_idx")
        })
@SQLDelete(sql = "UPDATE hall set deleted='true' WHERE  id=?")
public class Hall extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 345345L;

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

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    List<HallItem> items = new ArrayList<>();

    @Column(name = "catering_service")
    Integer cateringService;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    List<HallEmployee> employees = new ArrayList<>();

    @Column(name = "fee_percent", scale = 2, precision = 25)
    BigDecimal feePercent;

    public void addEmployee(HallEmployee hallEmployee) {
        hallEmployee.setHall(this);
        this.employees.add(hallEmployee);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Hall)) {
            return false;
        }
        return id != null && id.equals(((Hall) o).id);
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.HALL;
    }
}
