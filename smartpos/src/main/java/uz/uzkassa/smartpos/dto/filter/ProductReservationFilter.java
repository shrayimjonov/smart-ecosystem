package uz.uzkassa.smartpos.dto.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ProductReservationFilter extends PrimaryFilter {
    private Long companyId;
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;
    private LocalDateTime expireDate;
    private Long productItemId;
    private Long productId;
    private ReservationStatus status;
}
