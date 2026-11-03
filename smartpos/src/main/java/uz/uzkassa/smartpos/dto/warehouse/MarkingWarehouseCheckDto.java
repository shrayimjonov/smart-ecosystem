package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingIncomeDetail}
 */
@Getter
@Setter
public class MarkingWarehouseCheckDto implements Serializable {
    @NotBlank
    private String marking;
    @NotBlank
    private String mxik;
}