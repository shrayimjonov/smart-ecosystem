package uz.uzkassa.smartpos.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import uz.uzkassa.smartpos.domain.postgres.catalog.CreditSettings;
import uz.uzkassa.smartpos.dto.CategoryDTO;
import uz.uzkassa.smartpos.dto.CommonFileDTO;
import uz.uzkassa.smartpos.dto.UnitDTO;
import uz.uzkassa.smartpos.dto.product.AttributeValue;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductDTO implements Serializable {
    private Long id;
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    @Size(max = 255)
    private String barcode;
    @NotNull
    @Size(min = 1, max = 255)
    private String vatBarcode;
    @Size(max = 14)
    private String committentTin;
    @Size(max = 1000)
    private String description;

    private BigDecimal vatRate;
    private boolean hasMark;
    private boolean hasExcise;
    private boolean favorite;
    private BigDecimal exciseAmount;
    private UnitDTO unit;
    @NotNull
    private CategoryDTO categoryDTO;
    private BigDecimal salesPrice;
    private Long branchId;

    private boolean deleted;
    private List<ProductUnitDTO> units;
    private BigDecimal amount;
    private boolean creditEnabled;
    private CreditSettings creditSettings;
    private boolean advanceEnabled;
    private CreditSettings advanceSettings;
    private MxikPackage defaultPackage;
    private Short origin;
    private HashSet<AttributeValue> attributes;
    private CommonFileDTO image;

    public void setUnits(List<ProductUnitDTO> units) {
        if (CollectionUtils.isNotEmpty(units)) {
            this.units = units;
            this.units.forEach(productUnitDTO -> productUnitDTO
                    .setPrice(salesPrice.multiply(BigDecimal.valueOf(Optional.ofNullable(productUnitDTO.getCoefficient()).orElse(1D)))));
        }
    }

    public List<ProductUnitDTO> getUnits() {
        if (units == null) {
            units = new ArrayList<>();
        }
        return units;
    }

    public ProductDTO(Long id, @NotBlank String name, @NotBlank String barcode) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDTO that = (ProductDTO) o;
        return Objects.equals(name.toLowerCase(), that.name.toLowerCase()) && Objects.equals(barcode.toLowerCase(), that.barcode.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), barcode.toLowerCase());
    }
}
