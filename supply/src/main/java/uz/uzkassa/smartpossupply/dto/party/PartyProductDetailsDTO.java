package uz.uzkassa.smartpossupply.dto.party;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.product.ProductFullDetailDTO;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartyProductDetailsDTO implements Serializable {
    Long id;

    ProductFullDetailDTO product;

    ProductFullDetailDTO childProduct;

    Set<String> aggregations = new LinkedHashSet<>();

    Set<String> blocks = new LinkedHashSet<>();

    Set<String> marks = new LinkedHashSet<>();
}
