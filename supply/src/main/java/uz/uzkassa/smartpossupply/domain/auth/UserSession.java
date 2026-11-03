package uz.uzkassa.smartpossupply.domain.auth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.shift.Shift;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.ShiftStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.12.2022 17:59
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_session")
public class UserSession extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 438494L;

    @Column(name = "user_id")
    Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;

    @Column(name = "shift_id")
    Long shiftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", updatable = false, insertable = false)
    Shift shift;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ShiftStatus status;

    @Column(name = "end_date")
    LocalDateTime endDate;

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.USER_SESSION;
    }

    @Override
    public String getEntityName() {
        return this.user.getName();
    }
}
