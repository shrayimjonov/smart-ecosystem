package uz.uzkassa.smartpos.integration.supply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class OrderProductDTO {
    Long orderProductId;
    Long id;
    String name;
    String barcode;
    String vatBarcode;
    boolean hasMark;
    Long unitId;
    String unitName;
    String unitCode;
    BigDecimal qty;
    BigDecimal price;
    BigDecimal vatRate;
    ArrayList<MarketplacePhotoDTO> photos = new ArrayList<>();

    @JsonProperty("totalPrice")
    public BigDecimal getTotalPrice() {
        return qty.multiply(price);
    }

}
