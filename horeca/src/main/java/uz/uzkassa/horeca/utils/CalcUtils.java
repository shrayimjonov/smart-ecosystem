package uz.uzkassa.horeca.utils;

import uz.uzkassa.horeca.enums.Vat;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;

public final class CalcUtils {

    public static final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public static BigDecimal calcVat(BigDecimal price, BigDecimal vatRate) {
        if (price == null || vatRate == null) {
            return null;
        }
        if (vatRate.compareTo(Vat.getDefaultVatRate()) == 0) {
            return price.multiply(Vat.getDefaultVatRate().divide((Vat.getDefaultVatRate().add(BigDecimal.valueOf(100))), MathContext.DECIMAL64));
        }
        return null;
    }

    public static BigDecimal calcCateringServiceAmount(BigDecimal feePercent, BigDecimal totalPrice) {
        if (totalPrice == null || feePercent == null) {
            return null;
        }
        if (feePercent.compareTo(BigDecimal.ZERO) == 0 || totalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return totalPrice.divide(BigDecimal.valueOf(100), MathContext.DECIMAL64).multiply(feePercent, MathContext.DECIMAL64);
    }
}
