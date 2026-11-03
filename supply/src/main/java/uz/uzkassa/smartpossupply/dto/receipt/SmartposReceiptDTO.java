package uz.uzkassa.smartpossupply.dto.receipt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import uz.uzkassa.smartpossupply.enums.ReceiptPaymentType;
import uz.uzkassa.smartpossupply.enums.ReceiptStatus;
import uz.uzkassa.smartpossupply.enums.Source;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SmartposReceiptDTO implements Serializable {

    Long id;

    String originUid;

    String uid;

    @Size(max = 255)
    String fiscalSign;

    @Size(max = 255)
    String fiscalUrl;

    LocalDateTime receiptDateTime;

    int positionCount;

    LocalDateTime createdDateTime;

    Map<ReceiptPaymentType, BigDecimal> payments;

    BigDecimal totalCash = BigDecimal.ZERO;

    BigDecimal totalCard = BigDecimal.ZERO;

    BigDecimal totalLoyaltyCard = BigDecimal.ZERO;

    BigDecimal totalCost;

    BigDecimal totalDiscount;

    BigDecimal discountPercent;

    BigDecimal totalPaid;

    BigDecimal totalNds = BigDecimal.ZERO;

    Long branchId;

    String branchName;

    String branchAddress;

    String terminalSN;

    String companyName;

    String companyINN;

    Long shiftNo;

    LinkedList<SmartposReceiptItemDTO> receiptDetails = new LinkedList<>();

    ReceiptStatus status;

    Source source;

    String stockOutId;

    BigDecimal markCount = BigDecimal.ZERO;

    @JsonIgnore
    public void addItem(SmartposReceiptItemDTO item) {
        receiptDetails.add(item);
    }

    @JsonIgnore
    public void clone(SmartposReceiptDTO smartposReceiptDTO) {
        smartposReceiptDTO.setFiscalSign(this.fiscalSign);
        smartposReceiptDTO.setFiscalUrl(this.fiscalUrl);
        smartposReceiptDTO.setReceiptDateTime(this.receiptDateTime);
        smartposReceiptDTO.setCreatedDateTime(this.createdDateTime);
        smartposReceiptDTO.setBranchId(this.branchId);
        smartposReceiptDTO.setBranchName(this.branchName);
        smartposReceiptDTO.setBranchAddress(this.branchAddress);
        smartposReceiptDTO.setTerminalSN(this.terminalSN);
        smartposReceiptDTO.setCompanyName(this.companyName);
        smartposReceiptDTO.setCompanyINN(this.companyINN);
        smartposReceiptDTO.setShiftNo(this.shiftNo);
        smartposReceiptDTO.setStatus(this.status);
        smartposReceiptDTO.setSource(this.source);
        smartposReceiptDTO.setStockOutId(this.stockOutId);
        if (smartposReceiptDTO.getUid() == null) {
            smartposReceiptDTO.setUid(String.valueOf(System.currentTimeMillis()).concat(RandomStringUtils.randomNumeric(4, 4)));
        }
    }
}
