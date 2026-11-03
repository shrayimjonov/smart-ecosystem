package uz.uzkassa.horeca.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1049024L;

    Long id;

    Long productId;

    BigDecimal qty;

    BigDecimal price;

    BigDecimal inKg;

    LocalDate createdDate;

    Long unitId;

    Long fromWarehouseId;

    Long toWarehouseId;

    List<String> marks;
}
