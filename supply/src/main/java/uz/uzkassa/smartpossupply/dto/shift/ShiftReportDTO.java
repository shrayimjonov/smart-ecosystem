package uz.uzkassa.smartpossupply.dto.shift;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.12.2022 16:32
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftReportDTO extends ShiftReportBaseTotalDTO {

    Long shiftId;

    Long cashierId;

    Long fiscalNumber;

    LocalDateTime startDate;

    LocalDateTime endDate;
}
