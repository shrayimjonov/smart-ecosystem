package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.enums.ReceiptPaymentType;
import uz.uzkassa.horeca.enums.ReceiptStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Map;

@Getter
@Setter
public class TerminalReceiptPayload implements Serializable {
    private String originUid;
    private Map<ReceiptPaymentType, BigDecimal> payments;
    private BigDecimal totalCash = BigDecimal.ZERO;
    private BigDecimal totalCard = BigDecimal.ZERO;
    @NotNull
    private BigDecimal totalCost;
    private BigDecimal totalDiscount;
    private BigDecimal totalExcise;
    private BigDecimal discountPercent;
    @NotNull
    private BigDecimal totalPaid;
    @NotNull
    private BigDecimal totalNds = BigDecimal.ZERO;
    private Long branchId;
    private String branchName;
    private String branchAddress;
    @NotBlank
    private String terminalSN;
    private Long companyId;
    private String companyName;
    private String companyINN;
    private LinkedList<TerminalReceiptDetailDTO> receiptDetails;
    private TerminalUserDTO user;
    private Long userId;
    private ReceiptStatus status;
}
