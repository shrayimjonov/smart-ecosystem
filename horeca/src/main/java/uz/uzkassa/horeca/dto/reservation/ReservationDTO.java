package uz.uzkassa.horeca.dto.reservation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4231L;

    String phone;

    Long tableId;

    Long numberOfGuest;

    String guestName;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    String comment;
}
