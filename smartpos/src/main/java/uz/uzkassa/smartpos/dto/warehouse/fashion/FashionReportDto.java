package uz.uzkassa.smartpos.dto.warehouse.fashion;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.ReceiptStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class FashionReportDto {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    private Long productId;
    private String productName;
    private Long branchId;
    private String branchName;
    private Long companyId;
    private String companyName;
    private Long cityId;
    private Long colorGroupId;
    private String colorCode;
    private String colorName;
    private String printName;
    private Long itemId;
    private String sizeCode;
    private String sizeValue;
    private Long categoryId;
    private String categoryName;
    private Long unitId;
    private String unitName;
    private BigDecimal salesPrice;
    private BigDecimal qty;
    private BigDecimal totalDiscount;
    private BigDecimal totalVat;
    private BigDecimal totalSumma;
    private Long receiptId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ReceiptStatus status;
    private Long userId;
    private String userFullName;
}
