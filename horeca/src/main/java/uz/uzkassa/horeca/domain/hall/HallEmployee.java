package uz.uzkassa.horeca.domain.hall;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.enums.EntityTypeEnum;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "hall_employee",
        indexes = {
                @Index(columnList = "hall_id", name = "hall_employee_hall_id_idx"),
                @Index(columnList = "employee_id", name = "hall_employee_employee_id_idx")
        })
@SQLDelete(sql = "UPDATE hall_employee SET deleted='true' WHERE id=?")
public class HallEmployee extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 345345L;

    @NotNull
    @Column(name = "employee_id", nullable = false)
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    User employee;

    @NotNull
    @Column(name = "hall_id", nullable = false)
    Long hallId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", insertable = false, updatable = false)
    Hall hall;

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.HALL_EMPLOYEE;
    }

}
