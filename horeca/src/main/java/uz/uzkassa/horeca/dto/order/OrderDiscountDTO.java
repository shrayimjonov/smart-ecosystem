package uz.uzkassa.horeca.dto.order;

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
public class OrderDiscountDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 938459834L;

    BigDecimal amount;

    BigDecimal percent;
}
