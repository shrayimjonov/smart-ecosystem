package uz.uzkassa.smartpossupply.dto.agent.performancereport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AgentPerformanceReportOrderPaymentDTO implements Serializable {

    PaymentType paymentType;

    BigDecimal totalPrice;

    @SuppressWarnings("unused")
    public AgentPerformanceReportOrderPaymentDTO(String paymentType, BigDecimal totalPrice) {
        this.paymentType = PaymentType.valueOf(paymentType);
        this.totalPrice = totalPrice;
    }
}
