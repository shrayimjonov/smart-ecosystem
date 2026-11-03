package uz.uzkassa.smartpossupply.dto.stocktransfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalStockTransferProductDetailDTO {

    Long stockTransferProductId;

    Long id; //productId

    String name; //product name

    String vatBarCode; //IKPU

    String barcode; //Shtrix code

    BigDecimal qtyInPackage;
    BigDecimal qtyInBox;
    BigDecimal qtyInPallet;

    BigDecimal qty; //soni

    UnitDTO unit;

    Integer aggregationQty;

    Integer markQty;

    Integer aggregationBlockQty;

    BigDecimal qtyInWarehouse;

    BigDecimal reservationQty;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    BigDecimal totalBlockQty;
}
