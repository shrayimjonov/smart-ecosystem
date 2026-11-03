package uz.uzkassa.smartpos.dto.clickhouse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@ToString
public class ClickhouseReceiptDetail {
    private Long id;
    private Long productId;
    private String productName;
    private String productBarcode;
    private String vatBarcode;
    private Long unitId;
    private String unitName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal qty;
    private BigDecimal amount;
    private BigDecimal nds;
    private BigDecimal ndsPercent;
    private BigDecimal discount;
    private BigDecimal discountPercent;
    private BigDecimal exciseRate;
    private BigDecimal excise;
    private Set<String> marks;
    private String receiptDateTime;
    private Long receiptId;
    private Long createdById;
    private String status;
    private Long companyId;
    private Long branchId;
    private String branchName;
    private String committentTin;
    private String extraDetail;
}
