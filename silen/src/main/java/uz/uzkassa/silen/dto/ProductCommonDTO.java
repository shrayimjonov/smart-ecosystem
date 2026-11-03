package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
public class ProductCommonDTO implements Serializable {

    String id;

    String name;

    String barcode;

    public ProductCommonDTO(String id, String name, String barcode) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
    }
}
