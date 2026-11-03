package uz.uzkassa.smartpossupply.dto.shift;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.12.2022 17:01
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftDTO {

    Long id;

    Long branchId;

    Long cashierId;

    Long fiscalNumber;
}
