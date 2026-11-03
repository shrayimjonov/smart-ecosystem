package uz.uzkassa.smartpos.dto.warehouse;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.MarkingWarehouseStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingIncomeDetail}
 */
@Getter
@Setter
public class IncomeMarkDto implements Serializable {
    private String marking;
    private String printCode;
    private LocalDateTime receivedAt;
    private MarkingWarehouseStatus status;
}