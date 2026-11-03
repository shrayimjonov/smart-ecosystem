package uz.uzkassa.horeca.dto.reservation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.OrderReason;
import uz.uzkassa.horeca.enums.ReservationStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.03.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationUpdateStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4231489761L;

    ReservationStatus status;

    OrderReason reason;

    String otherReason;
}
