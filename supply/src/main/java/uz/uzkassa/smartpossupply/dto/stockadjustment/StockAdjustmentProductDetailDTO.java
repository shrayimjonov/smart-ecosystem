package uz.uzkassa.smartpossupply.dto.stockadjustment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.BlockProductDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.enums.AdjustmentReason;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.06.2023
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentProductDetailDTO extends CommonDTO {

    CommonDTO productSerial;

    String barcode;

    String vatBarcode;

    BigDecimal qty;

    BigDecimal price;

    BigDecimal foreignPrice;

    BigDecimal qtyInWarehouse;

    BigDecimal reservationQty;

    BigDecimal qtyInWarehouseBySerial;

    Long stockAdjustmentProductId;

    AdjustmentReason reason;

    String otherReason;

    @Deprecated
    ProductCommonDTO parent;

    String boxBarcode;

    String blockBarcode;

    boolean hasMark;

    boolean hasMarking;

    ProductType productType;

    List<BlockProductDTO> blockProducts = new ArrayList<>();

    List<CheckedCodeDTO> aggregations = new ArrayList<>();

    List<CheckedCodeDTO> aggregationBlocks = new ArrayList<>();

    List<CheckedCodeDTO> marks = new ArrayList<>();

    public void addAggregation(CheckedCodeDTO code) {
        this.aggregations.add(code);
    }

    public void addAggregationBlock(CheckedCodeDTO code) {
        this.aggregationBlocks.add(code);
    }

    public void addMark(CheckedCodeDTO code) {
        this.marks.add(code);
    }
}
