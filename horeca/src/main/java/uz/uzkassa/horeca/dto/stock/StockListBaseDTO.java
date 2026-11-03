package uz.uzkassa.horeca.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.StockStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class StockListBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 645645L;

    Long id;

    String stockNumber;

    LocalDateTime stockDate;

    CommonDTO department;

    CommonDTO warehouse;

    StockStatus status;

    BigDecimal totalPrice;

    BigDecimal totalQty;

    BigDecimal totalVatPrice;
}
