package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.dto.soliq.PackageDTO;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class TerminalProductDTO extends TerminalBaseAuditDTO implements Serializable {
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    @Size(max = 255)
    private String barcode;
    @NotNull
    @Size(min = 1, max = 255)
    private String vatBarcode;
    private String model;
    private String measurement;
    @Size(max = 255)
    private String description;
    //    @Pattern(regexp = "[0-9]+")
    @Size(max = 14)
    private String committentTin;

    private BigDecimal vatRate;
    private boolean hasMark;
    private boolean hasExcise;
    private boolean hasDelivery;
    private boolean selfPickup;
    private boolean favorite;
    private boolean noVat;

    private boolean service;
    private BigDecimal exciseAmount;
    private TerminalUnitDTO unit;
    private TerminalUnitDTO packageType;
    @NotNull
    private TerminalCategoryDTO categoryDTO;
    private boolean custom;
    private BigDecimal salesPrice;
    private Long branchId;

    private boolean deleted;
    private Long manufacturerId;
    private String manufacturingCountry;
    private String productNameUz;
    private boolean updateImage;
    private List<TerminalProductUnitDTO> units;
    private Long agreementId;
    private BigDecimal amount;
    private boolean creditEnabled;
    private boolean advanceEnabled;
    private PackageDTO defaultPackage;
    private TerminalSelectItemDTO childProduct;
    private Short origin;

    public void setUnits(List<TerminalProductUnitDTO> units) {
        if (!CollectionUtils.isEmpty(units)) {
            this.units = units;
            this.units.forEach(productUnitDTO -> productUnitDTO
                    .setPrice(salesPrice.multiply(BigDecimal.valueOf(Optional.ofNullable(productUnitDTO.getCoefficient()).orElse(1D)))));
        }
    }

    public List<TerminalProductUnitDTO> getUnits() {
        if (units == null) {
            units = new ArrayList<>();
        }
        return units;
    }

    public TerminalProductDTO(Long id, @NotBlank String name, @NotBlank String barcode) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TerminalProductDTO that = (TerminalProductDTO) o;
        return Objects.equals(name.toLowerCase(), that.name.toLowerCase()) && Objects.equals(barcode.toLowerCase(), that.barcode.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), barcode.toLowerCase());
    }
}
