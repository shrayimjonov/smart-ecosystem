package uz.uzkassa.smartpos.dto.receipt;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.domain.postgres.receipt.ExtraDetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@ToString
public class ReceiptDetailDTO implements Serializable {
    protected Long productId;
    @Size(max = 255)
    protected String productName;
    @Size(max = 255)
    protected String productBarcode;
    @Size(max = 255)
    protected String vatBarcode;
    protected Long unitId;
    protected String unitName;
    protected BigDecimal excise;
    protected BigDecimal exciseRate;
    @NotNull
    protected BigDecimal price;
    protected BigDecimal salesPrice;
    @NotNull
    protected BigDecimal qty;
    @NotNull
    protected BigDecimal amount;
    protected BigDecimal discountPercent;
    protected BigDecimal discount;
    protected BigDecimal ndsPercent;
    protected BigDecimal nds;
    protected Set<String> marks = new HashSet<>();
    private String committentTin;
    private Long categoryId;
    @Size(max = 255)
    private String categoryName;
    private Long receiptId;
    private Long branchId;
    private ExtraDetail extraDetail;

}
