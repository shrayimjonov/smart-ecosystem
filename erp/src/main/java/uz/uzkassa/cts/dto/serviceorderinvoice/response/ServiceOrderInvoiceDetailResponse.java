package uz.uzkassa.cts.dto.serviceorderinvoice.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ServiceOrderInvoiceDetailResponse {
    private Long id;
    private Long tariffId;
    private String tariffName;
    private Integer deviceCount;
    private BigDecimal price1pc;
    private BigDecimal vat;
    private BigDecimal totalPrice;
    private BigDecimal totalPriceWithVAT;
}
