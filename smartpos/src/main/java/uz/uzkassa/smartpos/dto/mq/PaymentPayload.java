package uz.uzkassa.smartpos.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPayload implements Serializable {
    private String accountCode;
    private Long companyId;
    private String receiptUid;
    private BigDecimal amount;
    private String trxId;
    private Long paymentId;
}
