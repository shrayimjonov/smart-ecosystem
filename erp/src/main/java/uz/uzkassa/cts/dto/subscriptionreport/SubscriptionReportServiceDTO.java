package uz.uzkassa.cts.dto.subscriptionreport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionReportServiceDTO implements Serializable {


    Long employeeId;

    Long serviceId;

    BigInteger qty;

    BigDecimal bonus;

    BigDecimal price;
}
