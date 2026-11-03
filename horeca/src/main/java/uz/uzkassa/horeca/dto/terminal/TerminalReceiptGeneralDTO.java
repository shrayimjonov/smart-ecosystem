package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.ReceiptPaymentType;
import uz.uzkassa.horeca.enums.ReceiptStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@ToString
public class TerminalReceiptGeneralDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5645634L;

    Long id;

    String originUid;

    @Size(max = 255)
    String fiscalSign;

    @Size(max = 255)
    String fiscalUrl;

    LocalDateTime createdDateTime;

    Map<ReceiptPaymentType, BigDecimal> payments;

    BigDecimal totalCash = BigDecimal.ZERO;

    BigDecimal totalCard = BigDecimal.ZERO;

    BigDecimal totalLoyaltyCard = BigDecimal.ZERO;

//    @NotNull
    BigDecimal totalCost;

    BigDecimal totalDiscount;

    BigDecimal totalExcise;

    BigDecimal discountPercent;

//    @NotNull
    BigDecimal totalPaid;

//    @NotNull
    BigDecimal totalNds = BigDecimal.ZERO;

    String billId;

    Long loyaltyCardId;

    Long customerId;

    Long branchId;

    String branchName;

    String branchAddress;

    Long terminalId;

    String terminalSN;

    String terminalModel = "N5";

    Long companyId;

    String companyName;

    String companyINN;

    Long shiftNo;

    LinkedList<TerminalReceiptDetailDTO> receiptDetails;

    TerminalUserDTO user;

    Long userId;

    ReceiptStatus status;

    TerminalSource source;

    Long promotionId;

    BigDecimal totalPromotionBonus;

    BigDecimal totalProductPromotionBonuses;

    BigDecimal totalBasketPromotionBonuses;

    Double discountByPromotionPercent;

    TerminalAdditionalDTO additional;

    public List<TerminalReceiptDetailDTO> getReceiptDetails() {
        if (receiptDetails == null) {
            receiptDetails = new LinkedList<>();
        }
        return receiptDetails;
    }
    public void addReceiptDetail(TerminalReceiptDetailDTO receiptDetailDTO) {
        getReceiptDetails().add(receiptDetailDTO);
    }

}
