
package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalesAndCustomerSummaryDTO {
    Long totalProductsCount;
    BigDecimal totalQuantity;
    BigDecimal totalAmountWithoutVat;
    BigDecimal totalVatAmount;
    BigDecimal totalAmountWithVat;
}
