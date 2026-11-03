package uz.uzkassa.smartpossupply.dto.receipt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchAddressLookupDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.ReceiptStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Map;

@Getter
@Setter
@ToString
public class TerminalReceiptDTO implements Serializable {

    Long id;

    String uid;

    TerminalReceiptFiscalDTO fiscalDTO;

    int positionCount;

    Map<String, BigDecimal> payments;

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

    Long stockOutId;

    Long stockReturnId;

    CommonDTO customer;

    CommonDTO creator;

    BigDecimal markCount = BigDecimal.ZERO;

    @JsonIgnore
    public void addItem(TerminalReceiptItemDTO item) {
        items.add(item);
    }

    @JsonIgnore
    public void clone(TerminalReceiptDTO smartposReceiptDTO) {
        smartposReceiptDTO.setFiscalDTO(this.fiscalDTO);
        smartposReceiptDTO.setBranch(this.getBranch());
        smartposReceiptDTO.setCompany(this.getCompany());
        smartposReceiptDTO.setCustomer(this.getCustomer());
        smartposReceiptDTO.setStockOutId(this.stockOutId);
        smartposReceiptDTO.setMarkCount(this.markCount);
        smartposReceiptDTO.setAppVersion(this.appVersion);
        if (smartposReceiptDTO.getUid() == null) {
            smartposReceiptDTO.setUid(String.valueOf(System.currentTimeMillis()).concat(RandomStringUtils.randomNumeric(4, 4)));
        }
    }
}
