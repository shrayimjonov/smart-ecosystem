package uz.uzkassa.cts.dto.vat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VatInvoiceCalculationDto {
    BigDecimal vatRate;
    BigDecimal totalPriceWithVat;
    BigDecimal totalPrice;
    BigDecimal totalVat;
    BigDecimal priceWithVat;
    BigDecimal price;
    BigDecimal vat;

    public VatInvoiceCalculationDto vatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
        return this;
    }

    public VatInvoiceCalculationDto totalPriceWithVat(BigDecimal totalPriceWithVat) {
        this.totalPriceWithVat = totalPriceWithVat;
        return this;
    }

    public VatInvoiceCalculationDto totalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }
    public VatInvoiceCalculationDto totalVat(BigDecimal totalVat) {
        this.totalVat = totalVat;
        return this;
    }
    public VatInvoiceCalculationDto priceWithVat(BigDecimal priceWithVat) {
        this.priceWithVat = priceWithVat;
        return this;
    }

    public VatInvoiceCalculationDto price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public VatInvoiceCalculationDto vat(BigDecimal vat) {
        this.vat = vat;
        return this;
    }

}
