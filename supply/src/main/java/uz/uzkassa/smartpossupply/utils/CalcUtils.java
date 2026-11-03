package uz.uzkassa.smartpossupply.utils;

import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CalcUtils {

    public static BigDecimal getPriceWithoutVat(BigDecimal price, Vat vat) {
        if (vat == null) {
            return price;
        }
        return price.multiply(new BigDecimal("100")).divide(BigDecimal.valueOf(100).add(vat.getAmount()), 2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal getPriceWithoutVat(BigDecimal price, BigDecimal vatRate) {
        if (vatRate == null) {
            return price;
        }
        return price.multiply(new BigDecimal("100")).divide(BigDecimal.valueOf(100).add(vatRate), 2, RoundingMode.HALF_EVEN);
    }
}
