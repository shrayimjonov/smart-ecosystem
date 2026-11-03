package uz.uzkassa.smartpos.dto.catalog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonIdDto;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product1CCreateDto implements Serializable {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 20)
    private String barcode;
    @NotBlank
    private String vatBarcode;
    @Size(max = 14)
    private String committentTin;
    private BigDecimal vatRate;
    private boolean hasMark;
    private boolean favorite;
    @Valid
    private CommonIdDto unit;
    @Valid
    private CommonIdDto categoryDTO;
    @NotNull
    private BigDecimal salesPrice;
    private Long branchId;
    private List<ProductUnitDTO> units;
    private MxikPackage defaultPackage;
    private Short origin;
}
