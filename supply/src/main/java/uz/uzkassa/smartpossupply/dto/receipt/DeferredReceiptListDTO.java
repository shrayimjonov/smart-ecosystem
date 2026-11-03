package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.12.2022 11:46
 **/
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeferredReceiptListDTO implements Serializable {

    Long id;

    LocalDateTime createdDate;

    BigDecimal totalCost;

    String customerName;

    String customerPhone;
}
