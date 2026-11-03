package uz.uzkassa.smartpos.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.uzkassa.smartpos.dto.catalog.ProductDTO;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemInfoDTO implements Serializable {
    private ProductDTO product;
    private List<ProductItemStockInfo> stock;
    private boolean warehouseEnabled;
}
