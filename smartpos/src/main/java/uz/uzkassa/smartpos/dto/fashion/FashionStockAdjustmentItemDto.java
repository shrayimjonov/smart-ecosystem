package uz.uzkassa.smartpos.dto.fashion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.warehouse.FashionStockAdjustmentItem;
import uz.uzkassa.smartpos.dto.UserShortDto;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.enums.StockAdjustmentReason;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.fashion.FashionRegister}
 */
@Getter
@Setter
@NoArgsConstructor
public class FashionStockAdjustmentItemDto implements Serializable {
    private Long id;
    private LocalDateTime createdAt;
    private UserShortDto createdBy;
    private StockAdjustmentReason reason;
    private CommonDTO product;
    private CommonDTO branch;
    private BigDecimal qty;
    private CommonDTO unit;
    private String colorCode;
    private String colorName;
    private String printName;
    private String sizeCode;
    private String sizeValue;

    public FashionStockAdjustmentItemDto(FashionStockAdjustmentItem item) {
        this.id = item.getId();
        this.createdAt = item.getCreatedAt();
        if (item.getCreatedBy() != null) {
            this.createdBy = new UserShortDto(item.getCreatedById(), item.getCreatedBy().getLogin(), item.getCreatedBy().getFullNameStr());
        }
        this.reason = item.getReason();
        if (item.getProduct() != null) {
            this.product = item.getProduct().toSelectItem();
        }
        if (item.getBranch() != null) {
            this.branch = item.getBranch().toSelectItem();
        }
        this.qty = item.getQty();
        if (item.getUnit() != null) {
            this.unit = item.getUnit().getShortDTO();
        }
        if (item.getProductItem() != null) {
            if (item.getProductItem().getColorGroup() != null) {
                this.colorCode = item.getProductItem().getColorGroup().getColorCode();
                this.colorName = item.getProductItem().getColorGroup().getColorName();
                this.printName = item.getProductItem().getColorGroup().getPrintName();
            }
            this.sizeValue = item.getProductItem().getSizeValue();
            this.sizeCode = item.getProductItem().getSizeCode();
        }
    }
}