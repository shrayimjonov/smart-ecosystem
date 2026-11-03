package uz.uzkassa.smartpos.dto.sales;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.receipt.ExtraDetail;
import uz.uzkassa.smartpos.dto.clickhouse.SalesDetailClickhouseInfoDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
public class SalesDetailDto implements Serializable {
    private Long productId;
    private String productName;
    private String productBarcode;
    private String vatBarcode;
    private Long unitId;
    private String unitName;
    private BigDecimal excise;
    private BigDecimal exciseRate;
    private BigDecimal price;
    private BigDecimal qty;
    private BigDecimal amount;
    private BigDecimal discountPercent;
    private BigDecimal discount;
    private BigDecimal ndsPercent;
    private BigDecimal nds;
    private Set<String> marks = new HashSet<>();
    private String committentTin;
    private Long categoryId;
    private String categoryName;
    private Long salesId;
    private Long branchId;
    private ExtraDetail extraDetail;

    public SalesDetailDto(SalesDetailClickhouseInfoDto infoDto) {
        this.productId = infoDto.getProductId();
        this.productName = infoDto.getProductName();
        this.productBarcode = infoDto.getProductBarcode();
        this.vatBarcode = infoDto.getVatBarcode();
        if (infoDto.getUnit() != null) {
            this.unitId = infoDto.getUnit().getId();
            this.unitName = infoDto.getUnit().getName();
        }
        this.excise = infoDto.getExcise();
        this.exciseRate = infoDto.getExciseRate();
        this.price = infoDto.getPrice();
        this.qty = infoDto.getQty();
        this.amount = infoDto.getAmount();
        this.discountPercent = infoDto.getDiscountPercent();
        this.discount = infoDto.getDiscount();
        this.ndsPercent = infoDto.getVatPercent();
        this.nds = infoDto.getVat();
        this.marks = infoDto.getMarks();
        this.committentTin = infoDto.getCommittentTin();
        if (infoDto.getCategory() != null) {
            this.categoryId = infoDto.getCategory().getId();
            this.categoryName = infoDto.getCategory().getName();
        }
        this.salesId = infoDto.getSalesId();
        if (infoDto.getBranch() != null) {
            this.branchId = infoDto.getBranch().getId();
        }
        this.extraDetail = infoDto.getExtraDetail();
    }
}
