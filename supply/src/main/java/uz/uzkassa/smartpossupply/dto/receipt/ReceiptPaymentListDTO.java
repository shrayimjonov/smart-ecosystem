package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ReceiptPaymentType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.12.2022 11:46
 **/
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptPaymentListDTO implements Serializable {

    ReceiptPaymentType paymentType;

    BigDecimal amount;
}
