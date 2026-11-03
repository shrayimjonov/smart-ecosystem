package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.TimeInterval;
import uz.uzkassa.smartpos.enums.TransactionOperation;
import uz.uzkassa.smartpos.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class TransactionFilter extends PrimaryFilter {
    private Long companyId;
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;
    private Long userId;
    private String deviceSN;
    private TransactionStatus status;
    private PaymentType paymentType;
    private TransactionOperation operation;
    private LocalDateTime from;
    private LocalDateTime to;
    private TimeInterval timeInterval = TimeInterval.DAY;
}
