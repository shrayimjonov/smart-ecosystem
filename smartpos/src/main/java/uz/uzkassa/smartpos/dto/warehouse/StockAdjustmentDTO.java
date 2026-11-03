package uz.uzkassa.smartpos.dto.warehouse;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.BaseAuditDTO;
import uz.uzkassa.smartpos.dto.FullNameDTO;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.enums.StockAdjustmentReason;

import java.util.List;

@Getter
@Setter
public class StockAdjustmentDTO extends BaseAuditDTO {
    private StockAdjustmentReason reason;
    private FullNameDTO fullName;
    private String description;
    private SelectItem branch;
    private List<StockAdjustmentItemDTO> stockAdjustmentItems;
}
