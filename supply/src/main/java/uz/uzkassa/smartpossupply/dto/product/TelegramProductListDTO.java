package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramProductListDTO extends CommonDTO {

    BigDecimal qty;

    BigDecimal unitPrice;

    BigDecimal totalPrice;
}
