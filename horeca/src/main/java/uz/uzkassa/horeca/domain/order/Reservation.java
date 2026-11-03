package uz.uzkassa.horeca.domain.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.hall.HallItem;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.OrderReason;
import uz.uzkassa.horeca.enums.ReservationStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "reservation",
        indexes = {
                @Index(columnList = "table_id", name = "reservation_table_id_idx"),
                @Index(columnList = "branch_id", name = "reservation_branch_id_idx"),
                @Index(columnList = "client_id", name = "reservation_client_id_idx"),
                @Index(columnList = "order_id", name = "reservation_order_id_idx"),
                @Index(columnList = "status", name = "reservation_status_idx")
        })
@SQLDelete(sql = "UPDATE reservation SET deleted='true' WHERE id=?")
public class Reservation extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 83948398L;

    @Column(name = "reservation_number")
    String reservationNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "comment")
    String comment;

    @Column(name = "table_id")
    Long tableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", insertable = false, updatable = false)
    HallItem table;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @Column(name = "guest_name")
    String guestName;

    @Column(name = "number_of_guest")
    Long numberOfGuest;

    @Column(name = "fromDate")
    LocalDateTime fromDate;

    @Column(name = "toDate")
    LocalDateTime toDate;

    @Column(name = "client_id")
    Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    User client;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ReservationStatus status = ReservationStatus.NEW;

    @Column(name = "reason")
    @Enumerated(EnumType.STRING)
    OrderReason reason;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "other_reason")
    String otherReason;

    @Column(name = "order_id")
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    Order order;

    @Override
    public String getName() {
        return this.getReservationNumber();
    }

    public CommonDTO clientCommonDTO() {
        if (getClientId() != null) {
            return new CommonDTO(getClientId(), Optional.ofNullable(getClient().getGuestName()).orElse(getClient().getName()));
        }
        return null;
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.RESERVATION;
    }
}
