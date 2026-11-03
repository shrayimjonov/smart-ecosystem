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
public class ReservationDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34989345L;

    Long id;

    String reservationNumber;

    MobileTableDetailWithMergedTablesDTO table;

    Long numberOfGuest;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    CustomerDetailDTO client;

    String guestName;

    CommonDTO order;

    String comment;

    LocalDateTime lastModifiedDate;

    ReservationStatus status;

    CommonDTO creator;
}
