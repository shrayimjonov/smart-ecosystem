package uz.uzkassa.smartpos.dto.filter.receipt;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.enums.PaymentType;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CustomerReceiptsFilter extends BaseFilter implements Serializable {
    private Long regionId;
    private Long cityId;
    private Long activityTypeId;
    private Long receiptAmount;
    private Long revenue;
    private Boolean isFiscal;
    private Long revenueFrom;
    private Long revenueTo;
    private EnumSet<PaymentType> paymentTypes;
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;

    public Set<Long> getBranchIds() {
        if (branchIds == null) branchIds = new HashSet<>();
        if (branchId != null) branchIds.add(branchId);
        return branchIds;
    }

    public Set<Long> getBranchGroupIds() {
        if (branchGroupIds == null) branchGroupIds = new HashSet<>();
        return branchGroupIds;
    }
}
