package uz.uzkassa.smartpos.domain.postgres.sales;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.receipt.Additional;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class  SalesAdditional implements Serializable {
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
    private String customerName;
    private String customerPhone;
    private String billId;
    private boolean sendSms;
    private Long customerId;
    private boolean newCustomer;

    public SalesAdditional(Additional additional) {
        this.samModuleSerialNumber = additional.getSamModuleSerialNumber();
        this.receiptSeq = additional.getReceiptSeq();
        this.fiscalCreatedDate = additional.getFiscalCreatedDate();
        this.providerId = additional.getProviderId();
        this.transactionId = additional.getTransactionId();
        this.numberInShift = additional.getNumberInShift();
        this.epaymentId = additional.getEpaymentId();
        this.voucherId = additional.getVoucherId();
        this.carPlateNumber = additional.getCarPlateNumber();
        this.columnNumber = additional.getColumnNumber();
        this.cardType = additional.getCardType();
        this.rrn = additional.getRrn();
        this.terminalVersion = additional.getTerminalVersion();
        this.forCredit = additional.isForCredit();
        this.month = additional.getMonth();
        this.homeServiceAccount = additional.getHomeServiceAccount();
        this.cardPan = additional.getCardPan();
        this.appType = additional.getAppType();
    }

    @JsonIgnore
    public Additional getAdditional() {
        Additional additional = new Additional();
        additional.setSamModuleSerialNumber(samModuleSerialNumber);
        additional.setReceiptSeq(receiptSeq);
        additional.setFiscalCreatedDate(fiscalCreatedDate);
        additional.setProviderId(providerId);
        additional.setTransactionId(transactionId);
        additional.setNumberInShift(numberInShift);
        additional.setEpaymentId(epaymentId);
        additional.setVoucherId(voucherId);
        additional.setCarPlateNumber(carPlateNumber);
        additional.setColumnNumber(columnNumber);
        additional.setCardType(cardType);
        additional.setRrn(rrn);
        additional.setTerminalVersion(terminalVersion);
        additional.setForCredit(forCredit);
        additional.setMonth(month);
        additional.setHomeServiceAccount(homeServiceAccount);
        additional.setCardPan(cardPan);
        additional.setAppType(appType);
        return additional;
    }
}
