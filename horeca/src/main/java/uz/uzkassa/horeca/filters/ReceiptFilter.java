package uz.uzkassa.horeca.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.ReceiptPaymentType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.05.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptFilter extends BaseFilter {

    Long terminalId;

    Boolean isFiscal;

    Long cashierId;

    Integer shiftNumber;

    ReceiptPaymentType paymentType;

    Integer itemsCount;

    BigDecimal fromTotalPrice;

    BigDecimal toTotalPrice;

    List<String> paymentTypes;
}
