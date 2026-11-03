package uz.uzkassa.smartpossupply.dto.warehouseproduct;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductQtyDTO;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseProductQtyDTO implements Serializable {

    CommonDTO branch;

    CommonDTO warehouse;

    List<ProductQtyDTO> products;
}
