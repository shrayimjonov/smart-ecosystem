package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.Source;
import uz.uzkassa.smartpos.enums.TimeInterval;
import uz.uzkassa.smartpos.enums.sales.SalesStatus;
import uz.uzkassa.smartpos.enums.sales.SalesType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SalesFilter extends PrimaryFilter {
    private Long companyId;
    private Long branchId;
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;
    private LocalDateTime from;
    private LocalDateTime to;
    private Long userId;
    private Source source;
    private SalesType type;
    private List<SalesType> types;
    private SalesStatus status;
    private Long shiftNumber;
    private PaymentType paymentType;
    private BigDecimal totalCostFrom;
    private BigDecimal totalCostTo;
    private TimeInterval timeInterval = TimeInterval.DAY;
}
