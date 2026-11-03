package uz.uzkassa.cts.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.StockItemType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class StockItemBaseDTO implements Serializable {

    Long id;

    Long itemId;

    StockItemType itemType;

    List<Long> variationOptionIds;

    Long unitId;

    BigDecimal qty;
}
