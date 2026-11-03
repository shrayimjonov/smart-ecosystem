package uz.uzkassa.horeca.domain.shift;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.ShiftStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.12.2022 16:20
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "shift")
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Shift extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 384938L;

    @Column(name = "end_date")
    LocalDateTime endDate;

    @Column(name = "increment_number")
    Long incrementNumber;

    @NotNull
    @Column(name = "shift_number")
    String shiftNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ShiftStatus status = ShiftStatus.OPEN;

    @NotNull
    @Column(name = "user_id")
    Long userId;

    @ManyToOne(cascade = PERSIST, fetch = LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;

    @NotNull
    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(cascade = PERSIST, fetch = LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    public String getName() {
        return this.getShiftNumber().toString();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shift)) {
            return false;
        }
        return id != null && id.equals(((Shift) o).id);
    }

}
