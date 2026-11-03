package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.catalog.ProductDTO;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class IncomeOfProductDetailDTO {
    private Long id;
    private BigDecimal costPrice;
    private BigDecimal markup;
    private BigDecimal salesPrice;
    @NotNull
    private BigDecimal qty;
    @NotNull
    private Long productId;
    private ProductDTO product;
    @NotNull
    private Long unitId;
    private String unitName;
    private List<String> markings;
    private BigDecimal totalSum;
}
