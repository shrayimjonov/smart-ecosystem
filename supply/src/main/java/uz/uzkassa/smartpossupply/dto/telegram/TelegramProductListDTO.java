package uz.uzkassa.smartpossupply.dto.telegram;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramProductListDTO extends CommonDTO {

    Vat vat;

    BigDecimal price;

    BigDecimal foreignPrice;

    CurrencyType currencyType;

    String photoUrl;
}
