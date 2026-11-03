package uz.uzkassa.cts.utils;


import lombok.experimental.UtilityClass;
import uz.uzkassa.cts.domain.Vat;
import uz.uzkassa.cts.dto.vat.VatInvoiceCalculationDto;
import uz.uzkassa.cts.enums.PeriodType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;

@UtilityClass
public final class CalcUtils {

    public static BigDecimal calcVat(BigDecimal vatRate, BigDecimal price) {
        if (price == null || vatRate == null) {
            return null;
        }
        return price.multiply(Vat.DEFAULT_VAT_RATE.divide((Vat.DEFAULT_VAT_RATE.add(BigDecimal.valueOf(100))), MathContext.DECIMAL64));
    }

    public static VatInvoiceCalculationDto calculateInvoice(BigDecimal amountWithVat, Integer productCount, BigDecimal vatRate) {
        BigDecimal totalPriceWithVat = amountWithVat.multiply(BigDecimal.valueOf(productCount));
        BigDecimal vatPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(amountWithVat, vatRate);
        BigDecimal productPriceFor1 = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(amountWithVat, vatPriceFor1);
        BigDecimal totalVatPrice = vatPriceFor1.multiply(BigDecimal.valueOf(productCount));
        BigDecimal totalPrice = totalPriceWithVat.subtract(totalVatPrice);

        return new VatInvoiceCalculationDto()
                .vatRate(vatRate)
                .totalPriceWithVat(totalPriceWithVat)
                .totalPrice(totalPrice)
                .totalVat(totalVatPrice)
                .priceWithVat(amountWithVat)
                .price(productPriceFor1)
                .vat(vatPriceFor1);
    }

    public static BigDecimal calculatePriceByHistory(LocalDateTime createDate, PeriodType periodType, BigDecimal vatRate) {
        BigDecimal baseValue = BigDecimal.valueOf(92000);
        if (createDate.isAfter(LocalDate.of(2022, 2, 1).atStartOfDay())) {
            if (periodType == PeriodType.MONTHLY)
                baseValue = BigDecimal.valueOf(92000);
            if (periodType == PeriodType.YEARLY)
                baseValue = BigDecimal.valueOf(999000);
        }
        if (createDate.isAfter(LocalDate.of(2020, 4, 1).atStartOfDay())) {
            if (periodType == PeriodType.MONTHLY)
                baseValue = BigDecimal.valueOf(75000);
            if (periodType == PeriodType.YEARLY)
                baseValue = BigDecimal.valueOf(825000);
        } else {
            if (periodType == PeriodType.MONTHLY)
                baseValue = BigDecimal.valueOf(50000);
            if (periodType == PeriodType.YEARLY)
                baseValue = BigDecimal.valueOf(550000);
        }

        BigDecimal vatPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(baseValue, vatRate);
        return BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(baseValue, vatPriceFor1);
    }
}
