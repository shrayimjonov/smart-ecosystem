package uz.uzkassa.smartpos.dto.clickhouse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.enums.PaymentType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class ClickhouseReceipt {
    private Long id;
    private String uid;
    private String originUid;
    private String fiscalSign;
    private String fiscalUrl;
    private Double lat;
    private Double lon;
    private String receiptDateTime;
    private BigDecimal totalCash;
    private BigDecimal totalCard;
    private BigDecimal totalLoyaltyCard;
    private BigDecimal totalCost;
    private BigDecimal totalPaid;
    private BigDecimal totalDiscount;
    private BigDecimal totalExcise;
    private BigDecimal discountPercent;
    private BigDecimal totalNds;
    private Long loyaltyCardId;
    private Long shiftNumber;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Byte readOnly = 0;
    private Byte forceToPrint = 0;
    private String status;
    private String source;
    private Map<PaymentType, BigDecimal> payments;

    private Long branchId;
    private String branchName;
    private Long branchCompanyId;
    private Long branchActivityTypeId;
    private Long branchRegionId;
    private Long branchCityId;

    private Long companyId;
    private String companyInn;
    private String companyName;
    private Long companyRegionId;
    private Long companyCityId;

    private Long createdById;
    private String createdByFirstname;
    private String createdByLastname;

    private Long terminalId;
    private String terminalSerialNumber;

    private String createdDateTime;
    private List<Long> receiptDetailIds;
    private String additional;
}
