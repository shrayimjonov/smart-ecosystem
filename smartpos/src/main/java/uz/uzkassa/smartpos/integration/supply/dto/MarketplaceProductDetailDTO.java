package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.dto.base.ClickhouseCommonDTO;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceProductDetailDTO {
    String id;
    String name;
    String customName;
    String brand;
    String description;
    String barcode;
    String catalogCode;
    ClickhouseCommonDTO category;
    CommonDTO unit;
    BigDecimal price;
    Long supplierCount;
    Long supplyProductId;
    List<ProductReferenceDTO> references = new ArrayList<>();
    List<MarketplacePhotoDTO> photos = new ArrayList<>();

}
