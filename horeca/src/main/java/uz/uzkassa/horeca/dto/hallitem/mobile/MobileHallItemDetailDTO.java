package uz.uzkassa.horeca.dto.hallitem.mobile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderDetailMobileDTO;
import uz.uzkassa.horeca.dto.reservation.mobile.MobileReservationDetailDTO;
import uz.uzkassa.horeca.enums.HallItemStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class MobileHallItemDetailDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7543318961L;

    String code;

    Long capacity;

    CommonDTO hall;

    HallItemStatus status;

    List<CommonCodeDTO> mergedTables;

    CommonDTO mergedTable;

    Long numberOfGuest;

    List<MobileReservationDetailDTO> reservations = new ArrayList<>();

    List<OrderDetailMobileDTO> orders = new ArrayList<>();

    @JsonIgnore
    public void addReservation(MobileReservationDetailDTO reservation) {
        reservations.add(reservation);
    }

    @JsonIgnore
    public void addOrder(OrderDetailMobileDTO order) {
        orders.add(order);
    }
}
