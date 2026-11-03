package uz.uzkassa.horeca.dto.terminal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TerminalSalesStatsDTO {
    Long id;

    String name;

    String vatBarcode;
    String barcode;

    String unit;

    String category;

    BigDecimal price;

    BigDecimal salesCount;

    BigDecimal salesTotal;

    BigDecimal nds;

    BigDecimal ndsPercent;

    BigDecimal discount;

    Long receiptCount;

    String uidList;

    LocalDateTime dateTime;

    Long branchId;

    String branchName;

    public TerminalSalesStatsDTO(Long id, String name, String vatBarcode, String barcode, String unit, String category, BigDecimal price, BigDecimal salesCount,
                                 BigDecimal salesTotal, BigDecimal nds, BigDecimal ndsPercent, BigDecimal discount, BigInteger receiptCount, String uidList,
                                 Long branchId, String branchName) {
        this.id = id;
        this.name = name;
        this.vatBarcode = vatBarcode;
        this.barcode = barcode;
        this.unit = unit;
        this.category = category;
        this.price = price;
        this.salesCount = salesCount;
        this.salesTotal = salesTotal;
        this.nds = nds;
        this.ndsPercent = ndsPercent;
        this.discount = discount;
        this.receiptCount = receiptCount != null ? receiptCount.longValue() : null;
        this.uidList = uidList;
        this.branchId = branchId;
        this.branchName = branchName;
    }

    public TerminalSalesStatsDTO(String name, String unit, BigDecimal price, BigDecimal salesCount,
                                 BigDecimal salesTotal, BigDecimal nds, BigDecimal discount, BigInteger receiptCount, LocalDateTime dateTime) {
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.salesCount = salesCount;
        this.salesTotal = salesTotal;
        this.nds = nds;
        this.discount = discount;
        this.receiptCount = receiptCount != null ? receiptCount.longValue() : null;
        this.dateTime = dateTime;
    }
}
