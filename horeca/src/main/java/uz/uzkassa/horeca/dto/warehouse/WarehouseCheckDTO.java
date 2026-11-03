package uz.uzkassa.horeca.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseCheckDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 98329843L;

    @NotNull
    Long warehouseId;

    @NotNull
    Long productId;

    @NotNull
    Long unitId;
}
