package uz.uzkassa.smartpossupply.dto.product;

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
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductListForOfflineDTO extends CommonDTO {

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

    List<ProductPhotoListDTO> photos = new ArrayList<>();

    String capacity;

    List<Long> categoryIds;

    List<Long> warehouseIds;

    List<Long> branchIds;

    @SuppressWarnings("unused")
    public ProductListForOfflineDTO(
            Long id,
            String name,
            String barcode,
            String vat,
            Boolean favourite,
            Long unitId,
            String unitCode,
            String unitName,
            Long measureId,
            BigDecimal purchasePrice,
            BigDecimal price,
            BigDecimal foreignPrice,
            BigDecimal exciseRate,
            BigDecimal exciseSum,
            BigDecimal qtyInPackage,
            BigDecimal qtyInBox,
            BigDecimal qtyInPallet,
            BigDecimal qtyInWarehouse,
            BigDecimal reservationQty,
            String productType,
            Boolean hasMark,
            BigDecimal brutto,
            BigDecimal netto,
            String blockBarcode,
            String boxBarcode,
            String blockProducts,
            String capacity,
            String categoryIds,
            String warehouseIds,
            String branchIds
    ) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.vat = vat != null ? Vat.valueOf(vat) : Vat.WITHOUT_VAT;
        this.favourite = favourite != null && favourite;
        this.unit = new UnitDTO(unitId, unitName, unitCode, measureId);
        this.purchasePrice = purchasePrice;
        this.price = price;
        this.foreignPrice = foreignPrice;
        this.exciseRate = exciseRate;
        this.exciseSum = exciseSum;
        this.qtyInPackage = qtyInPackage;
        this.qtyInBox = qtyInBox;
        this.qtyInPallet = qtyInPallet;
        this.qtyInWarehouse = qtyInWarehouse;
        this.reservationQty = reservationQty;
        this.productType = productType != null ? ProductType.valueOf(productType) : null;
        this.hasMark = hasMark != null && hasMark;
        this.brutto = brutto;
        this.netto = netto;
        this.blockBarcode = blockBarcode;
        this.boxBarcode = boxBarcode;
        this.capacity = capacity;

        this.categoryIds = parsePgArray(categoryIds);
        this.warehouseIds = parsePgArray(warehouseIds);
        this.branchIds = parsePgArray(branchIds);
    }

    private static List<Long> parsePgArray(String arrayString) {
        if (StringUtils.isBlank(arrayString)) return List.of();
        return Arrays.stream(arrayString.replaceAll("[{}]", "").split(","))
                .map(String::trim)
                .filter(string -> !string.isBlank() && !"NULL".equalsIgnoreCase(string))
                .map(string -> string.replaceAll("\"", ""))
                .filter(StringUtils::isNumeric)
                .map(Long::valueOf)
                .toList();
    }

}
