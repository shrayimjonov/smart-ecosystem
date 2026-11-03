package uz.uzkassa.smartpossupply.dto.stock;

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
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.dto.product.BlockProductDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockProductLookUpDTO extends CommonDTO {

    static final ObjectMapper objectMapper = new ObjectMapper();

    BigDecimal qtyInWarehouse;

    BigDecimal reservationQty;

    BigDecimal aggregationOrMarkQty;

    BigDecimal qtyInPackage;
    BigDecimal qtyInBox;
    BigDecimal qtyInPallet;

    BigDecimal purchasePrice;

    BigDecimal price;

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    Vat vat;

    String barcode;

    String vatBarcode;

    String catalogName;

    String packageName;

    String packageCode;

    String capacity;

    boolean hasMark;

    ProductType productType;

    CompanyCommittentLookUpDTO companyCommittent;

    @Deprecated
    ProductCommonDTO parent;

    String boxBarcode;

    String blockBarcode;

    List<BlockProductDTO> blockProducts;

    Integer sortOrder;

    BigDecimal exchangeRate;
    CurrencyType currencyType;
    BigDecimal foreignPrice;

    //Todo it's used on constructor query
    public StockProductLookUpDTO(
            Long id,
            String name,

            BigDecimal qtyInWarehouse,
            BigDecimal reservationQty,
            BigDecimal aggregationOrMarkQty,

            BigDecimal qtyInPackage,
            BigDecimal qtyInBox,
            BigDecimal qtyInPallet,

            BigDecimal purchasePrice,
            BigDecimal price,

            BigDecimal exciseRate,
            BigDecimal exciseSum,

            String vatCode,
            String barcode,
            String vatBarcode,
            String catalogName,
            String packageName,
            String packageCode,

            String capacity,
            boolean hasMark,

            String productType,
            String blockProducts,

            Integer sortOrder,

            Long companyCommittentId,
            Long committentId,
            String committentName,
            String committentTin,

            Long parentId,
            String parentName,
            String parentBarcode,
            String parentVatBarcode,
            String boxBarcode,
            String blockBarcode,

            BigDecimal exchangeRate,
            String currencyType,
            BigDecimal foreignPrice

    ) {
        super(id, name);
        this.qtyInWarehouse = qtyInWarehouse;
        this.reservationQty = reservationQty;
        this.aggregationOrMarkQty = aggregationOrMarkQty;

        this.qtyInPackage = qtyInPackage;
        this.qtyInBox = qtyInBox;
        this.qtyInPallet = qtyInPallet;

        this.purchasePrice = purchasePrice;
        this.price = price;

        this.exciseRate = exciseRate;
        this.exciseSum = exciseSum;

        this.vat = Vat.valueOf(vatCode);
        this.barcode = barcode;
        this.vatBarcode = vatBarcode;
        this.catalogName = catalogName;
        this.packageName = packageName;
        this.packageCode = packageCode;

        this.capacity = capacity;
        this.hasMark = hasMark;

        this.boxBarcode = boxBarcode;
        this.blockBarcode = blockBarcode;
        this.productType = productType != null ? ProductType.valueOf(productType) : null;
        if (companyCommittentId != null) {
            this.companyCommittent = new CompanyCommittentLookUpDTO(companyCommittentId, committentId, committentName, committentTin);
        }
        if (parentId != null) {
            this.parent = new ProductCommonDTO(parentId, parentName, parentBarcode, parentVatBarcode);
            if (StringUtils.isEmpty(blockBarcode)) {
                this.blockBarcode = blockBarcode;
            }
        }
        if (blockProducts != null) {
            try {
                this.blockProducts = objectMapper.readValue(blockProducts, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                this.blockProducts = null;
            }
        }
        this.sortOrder = sortOrder;

        this.exchangeRate = exchangeRate;
        this.currencyType = currencyType != null ? CurrencyType.valueOf(currencyType) : null;
        this.foreignPrice = foreignPrice;
    }
}
