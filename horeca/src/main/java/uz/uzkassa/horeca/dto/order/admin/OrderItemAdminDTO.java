package uz.uzkassa.horeca.dto.order.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemAdminDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 834598349L;

    Long id;

    Long menuItemId;

    Long unitId;

    BigDecimal qty;

    BigDecimal price;

    BigDecimal totalPrice;

    String comment;

    Long orderId;
}
