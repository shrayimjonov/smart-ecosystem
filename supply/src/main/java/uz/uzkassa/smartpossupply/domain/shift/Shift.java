package uz.uzkassa.smartpossupply.domain.shift;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.ShiftStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;

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
public class Shift extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 384938L;

    @Column(name = "end_date")
    LocalDateTime endDate;

    @Column(name = "shift_number")
    Long shiftNumber;

    @Column(name = "fiscal_number")
    Long fiscalNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ShiftStatus status = ShiftStatus.OPEN;

    @Column(name = "cashier_id")
    Long cashierId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cashier_id", updatable = false, insertable = false)
    User cashier;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @Override
    public String getName() {
        return this.getShiftNumber().toString();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.SHIFT;
    }

    @Override
    public String getEntityName() {
        return getName();
    }
}
