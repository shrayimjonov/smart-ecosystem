package uz.uzkassa.smartpossupply.dto.stockadjustment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentAggregationCodesDTO implements Serializable {

    String vatBarcode;

    List<String> codes;
}
