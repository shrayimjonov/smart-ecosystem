package uz.uzkassa.smartpos.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftFilter extends BaseFilter {
    private Long branchId;
    private Long shiftNumber;
}
