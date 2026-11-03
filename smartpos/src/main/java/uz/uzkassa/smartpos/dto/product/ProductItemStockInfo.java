package uz.uzkassa.smartpos.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.uzkassa.smartpos.dto.fashion.CountItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemStockInfo implements Serializable {
    private String colorCode;
    private String colorName;
    private BigDecimal salesPrice;
    private String printName;
    private String sizeKey;
    private List<CountItem> items;
    private List<ColorGroupImageDTO> images;
}
