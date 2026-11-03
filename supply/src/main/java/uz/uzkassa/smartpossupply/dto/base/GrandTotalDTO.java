package uz.uzkassa.smartpossupply.dto.base;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.offlineorder.BaseOfflineOrderDTO;
import uz.uzkassa.smartpossupply.enums.CurrencyType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public class GrandTotalDTO implements Serializable {

    BigDecimal totalQty;

    BigDecimal totalPrice;

    BigDecimal totalForeignPrice;

    BigDecimal exchangeRate;

    CurrencyType currencyType;

    BigDecimal totalPriceWithoutVat;

    BigDecimal totalVatPrice;

    BigDecimal totalDiscountPrice;
}
