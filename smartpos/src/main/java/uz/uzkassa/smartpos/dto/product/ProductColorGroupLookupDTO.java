package uz.uzkassa.smartpos.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProductColorGroupLookupDTO implements Serializable {
    private Long id;
    private String colorCode;
    private String colorName;
    private String printName;
}
