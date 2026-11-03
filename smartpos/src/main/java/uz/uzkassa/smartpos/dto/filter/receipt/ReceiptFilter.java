package uz.uzkassa.smartpos.dto.filter.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.enums.Source;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class ReceiptFilter extends BaseFilter {
    private Long customerId;
    private Long userId;
    private Long terminalId;
    private Long activityTypeId;
    private ReceiptStatus status;
    private Source source;
    private List<Source> sources;
    private EnumSet<ReceiptStatus> statuses = EnumSet.of(ReceiptStatus.PAID, ReceiptStatus.RETURNED);
    private EnumSet<PaymentType> paymentTypes;
    private List<String> uidList;
    private Boolean isFiscal;
    private BigDecimal totalCostFrom;
    private BigDecimal totalCostTo;
    private Long promotionId;
    private String promotionStatus;
    private List<String> terminalVersions;
    private Long position;

    private Long shiftNumber;

    @Getter(AccessLevel.NONE)
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;

    public Set<Long> getBranchGroupIds() {
        if (branchGroupIds == null) branchGroupIds = new HashSet<>();
        return branchGroupIds;
    }

    public Set<Long> getBranchIdsWithBranchId() {
        if (branchIds == null) branchIds = new HashSet<>();
        if (branchId != null) branchIds.add(branchId);
        return branchIds;
    }
}
