package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.ProductType;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExciseFilter extends Filter {

    ProductType productType;
}
