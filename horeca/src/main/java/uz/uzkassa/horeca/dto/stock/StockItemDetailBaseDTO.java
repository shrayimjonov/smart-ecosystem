package uz.uzkassa.horeca.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class StockItemDetailBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34534543L;

    Long id;

    CommonDTO product;

    CommonDTO unit;

    CommonDTO warehouse;

    boolean hasMarking;

    BigDecimal qty;

    BigDecimal price;

    BigDecimal inKg;

    BigDecimal totalPrice;

    BigDecimal vatRate;

    BigDecimal totalVatPrice;
}
