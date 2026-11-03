package uz.uzkassa.horeca.dto.receipt.cabinet;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.dto.reservation.CustomerDetailDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalSource;
import uz.uzkassa.horeca.enums.ReceiptPaymentType;
import uz.uzkassa.horeca.enums.ReceiptStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ReceiptDetailCabinetDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 23423098647L;

    Long id;

    String originUid;

    @Size(max = 255)
    String fiscalSign;

    @Size(max = 255)
    String fiscalUrl;

    Map<ReceiptPaymentType, BigDecimal> payments;

    BigDecimal totalCash = BigDecimal.ZERO;

    BigDecimal totalCard = BigDecimal.ZERO;

    BigDecimal totalCost;

    BigDecimal totalDiscount;

    BigDecimal discountPercent;

    BigDecimal totalPaid;

    BigDecimal totalVat = BigDecimal.ZERO;

    CommonDTO branch;

    CommonCodeDTO terminal;

    CompanyCommonDTO company;

    Long shiftNo;

    Integer numberInShift;

    String receiptSeq;

    List<ReceiptItemCabinetDTO> items = new ArrayList<>();

    CommonDTO user;

    ReceiptStatus status;

    TerminalSource source;

    CustomerDetailDTO customer;

    String uid;

    LocalDateTime receiptDateTime;

    BigDecimal itemsCount;

    CommonDTO table;

    CommonDTO order;

    public void addItem(ReceiptItemCabinetDTO item) {
        items.add(item);
    }
}
