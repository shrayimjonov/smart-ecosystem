package uz.uzkassa.smartpossupply.dto.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductLookUpWithWarehouseQtyDTO extends CommonDTO {

    static final ObjectMapper objectMapper = new ObjectMapper();

    String barcode;

    Vat vat;

    UnitDTO unit;

    BigDecimal purchasePrice;

    BigDecimal price;

    BigDecimal foreignPrice;

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    BigDecimal qtyInPackage;
    BigDecimal qtyInBox;
    BigDecimal qtyInPallet;

    BigDecimal qtyInWarehouse;

    BigDecimal reservationQty;

    @Deprecated
    ProductCommonDTO parent;

    ProductType productType;

    boolean hasMark;

    BigDecimal brutto;

    BigDecimal netto;

    String blockBarcode;

    String boxBarcode;

    List<BlockProductDTO> blockProducts = new ArrayList<>();

    boolean favourite;

    //It's used on constructor query
    @SuppressWarnings("unused")
    public ProductLookUpWithWarehouseQtyDTO(
            Long id,
            String name,
            String barcode,
            String vat,
            boolean favourite,
            Long unitId,
            String unitCode,
            String unitName,
            Long measureId,
            BigDecimal purchasePrice,
            BigDecimal price,
            BigDecimal foreignPrice,
            BigDecimal exciseRate,
            BigDecimal exciseSum,
            BigDecimal qtyInWarehouse,
            BigDecimal reservationQty,
            Long parentId,
            String parentName,
            String parentBarcode,
            String parentVatBarcode,
            String productType,
            Boolean hasMark,
            BigDecimal qtyInPackage,
            BigDecimal qtyInBox,
            BigDecimal qtyInPallet,
            BigDecimal brutto,
            BigDecimal netto,
            String boxBarcode,
            String blockBarcode,
            String blockProducts
    ) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.vat = Vat.valueOf(vat);
        this.favourite = favourite;
        this.unit = new UnitDTO(unitId, unitName, unitCode, measureId);
        this.purchasePrice = purchasePrice;
        this.price = price;
        this.foreignPrice = foreignPrice;
        this.exciseRate = exciseRate;
        this.exciseSum = exciseSum;
        this.qtyInWarehouse = qtyInWarehouse;
        this.reservationQty = reservationQty;
        this.boxBarcode = boxBarcode;
        this.blockBarcode = blockBarcode;
        if (parentId != null) {
            this.parent = new ProductCommonDTO(parentId, parentName, parentBarcode, parentVatBarcode);
            if (StringUtils.isEmpty(this.blockBarcode)) {
                this.blockBarcode = parentBarcode;
            }
        }
        if (StringUtils.isNotEmpty(productType)) {
            this.productType = ProductType.valueOf(productType);
        }
        this.hasMark = hasMark;
        this.qtyInPackage = qtyInPackage;
        this.qtyInBox = qtyInBox;
        this.qtyInPallet = qtyInPallet;
        this.brutto = brutto;
        this.netto = netto;
        if (blockProducts != null) {
            try {
                this.blockProducts = objectMapper.readValue(blockProducts, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                this.blockProducts = null;
            }
        }
    }
}
