package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.ProductType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSelectItem implements Serializable {

    String id;

    String name;

    String barcode;

    String parentBarcode;

    int qtyInAggregation;

    int qtyInBlock;

    int expiredInMonth;

    ProductType productType;
}
