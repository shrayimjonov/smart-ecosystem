package uz.uzkassa.horeca.dto.hallitem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationDetailTableDTO;
import uz.uzkassa.horeca.enums.HallItemStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class HallItemTableDetailDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 754331L;

    Long capacity;

    CommonDTO hall;

    HallItemStatus status;

    boolean withOrder;

    Long numberOfGuest;

    LocalDateTime lastModifiedDate;

    List<ReservationDetailTableDTO> reservations = new ArrayList<>();

    List<CommonDTO> orders = new ArrayList<>();

    BigDecimal feePercent;

    CommonDTO mergedTable;

    @JsonIgnore
    public void addReservation(ReservationDetailTableDTO reservation) {
        reservations.add(reservation);
    }

    @JsonIgnore
    public void addOrder(CommonDTO order) {
        orders.add(order);
    }
}
