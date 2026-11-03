package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ReceiptPaymentType;
import uz.uzkassa.smartpossupply.enums.Source;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.12.2022 15:40
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ReceiptBaseDTO extends ReceiptBaseTotalDTO {

    Map<ReceiptPaymentType, BigDecimal> payments;

    String fiscalSign;

    String fiscalUrl;

    Long loyalCardId;

    Source source;
}
