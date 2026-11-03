package uz.uzkassa.cts.dto.paymentQuote;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentQuoteTemplateDTO implements Serializable {

    Long id;
    String modelName;
    String tin;
    String customerName;
    String createdDate;
    String fullAddress;
    Integer qty;
    BigDecimal totalPrice;
    BigDecimal totalPriceWithVat;
    BigDecimal priceWithVat;
    BigDecimal price;
    BigDecimal totalVatPrice;

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }
}
