package uz.uzkassa.horeca.dto.category;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CatalogDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9123991211L;

    String code;

    String name;

    String barcode;
}
