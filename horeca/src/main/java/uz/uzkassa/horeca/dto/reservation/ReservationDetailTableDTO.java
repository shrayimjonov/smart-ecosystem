package uz.uzkassa.horeca.dto.reservation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.ReservationStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationDetailTableDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 856849849834L;

    Long id;

    String reservationNumber;

    Long numberOfGuest;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    CustomerDetailDTO client;

    CommonDTO order;

    ReservationStatus status;

    String guessName;

    LocalDateTime lastModifiedDate;

    CommonDTO creator;
}
