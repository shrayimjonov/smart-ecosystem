package uz.uzkassa.smartpossupply.dto.stockin.onec;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDateDTO;
import uz.uzkassa.smartpossupply.dto.productcontract.ProductContractLookupDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductBaseDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockInProduct1CDTO extends StockProductBaseDTO {

    String stockProductName;

    CommonDTO category;

    UnitDTO unit;

    CommonDateDTO serial;

    ProductContractLookupDTO productContract;

    BigDecimal facturaProfitRate;

    String committentTin;

    List<String> markCodes = new ArrayList<>(); //KIZ

    List<String> aggregationCodes = new ArrayList<>(); //IdentTransUpak

    List<String> aggregationBlocks = new ArrayList<>(); //NomUpak
}
