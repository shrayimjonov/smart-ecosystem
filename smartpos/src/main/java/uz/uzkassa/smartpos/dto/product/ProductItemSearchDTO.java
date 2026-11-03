package uz.uzkassa.smartpos.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductItem;
import uz.uzkassa.smartpos.dto.catalog.ProductDTO;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemSearchDTO implements Serializable {
    private Long id;
    private Long branchId;
    private String name;
    private String barcode;
    private String colorCode;
    private String colorName;
    private String printName;
    private String imageUrl;
    private String sizeName;
    private BigDecimal salesPrice;
    private ProductDTO product;
    private BigDecimal stockAmount;

    public ProductItemSearchDTO(ProductItem productItem) {
        this.id = productItem.getGenericId();
        this.branchId = productItem.getBranchId();
        this.name = productItem.getName();
        this.barcode = productItem.getBarcode();
        if (productItem.getColorGroup() != null) {
            this.colorCode = productItem.getColorGroup().getColorCode();
            this.colorName = productItem.getColorGroup().getColorName();
            this.printName = productItem.getColorGroup().getPrintName();
        }
        this.sizeName = productItem.getSizeValue();
    }

    public ProductItemSearchDTO(ProductBranch productBranch) {
        this.product = productBranch.getDTO(false);
    }
}
