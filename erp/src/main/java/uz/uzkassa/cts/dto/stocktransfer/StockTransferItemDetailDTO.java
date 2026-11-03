package uz.uzkassa.cts.dto.stocktransfer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.stock.StockItemDTO;
import uz.uzkassa.cts.dto.stock.StockItemProductVariationDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferItemDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4353453L;

    Long id;

    StockItemDTO item;

    CommonDTO unit;

    BigDecimal qty;

    BigDecimal price;

    List<StockItemProductVariationDTO> productVariations = new ArrayList<>();

    @JsonIgnore
    public void addProductVariation(StockItemProductVariationDTO stockItemProductVariationDTO) {
        productVariations.add(stockItemProductVariationDTO);
    }
}
