package uz.uzkassa.horeca.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.product.ProductLookUpDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferItemDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4353453L;

    Long id;

    ProductLookUpDTO fromProduct;

    ProductLookUpDTO toProduct;

    CommonDTO fromWarehouse;

    CommonDTO toWarehouse;

    CommonDTO unit;

    BigDecimal qty;
}
