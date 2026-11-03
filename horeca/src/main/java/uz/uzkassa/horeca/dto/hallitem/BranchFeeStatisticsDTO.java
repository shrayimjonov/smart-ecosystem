package uz.uzkassa.horeca.dto.hallitem;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
public class BranchFeeStatisticsDTO {
    Long branchId;

    Float maxFee;

    Float minFee;

    public BranchFeeStatisticsDTO(Long branchId, Float minFee, Float maxFee) {
        this.branchId = branchId;
        this.maxFee = maxFee;
        this.minFee = minFee;
    }
}
