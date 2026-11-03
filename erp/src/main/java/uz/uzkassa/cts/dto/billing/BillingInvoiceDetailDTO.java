package uz.uzkassa.cts.dto.billing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingInvoiceDetailDTO implements Serializable {
    String serviceName;
    Integer productCount;
    BigDecimal price1pcWithVat;
    BigDecimal overallPriceWithVat;
    Long xizmatId;
    Long primeScheduleId;
}
