package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.integration.supply.dto.MarketplacePhotoDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class BasketItemDTO implements Serializable {

    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @NotNull
    private Long supplierProductId;
    @NotNull
    private Long companyId;
    @NotNull
    private Long supplyId;
    @NotBlank
    private String supplyTin;
    @NotBlank
    private String supplyName;
    private Long supplyBranchId;
    private Long warehouseId;
    @NotBlank
    private String name;
    private String barcode;
    @NotBlank
    private String vatBarcode;
    @NotNull
    private Long unitCode;
    private Long unitId;
    private String unitName;


    private ArrayList<MarketplacePhotoDTO> photos = new ArrayList<>();

    @NotNull
    private BigDecimal qty = BigDecimal.ONE;
    @NotNull
    private BigDecimal price = BigDecimal.ZERO;
    private BigDecimal vatRate;
    private boolean selected = true;

    public BigDecimal getTotalPrice() {
        return qty.multiply(price);
    }

    public BigDecimal getVatPrice() {
        return vatRate != null ? price.multiply(price).divide(BigDecimal.valueOf(100).add(vatRate), 4, RoundingMode.HALF_UP) : null;
    }

    public BigDecimal getTotalVatPrice() {
        return vatRate != null ? qty.multiply(getVatPrice()) : null;
    }
}
