package uz.uzkassa.cts.dto.productvariation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariationDTO implements Serializable {

    Long productId;

    List<Long> variationOptionIds;
}
