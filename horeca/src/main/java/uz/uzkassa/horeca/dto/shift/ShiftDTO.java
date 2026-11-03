package uz.uzkassa.horeca.dto.shift;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.11.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftDTO {

    Long id;

    Long branchId;

    Long userId;

    String shiftNumber;

    public ShiftDTO(Long branchId) {
        this.branchId = branchId;
    }
}
