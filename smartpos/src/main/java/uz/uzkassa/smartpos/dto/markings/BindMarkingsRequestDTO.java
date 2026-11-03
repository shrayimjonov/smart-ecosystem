package uz.uzkassa.smartpos.dto.markings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BindMarkingsRequestDTO {
    private Long productId;
    private Long branchId;
}
