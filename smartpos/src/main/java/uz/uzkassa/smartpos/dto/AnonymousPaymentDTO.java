package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AnonymousPaymentDTO {
    private String emitent;
    private String operator;
    private String agent;
    private String billId;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private String status;
    private String anonymousWalletPan;
    private String receiverPan;
    private String paymentType;
    private String merchantName;
    private String transactionDate;
}
