package uz.uzkassa.smartpossupply.dto.telegram;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramTopProductListDTO extends CommonDTO implements Serializable {

    Vat vat;

    BigDecimal price;

    BigDecimal foreignPrice;

    CurrencyType currencyType;

    String photoUrl;

    public TelegramTopProductListDTO(Long id, String name, String vat, BigDecimal price, BigDecimal foreignPrice, String currencyType, String photoUrl) {
        super(id, name);
        this.vat = vat != null ? Vat.valueOf(vat) : null;
        this.price = price;
        this.foreignPrice = foreignPrice;
        this.currencyType = currencyType != null ? CurrencyType.valueOf(currencyType) : null;
        this.photoUrl = photoUrl;
    }
}
