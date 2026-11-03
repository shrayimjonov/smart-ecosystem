package uz.uzkassa.horeca.dto.reservation.mobile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.reservation.CustomerDetailDTO;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.02.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileReservationDetailTableDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8569834L;

    Long id;

    String reservationNumber;

    Long numberOfGuest;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    CustomerDetailDTO client;

    CommonDTO order;

    CommonDTO waiter;

    String comment;
}
