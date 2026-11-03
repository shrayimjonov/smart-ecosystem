package uz.uzkassa.smartpos.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.MarkingStatus;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptMarkingsPayload implements Serializable {
    private Long branchId;
    private Long productId;
    private Long unitId;
    private MarkingStatus status;
    private Set<String> marks;
    private Boolean up;
}
