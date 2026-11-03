package uz.uzkassa.smartpos.dto.sales;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SalesFiscalDto {
    @NotNull
    private String uid;
    @NotNull
    private String fiscalSign;
    @NotNull
    private String fiscalUrl;
    private String terminalSN;

    private String samModuleSerialNumber;
    private String receiptSeq;
    @NotNull
    private LocalDateTime fiscalCreatedDate;
    private Long providerId;
    private String transactionId;
    private Integer numberInShift;
    private String epaymentId;
    private String voucherId;
    private String carPlateNumber;
    private String columnNumber;
    private String cardType;
    private String rrn;
    private String terminalVersion;
    private String homeServiceAccount;
    private String cardPan;
    private String appType;
}
