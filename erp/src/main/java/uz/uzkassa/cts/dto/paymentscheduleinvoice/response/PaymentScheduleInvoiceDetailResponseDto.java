package uz.uzkassa.cts.dto.paymentscheduleinvoice.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentScheduleInvoiceDetailResponseDto {
    private Long id;
    Long deviceId;
    String deviceSerialNumber;
    private BigDecimal priceFor1pc;
    private BigDecimal vatFor1pc;
    private BigDecimal priceWithVatFor1pc;
}
