package uz.uzkassa.horeca.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class StockBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 83983L;

    Long id;

    Long branchId;

    String description;

    Long warehouseId;

    LocalDateTime stockDate;
}
