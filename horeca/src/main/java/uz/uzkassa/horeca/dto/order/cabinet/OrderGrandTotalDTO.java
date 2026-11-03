package uz.uzkassa.horeca.dto.order.cabinet;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public class OrderGrandTotalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4534989L;

    BigDecimal totalQty;

    BigDecimal totalPrice;

    BigDecimal totalVat;

    BigDecimal totalPaid;

    BigDecimal feePercent;

    BigDecimal feeAmount;

    Integer itemsCount;
}
