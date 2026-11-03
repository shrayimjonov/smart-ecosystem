package uz.uzkassa.horeca.dto.order.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemCabinetDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 834598349L;

    Long id;

    Long menuItemId;

    Long multiUnitId;

    BigDecimal price;

    BigDecimal qty;

    BigDecimal totalPrice;

    String comment;

    Long orderId;

    LocalDateTime lastModifiedDate;

    Set<String> marks;
}
