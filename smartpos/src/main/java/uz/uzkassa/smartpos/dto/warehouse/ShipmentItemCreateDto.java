package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.warehouse.ShipmentItem;
import uz.uzkassa.smartpos.enums.ProductOrigin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link ShipmentItem}
 */
@Getter
@Setter
public class ShipmentItemCreateDto implements Serializable {
    private Long id;
    @NotNull
    private Long productId;
    @NotNull
    private Long branchId;
    @NotBlank
    private String vatBarcode;
    private String barcode;
    @NotBlank
    private String name;
    @NotBlank
    private String packageCode;
    @NotBlank
    private String packageName;
    @NotNull
    private BigDecimal price;
    @NotNull
    private Long unitId;
    private BigDecimal excise;
    private BigDecimal vatRate;
    @NotNull
    private ProductOrigin origin;
    private Set<String> markings;
    private Set<String> aggregations;
    @NotNull
    @Min(0L)
    private BigDecimal qty;
}
