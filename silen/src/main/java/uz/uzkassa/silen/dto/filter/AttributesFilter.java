package uz.uzkassa.silen.dto.filter;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.ProductType;

import java.util.Set;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributesFilter extends Filter {

    String parentId;

    Set<ProductType> productTypes;

    ProductType productType;

    String name;
}
