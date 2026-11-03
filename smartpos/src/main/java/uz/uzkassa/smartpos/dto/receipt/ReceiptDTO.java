package uz.uzkassa.smartpos.dto.receipt;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.domain.postgres.receipt.Additional;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.enums.Source;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class ReceiptDTO implements Serializable {
    private Long id;

    private String originUid;

    @Size(max = 255)
    private String fiscalSign;

    @Size(max = 255)
    private String fiscalUrl;

    private LocalDateTime createdDateTime;

    private Map<PaymentType, BigDecimal> payments;

    private BigDecimal totalCash;

    private BigDecimal totalCard;

    private BigDecimal totalLoyaltyCard;

    @NotNull
    private BigDecimal totalCost;

    private BigDecimal totalDiscount;

    private BigDecimal totalExcise;

    private BigDecimal discountPercent;

    @NotNull
    private BigDecimal totalPaid;

    private BigDecimal totalNds;

    private String billId;

    private Long loyaltyCardId;

    private Long customerId;

    private Long branchId;

    private String branchName;

    private String branchAddress;

    private Long terminalId;

    private String terminalSN;

    private Long companyId;

    private String companyName;

    private String companyINN;

    private Long shiftNo;

    private LinkedList<ReceiptDetailDTO> receiptDetails;

    private UserDTO user;

    private Long userId;

    private ReceiptStatus status;

    private Source source;

    private Long promotionId;

    private Additional additional;

    private Double lat;
    private Double lon;
    @Size(max = 255)
    private String customerName;
    @Size(max = 255)
    private String customerPhone;
    private boolean readOnly;
    private boolean forceToPrint;

    private String uid;

    private LocalDateTime receiptDateTime;
    private int positionCount;
    private String facturaId;
    private boolean sendSms;
    private String language;
    private String stockOutId;

    public List<ReceiptDetailDTO> getReceiptDetails() {
        if (receiptDetails == null) {
            receiptDetails = new LinkedList<>();
        }
        return receiptDetails;
    }
}
