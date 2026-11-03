package uz.uzkassa.horeca.dto.reservation;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.HallItemStatus;
import uz.uzkassa.horeca.enums.ReservationStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34454656L;

    Long id;

    String reservationNumber;

    CustomerDetailDTO client;

    CommonDTO table;

    HallItemStatus tableStatus;

    CommonDTO hall;

    Long numberOfGuest;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    CommonDTO order;

    ReservationStatus status;
}
