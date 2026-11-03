package uz.uzkassa.smartpos.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class FinanceUtils {
    public static BigDecimal calculateVat(BigDecimal totalAmount, BigDecimal vatPercent) {
        if (vatPercent == null) {
            return null;
        }
        return totalAmount.multiply(vatPercent).divide(BigDecimal.valueOf(100).add(vatPercent), 2, RoundingMode.HALF_UP);
    }
}
