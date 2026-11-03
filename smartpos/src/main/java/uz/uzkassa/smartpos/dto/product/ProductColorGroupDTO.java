package uz.uzkassa.smartpos.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ProductColorGroupDTO implements Serializable {
    private Long id;
    private Long productId;
    private String colorCode;
    private String colorName;
    private String printName;
    private String sizeKey;
    private List<ColorGroupImageDTO> images;
    private List<ProductItemDTO> productItems;
}
