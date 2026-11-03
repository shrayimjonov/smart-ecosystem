package uz.uzkassa.smartpossupply.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockInProductDTO extends StockProductBaseDTO {

    String stockProductName;

    Long categoryId;

    Long unitId;

    Long measureId;

    String serialNumber;
    Long productSerialId;

    Long productContractId;

    BigDecimal facturaProfitRate;

    String committentTin;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    List<String> markCodes = new ArrayList<>(); //KIZ

    List<String> aggregationCodes = new ArrayList<>(); //IdentTransUpak

    List<String> aggregationBlocks = new ArrayList<>(); //NomUpak

    boolean hasMarking;

    BigDecimal exciseRate;

    BigDecimal exciseSum;
}
