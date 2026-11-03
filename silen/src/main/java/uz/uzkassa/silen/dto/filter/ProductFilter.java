package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ProductStatus;
import uz.uzkassa.silen.enumeration.ProductType;

import java.util.Set;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFilter extends Filter {

    ProductStatus status;

    ProductType productType;

    Set<ProductType> productTypes;

    OrganizationType organizationType;
}
