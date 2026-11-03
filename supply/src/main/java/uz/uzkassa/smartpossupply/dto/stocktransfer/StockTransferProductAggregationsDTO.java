package uz.uzkassa.smartpossupply.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferProductAggregationsDTO implements Serializable {

    BigDecimal transferCount = BigDecimal.ZERO;

    List<String> transferAggregations = new ArrayList<>();

    BigDecimal saleCount = BigDecimal.ZERO;

    List<String> saleAggregations = new ArrayList<>();

    BigDecimal residueCount = BigDecimal.ZERO;

    List<String> residueAggregations = new ArrayList<>();
}
