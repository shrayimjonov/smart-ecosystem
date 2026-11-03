package uz.uzkassa.smartpossupply.dto.receipt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchAddressLookupDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockCommonDTO;
import uz.uzkassa.smartpossupply.enums.ReceiptStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@ToString
public class CabinetReceiptDetailDTO implements Serializable {

    Long id;

    String uid;

    TerminalReceiptFiscalDTO fiscalDTO;

    int positionCount;

    List<ReceiptPaymentListDTO> payments;

    BigDecimal totalCash = BigDecimal.ZERO;

    BigDecimal totalCard = BigDecimal.ZERO;

    BigDecimal totalPrice;

    BigDecimal totalDiscount;

    BigDecimal totalPaid;

    BigDecimal totalQty;

    BigDecimal totalVat = BigDecimal.ZERO;

    CompanyBaseDTO company;

    BranchAddressLookupDTO branch;

    LinkedList<TerminalReceiptItemDTO> items = new LinkedList<>();

    ReceiptStatus status;

    String appVersion;

    CommonDTO customer;

    CommonDTO creator;

    BigDecimal markCount = BigDecimal.ZERO;

    StockCommonDTO stockOut;

    @JsonIgnore
    public void addItem(TerminalReceiptItemDTO item) {
        items.add(item);
    }
}
