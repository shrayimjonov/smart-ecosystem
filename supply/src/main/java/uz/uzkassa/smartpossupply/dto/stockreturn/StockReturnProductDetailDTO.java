package uz.uzkassa.smartpossupply.dto.stockreturn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialLookUpDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductBaseDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReturnProductDetailDTO extends StockProductBaseDTO {

    BigDecimal facturaProfitRate;

    CommonDTO productContract;

    ProductSerialLookUpDTO productSerial;

    UnitDTO unit;

    boolean hasMarking;

    Integer aggregationQty;

    Integer markQty;

    Integer aggregationBlockQty;

    @Deprecated
    ProductCommonDTO parent;

    String boxBarcode;

    String blockBarcode;

    BigDecimal qtyInWarehouse;

    BigDecimal qtyInWarehouseBySerial;

    BigDecimal qtyInWarehouseByContract;
}
