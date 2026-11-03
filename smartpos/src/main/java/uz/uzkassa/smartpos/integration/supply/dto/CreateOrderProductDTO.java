package uz.uzkassa.smartpos.integration.supply.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderProductDTO {
    Long id;
    @NotNull
    Long orderProductId;
    @NotNull
    String name;
    @NotNull
    String barcode;
    @NotNull
    String vatBarcode;
    @NotNull
    boolean hasMark;
    @NotNull
    Long unitId;
    @NotNull
    String unitName;
    @NotNull
    String unitCode;
    @NotNull
    BigDecimal qty;
    @NotNull
    BigDecimal price;
    BigDecimal vatRate;
    ArrayList<MarketplacePhotoDTO> photos = new ArrayList<>();
}
