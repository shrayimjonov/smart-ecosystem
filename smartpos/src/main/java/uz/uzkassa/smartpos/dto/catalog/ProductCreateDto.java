package uz.uzkassa.smartpos.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uz.uzkassa.smartpos.dto.CommonFileDTO;
import uz.uzkassa.smartpos.dto.product.AttributeValue;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch}
 */
public record ProductCreateDto(
        @NotBlank String name,
        @Size(max = 20) String barcode,
        @NotBlank String vatBarcode,
        @Size(max = 14) String committentTin,
        @Size(max = 1000) String description,
        BigDecimal vatRate,
        boolean hasMark,
        boolean favorite,
        @NotNull Long unitId,
        @NotNull Long categoryId,
        @NotNull BigDecimal salesPrice,
        List<ProductUnitDTO> units,
        MxikPackage defaultPackage,
        Short origin,
        HashSet<AttributeValue> attributes,
        CommonFileDTO image) implements Serializable {
}
