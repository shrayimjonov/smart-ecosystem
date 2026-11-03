package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.dto.catalog.ProductDTO;

import java.math.BigDecimal;
import java.util.HashSet;

@Getter
@Setter
@ToString
public class TransferDetailDTO {
    private Long id;
    private BigDecimal askedQty;
    private BigDecimal realQty;
    @NotNull(message = "Product is required")
    private ProductDTO product;
    private Long unitId;
    private String unitName;
    private BigDecimal salesPrice;
    private HashSet<String> markings;
}
