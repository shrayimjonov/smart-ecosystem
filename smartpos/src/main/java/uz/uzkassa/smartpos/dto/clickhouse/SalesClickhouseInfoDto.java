package uz.uzkassa.smartpos.dto.clickhouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import uz.uzkassa.smartpos.domain.postgres.receipt.Additional;
import uz.uzkassa.smartpos.domain.postgres.receipt.SalesDetail;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.Source;
import uz.uzkassa.smartpos.enums.sales.SalesStatus;
import uz.uzkassa.smartpos.enums.sales.SalesType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesClickhouseInfoDto implements Serializable {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiptDateTime;

    private String uid;
    private String originUid;
    private String fiscalSign;
    private String fiscalUrl;
    private Long shiftNumber;
    private Double lat;
    private Double lon;
    private BigDecimal totalCost;
    private BigDecimal totalPaid;
    private BigDecimal discountPercent;
    private String customerName;
    private String customerPhone;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private SalesType type;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private SalesStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Source source;
    private Map<PaymentType, BigDecimal> payments;
    private CommonDTO branch;
    private CompanyBaseDTO company;
    private CommonDTO createdBy;
    private String terminalSN;
    private String billId;
    private String facturaId;
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
    private boolean sendSms;
    private List<Long> salesDetailIds;

    public SalesClickhouseInfoDto(Sales sales) {
        this.id = sales.getId();
        this.createdAt = sales.getCreatedAt();
        this.createdDateTime = sales.getCreatedDateTime();
        this.receiptDateTime = sales.getReceiptDateTime();
        this.uid = sales.getUid();
        this.originUid = sales.getOriginUid();
        this.fiscalSign = sales.getFiscalSign();
        this.fiscalUrl = sales.getFiscalUrl();
        this.shiftNumber = sales.getShiftNumber();
        this.lat = sales.getLat();
        this.lon = sales.getLon();
        this.totalCost = sales.getTotalCost();
        this.totalPaid = sales.getTotalPaid();
        this.discountPercent = sales.getDiscountPercent();
        this.type = sales.getType();
        this.status = sales.getStatus();
        this.source = sales.getSource();
        this.payments = sales.getPayments();
        if (sales.getBranch() != null) {
            this.branch = sales.getBranch().toCommonDTO();
        }
        if (sales.getCompany() != null) {
            this.company = sales.getCompany().toCompanyBaseDTO();
        }
        if (sales.getCreatedBy() != null) {
            this.createdBy = new CommonDTO(sales.getCreatedById(), sales.getCreatedBy().getFullNameStr());
        }
        this.terminalSN = sales.getTerminalSN();
        this.facturaId = sales.getFacturaId();
        if (sales.getAdditional() != null) {
            this.billId = sales.getAdditional().getBillId();
            this.customerName = sales.getAdditional().getCustomerName();
            this.customerPhone = sales.getAdditional().getCustomerPhone();
            this.sendSms = sales.getAdditional().isSendSms();
            this.samModuleSerialNumber = sales.getAdditional().getSamModuleSerialNumber();
            this.receiptSeq = sales.getAdditional().getReceiptSeq();
            this.fiscalCreatedDate = sales.getAdditional().getFiscalCreatedDate();
            this.providerId = sales.getAdditional().getProviderId();
            this.transactionId = sales.getAdditional().getTransactionId();
            this.numberInShift = sales.getAdditional().getNumberInShift();
            this.epaymentId = sales.getAdditional().getEpaymentId();
            this.voucherId = sales.getAdditional().getVoucherId();
            this.carPlateNumber = sales.getAdditional().getCarPlateNumber();
            this.columnNumber = sales.getAdditional().getColumnNumber();
            this.cardType = sales.getAdditional().getCardType();
            this.rrn = sales.getAdditional().getRrn();
            this.terminalVersion = sales.getAdditional().getTerminalVersion();
            this.forCredit = sales.getAdditional().isForCredit();
            this.month = sales.getAdditional().getMonth();
            this.homeServiceAccount = sales.getAdditional().getHomeServiceAccount();
            this.cardPan = sales.getAdditional().getCardPan();
            this.appType = sales.getAdditional().getAppType();
        }
        if (CollectionUtils.isNotEmpty(sales.getSalesDetails())) {
            this.salesDetailIds = sales.getSalesDetails().stream().map(SalesDetail::getId)
                    .toList();
        }
    }

    @JsonIgnore
    public Additional getAdditional() {
        Additional additional = new Additional();
        additional.setSamModuleSerialNumber(this.samModuleSerialNumber);
        additional.setReceiptSeq(this.receiptSeq);
        additional.setFiscalCreatedDate(this.fiscalCreatedDate);
        additional.setProviderId(this.providerId);
        additional.setTransactionId(this.transactionId);
        additional.setNumberInShift(this.numberInShift);
        additional.setEpaymentId(this.epaymentId);
        additional.setVoucherId(this.voucherId);
        additional.setCarPlateNumber(this.carPlateNumber);
        additional.setColumnNumber(this.columnNumber);
        additional.setCardType(this.cardType);
        additional.setRrn(this.rrn);
        additional.setTerminalVersion(this.terminalVersion);
        additional.setForCredit(this.forCredit);
        additional.setMonth(this.month);
        additional.setHomeServiceAccount(this.homeServiceAccount);
        additional.setCardPan(this.cardPan);
        additional.setAppType(this.appType);
        return additional;
    }
}
