package uz.uzkassa.cts.dto.paymentCompanyQuote;

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
public class PaymentCompanyQuoteTemplateDTO implements Serializable {

    Long id;
    String modelName;
    String tin;
    String companyName;
    String createdDate;
    String oked;
    String accountNumber;
    String bankName;
    String fullAddress;
    Integer qty;
    BigDecimal totalPrice;
    BigDecimal totalPriceWithVat;
    BigDecimal priceWithVat;
    BigDecimal price;

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }
}
