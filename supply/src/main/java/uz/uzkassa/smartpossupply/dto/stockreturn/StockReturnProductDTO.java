package uz.uzkassa.smartpossupply.dto.stockreturn;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.stock.StockProductBaseDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 09.03.2023 17:23
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReturnProductDTO extends StockProductBaseDTO {

    Long productContractId;

    Long productSerialId;

    BigDecimal facturaProfitRate;

    List<String> markCodes = new ArrayList<>();

    List<String> aggregationCodes = new ArrayList<>();

    List<String> aggregationBlocks = new ArrayList<>();
}
