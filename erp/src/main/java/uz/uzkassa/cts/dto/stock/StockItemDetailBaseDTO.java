package uz.uzkassa.cts.dto.stock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class StockItemDetailBaseDTO implements Serializable {

    Long id;

    StockItemDTO item;

    List<StockItemProductVariationDTO> productVariations = new ArrayList<>();

    CommonDTO unit;

    BigDecimal qty;

    @JsonIgnore
    public void addProductVariation(StockItemProductVariationDTO stockItemProductVariationDTO) {
        productVariations.add(stockItemProductVariationDTO);
    }
}
