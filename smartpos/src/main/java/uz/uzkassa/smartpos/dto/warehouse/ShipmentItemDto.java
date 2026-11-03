package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.warehouse.ShipmentItem;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.enums.ProductOrigin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link ShipmentItem}
 */
@Getter
@Setter
public class ShipmentItemDto implements Serializable {
    private Long id;
    private String createdBy;
    private LocalDateTime createdDate = LocalDateTime.now();
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate = LocalDateTime.now();
    @NotNull
    private Long productId;
    @NotNull
    private Long branchId;
    @NotNull
    private Long cityId;
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
    private CommonDTO unit;
    private BigDecimal excise;
    private BigDecimal vatRate;
    private ProductOrigin origin;
    private Set<String> markings;
    private Set<String> aggregations;
    private BigDecimal qty = BigDecimal.ZERO;
}
