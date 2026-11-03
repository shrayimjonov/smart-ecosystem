package uz.uzkassa.smartpos.dto.receipt;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NonecacheReceiptSummByTerminal {
    private Long terminalId;
    private String serialNumber;
    private Long companyId;
    private String companyName;
    private String companyInn;
    private BigDecimal amount;
    private String paymentType;

    public NonecacheReceiptSummByTerminal(Long terminalId, String serialNumber, Long companyId, String companyName, String companyInn, BigDecimal amount, String paymentType) {
        this.terminalId = terminalId;
        this.serialNumber = serialNumber;
        this.companyId = companyId;
        this.companyName = companyName;
        this.companyInn = companyInn;
        this.amount = amount;
        this.paymentType = paymentType;
    }
}
