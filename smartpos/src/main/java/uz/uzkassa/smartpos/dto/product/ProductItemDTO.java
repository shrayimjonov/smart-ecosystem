package uz.uzkassa.smartpos.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemDTO implements Serializable {
    private Long id;
    private boolean deleted;
    private Long colorGroupId;
    private String colorGroupName;
    private String colorGroupCode;
    private String colorGroupPrintName;
    private Long productId;
    private Long branchId;
    private String name;
    private String barcode;
    private BigDecimal salesPrice;
    private HashSet<AttributeValue> attributes;
    private String image;
    private String sizeKey;
    private String sizeCode;
    private String sizeValue;
    private Integer sorder;

}
