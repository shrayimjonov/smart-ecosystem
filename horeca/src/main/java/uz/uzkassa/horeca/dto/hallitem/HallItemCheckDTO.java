package uz.uzkassa.horeca.dto.hallitem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 27.02.2024
 */
@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class HallItemCheckDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 335248498554L;

    Long numberOfGuests;

    LocalDateTime reservationStartDate;

    LocalDateTime reservationEndDate;
}
