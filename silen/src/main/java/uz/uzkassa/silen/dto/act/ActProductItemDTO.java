package uz.uzkassa.silen.dto.act;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActProductItemDTO implements Serializable {
    String id;

    String name;

    String barcode;

    String packageName;
}
