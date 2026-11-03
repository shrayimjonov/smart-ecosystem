package uz.uzkassa.cts.dto.payment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentContractTemplateDTO implements Serializable {

    Long id;
    String contractNumber;
    String modelName;
    List<String> serialNumbers;
    String tin;
    String customerName;
    String businessType;
    String phone;
    String createdDate;
    String oked;
    String accountNumber;
    String bankName;
    String region;
    String district;
    String neighborhood;
    String street;
    String house;
    String apartment;
    String ownerName;
    Integer qty;
    BigDecimal vatRate;
    BigDecimal totalPriceWithVat;
    BigDecimal totalPrice;
    BigDecimal totalVat;
    BigDecimal priceWithVat;
    BigDecimal price;
    BigDecimal vat;

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }
}
