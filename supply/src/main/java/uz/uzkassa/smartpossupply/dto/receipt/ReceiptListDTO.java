package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ReceiptStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.12.2022 11:46
 **/
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptListDTO implements Serializable {

    Long id;

    LocalDateTime createdDate;

    ReceiptStatus status;

    BigDecimal totalPrice;

    Long shiftNumber;

    BigDecimal totalCash;

    BigDecimal totalCard;

    BigDecimal totalVat;

    String terminalSerialNumber;

    List<ReceiptPaymentListDTO> payments;
}
