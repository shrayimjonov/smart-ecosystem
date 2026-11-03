package uz.uzkassa.smartpossupply.dto.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductBarcodeDTO implements Serializable {

    String barcode;
}
