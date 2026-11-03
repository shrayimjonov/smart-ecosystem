package uz.uzkassa.horeca.dto.product;

import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.ProductOrigin;
import uz.uzkassa.horeca.enums.Vat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CateringServiceDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4353454L;

    Long id;

    String name;

    String packageCode;

    String packageName;

    String catalogCode;

    String catalogName;

    CommonDTO category;

    CommonDTO unit;

    @Builder.Default
    BigDecimal feePercent=BigDecimal.ZERO;

    Vat vat;

    ProductOrigin origin;

    String barcode;
}
