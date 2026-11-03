package uz.uzkassa.smartpos.domain.postgres.receipt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Additional implements Serializable {
    private String samModuleSerialNumber;
    private String receiptSeq;
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
    private boolean forCredit;
    private Integer month;
    private String homeServiceAccount;
    private String cardPan;
    private String appType;
}
