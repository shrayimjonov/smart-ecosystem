package uz.uzkassa.horeca.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class StockDetailBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 234554654L;

    Long id;

    String stockNumber;

    LocalDateTime stockDate;

    CommonDTO branch;

    CommonDTO department;

    CommonDTO warehouse;

    String description;

    StockStatus status;

    BigDecimal totalPrice;

    BigDecimal totalQty;

    BigDecimal totalVatPrice;
}
