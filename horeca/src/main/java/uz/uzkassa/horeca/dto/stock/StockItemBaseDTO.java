package uz.uzkassa.horeca.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class StockItemBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34534543L;

    Long id;

    Long productId;

    Long unitId;

    Long warehouseId;

    BigDecimal qty;

    BigDecimal price;

    BigDecimal inKg;

    BigDecimal totalPrice;

    BigDecimal vatRate;

    List<String> markCodes;

    List<String> aggregationCodes;

    List<String> blockCodes;
}
