package uz.uzkassa.smartpossupply.dto.transaction;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 08.02.2023 10:13
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionItemDTO {

    Long itemId;

    BigDecimal credit;

    BigDecimal debit;

    Long accountId;
}
