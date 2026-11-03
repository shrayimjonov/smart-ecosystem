package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 22.12.2022 10:38
 */
@Getter
@Setter
public class ReceiptFilter extends BaseFilter {

    String source;

    String shiftNumber;

    Long creatorId;

    List<String> paymentTypes;

    String paymentType;

    BigDecimal fromTotalPrice;

    BigDecimal toTotalPrice;

    Long terminalId;

    Long userId;

    Boolean isFiscal;

    Long positionCount;
}
