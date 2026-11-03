package uz.uzkassa.cts.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PaymentContractDetailDTO implements Serializable {

    private List<Long> deviceIds;
    private Long paymentContractId;

    private BigDecimal price1pc;
    private BigDecimal price1pcWithVat;
    private BigDecimal vat1pc;
    private BigDecimal priceWithVat;
}
