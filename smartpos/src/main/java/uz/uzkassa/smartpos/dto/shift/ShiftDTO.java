package uz.uzkassa.smartpos.dto.shift;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.ShiftStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShiftDTO {
    private Long id;
    private Long number;
    private Long fiscalNumber;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private ShiftStatus status;
}
