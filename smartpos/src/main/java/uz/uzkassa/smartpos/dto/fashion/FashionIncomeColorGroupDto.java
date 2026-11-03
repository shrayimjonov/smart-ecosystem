package uz.uzkassa.smartpos.dto.fashion;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProductDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class FashionIncomeColorGroupDto extends FashionIncomeBaseDto {
    private Long id;
    private String colorCode;
    private String colorName;
    private String printName;
    private String sizeKey;
    private BigDecimal qty;
    private List<FashionIncomeProductItemDto> items = new ArrayList<>();

    public FashionIncomeColorGroupDto(FashionIncomeOfProductDetail detail) {
        super(detail);
        this.id = detail.getProductItem().getColorGroup().getGenericId();
        this.colorCode = detail.getProductItem().getColorGroup().getColorCode();
        this.colorName = detail.getProductItem().getColorGroup().getColorName();
        this.printName = detail.getProductItem().getColorGroup().getPrintName();
        this.sizeKey = detail.getProductItem().getColorGroup().getSizeKey();
    }

    public BigDecimal getProfit() {
        if (CollectionUtils.isNotEmpty(items)) {
            return items.getFirst().getProfit();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FashionIncomeColorGroupDto that = (FashionIncomeColorGroupDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
