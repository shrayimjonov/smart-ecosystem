package uz.uzkassa.horeca.dto.warehouse;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseCheckResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 98329843L;

    BigDecimal price;

    BigDecimal inKg;

    BigDecimal qty;

    LocalDate createdDate;
}
