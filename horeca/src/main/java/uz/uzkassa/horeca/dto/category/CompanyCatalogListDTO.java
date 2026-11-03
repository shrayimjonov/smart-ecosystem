package uz.uzkassa.horeca.dto.category;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCatalogListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6688475L;

    String id;

    String catalogCode;

    String catalogName;

    String barcode;

    String packageCode;

    String packageName;
}
