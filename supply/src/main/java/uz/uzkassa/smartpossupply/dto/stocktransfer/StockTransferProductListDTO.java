package uz.uzkassa.smartpossupply.dto.stocktransfer;

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
import uz.uzkassa.smartpossupply.dto.product.BlockProductDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferProductListDTO extends CommonDTO {

    static final ObjectMapper objectMapper = new ObjectMapper();

    String capacity;

    BigDecimal qtyInWarehouse;

    BigDecimal reservationQty;

    String barcode;

    String boxBarcode;

    String blockBarcode;

    boolean hasMark;

    ProductType productType;

    @Deprecated
    ProductCommonDTO parent;

    BigDecimal brutto;

    BigDecimal netto;

    BigDecimal price;

    List<BlockProductDTO> blockProducts;

    @SuppressWarnings("unused")
    public StockTransferProductListDTO(
            Long id,
            String name,
            String capacity,
            BigDecimal qtyInWarehouse,
            BigDecimal reservationQty,
            String barcode,
            String boxBarcode,
            String blockBarcode,
            Boolean hasMark,
            String productType,
            Long parentId,
            String parentName,
            String parentBarcode,
            String parentVatBarcode,
            BigDecimal brutto,
            BigDecimal netto,
            BigDecimal price,
            String blockProducts
    ) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.qtyInWarehouse = qtyInWarehouse;
        this.barcode = barcode;
        this.hasMark = hasMark;
        this.productType = Optional.ofNullable(productType).map(ProductType::valueOf).orElse(null);
        this.boxBarcode = boxBarcode;
        this.blockBarcode = blockBarcode;
        if (parentId != null) {
            this.parent = new ProductCommonDTO(parentId, parentName, parentBarcode, parentVatBarcode);
            if (StringUtils.isNotEmpty(this.blockBarcode)) {
                this.blockBarcode = parentBarcode;
            }
        }
        this.reservationQty = reservationQty;
        this.brutto = Optional.ofNullable(brutto).orElse(BigDecimal.ZERO);
        this.netto = Optional.ofNullable(netto).orElse(BigDecimal.ZERO);
        this.price = Optional.ofNullable(price).orElse(BigDecimal.ZERO);
        if (blockProducts != null) {
            try {
                this.blockProducts = objectMapper.readValue(blockProducts, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                this.blockProducts = null;
            }
        }
    }
}
