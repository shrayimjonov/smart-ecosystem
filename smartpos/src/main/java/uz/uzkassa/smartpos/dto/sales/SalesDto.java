package uz.uzkassa.smartpos.dto.sales;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.receipt.Additional;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
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
public class SalesDto implements Serializable {
    private Long id;
    private String uid;
    private String originUid;
    private String fiscalSign;
    private String fiscalUrl;
    private LocalDateTime createdDateTime;
    private Map<PaymentType, BigDecimal> payments;
    private BigDecimal totalCost;
    private BigDecimal discountPercent;
    private BigDecimal totalPaid;
    private String billId;
    private Long branchId;
    private String branchName;
    private Double lat;
    private Double lon;
    private String terminalSN;
    private Long companyId;
    private String companyName;
    private String companyINN;
    private Long shiftNo;
    private List<SalesDetailDto> receiptDetails;
    private CommonDTO user;
    private SalesType type;
    private SalesStatus status;
    private Source source;
    private Long promotionId;
    private Additional additional;
    private String customerName;
    private String customerPhone;
    private LocalDateTime receiptDateTime;
    private String facturaId;

}
