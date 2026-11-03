package uz.uzkassa.cts.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BigDecimalUtil {

    private static BigDecimal calculateVatFor1WithoutScale(BigDecimal sellPrice, BigDecimal vatPercent) {
        if (vatPercent == null)
            vatPercent = BigDecimal.ZERO;
        return sellPrice.multiply(vatPercent).divide(vatPercent.add(BigDecimal.valueOf(100L)), MathContext.DECIMAL64);
    }

    //
    public static BigDecimal calculateVatPriceFor1(BigDecimal sellPrice, BigDecimal vatPercent) {
        return setScaleByTwo(calculateVatFor1WithoutScale(sellPrice, vatPercent));
    }

    public static BigDecimal calculateProductPriceFor1SubtractVatPrice(BigDecimal sellPrice, BigDecimal vatPrice) {
        return sellPrice.subtract(vatPrice);
    }

    public static BigDecimal calculatePrice(LocalDate startDate, LocalDate dueDate, BigDecimal price) {
        int dateDayOfMonth = startDate.lengthOfMonth();

        long daysForPay = ChronoUnit.DAYS.between(startDate, dueDate);
        if (daysForPay == 0)
            daysForPay = 1;
        return setScaleByTwo(price.multiply(BigDecimal.valueOf(daysForPay)
                .divide(BigDecimal.valueOf(dateDayOfMonth), MathContext.DECIMAL64)));
    }

    public static BigDecimal calculatePrice(LocalDate startDate, LocalDate dueDate, BigDecimal price, int discountInPercent) {
        int calculateDiscount = 100 - discountInPercent;
        BigDecimal calculate = calculatePrice(startDate, dueDate, price);
        return calculate.divide(BigDecimal.valueOf(100), MathContext.DECIMAL64)
                .multiply(BigDecimal.valueOf(calculateDiscount), MathContext.DECIMAL64);
    }

    public static BigDecimal setScaleByTwo(BigDecimal val) {
        return val.setScale(2, RoundingMode.CEILING);
    }
}
