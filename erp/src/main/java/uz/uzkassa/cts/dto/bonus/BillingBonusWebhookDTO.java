package uz.uzkassa.cts.dto.bonus;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BillingBonusWebhookDTO implements Serializable {
    private String inn;
    private String pinfl;
    private String phoneNumber;
    private String tariff;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal price;
    private String billingId;

    @Override
    public String toString() {
        return "BillingBonusWebhookDTO{" +
                "inn='" + inn + '\'' +
                ", pinfl='" + pinfl + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", tariff='" + tariff + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", price=" + price +
                ", billingId='" + billingId + '\'' +
                '}';
    }
}
