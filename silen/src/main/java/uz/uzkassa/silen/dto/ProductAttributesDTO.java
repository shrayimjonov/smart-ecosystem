package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductAttributesDTO {

    String productId;

    Long attributeId;

    String attributeValue;

    AttributesListDTO attributes;
}
