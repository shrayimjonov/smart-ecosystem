package uz.uzkassa.smartpos.dto.fashion;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProductDetail;

import java.math.BigDecimal;

@Getter
@Setter
public class FashionIncomeProductItemDto extends FashionIncomeBaseDto {
    private Long id;
    private BigDecimal costPrice;
    private BigDecimal salesPrice;
    private BigDecimal qty;
    private String sizeValue;
    private String barcode;

    public FashionIncomeProductItemDto(FashionIncomeOfProductDetail detail) {
        super(detail);
        this.id = detail.getId();
        this.costPrice = detail.getCostPrice();
        this.salesPrice = detail.getSalesPrice();
        this.qty = detail.getQty();
        this.sizeValue = detail.getProductItem().getSizeValue();
        this.barcode = detail.getProductItem().getBarcode();
    }

    public BigDecimal getProfit() {
        return salesPrice.subtract(costPrice);
    }
}
