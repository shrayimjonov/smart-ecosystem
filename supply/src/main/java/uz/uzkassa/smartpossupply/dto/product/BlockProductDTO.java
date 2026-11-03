package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlockProductDTO implements Serializable {
    String barcode;

    BigDecimal qtyInBlock;

    String packageCode;

    String packageName;
}