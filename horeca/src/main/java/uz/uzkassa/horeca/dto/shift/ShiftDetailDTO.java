package uz.uzkassa.horeca.dto.shift;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.ShiftStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.11.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftDetailDTO {

    Long id;

    String shiftNumber;

    LocalDateTime startDate;

    ShiftStatus status;
}
