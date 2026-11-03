package uz.uzkassa.cts.dto.cases;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseSmartposAppTypeChange {
    private Long ctoId;
    private String tin;
    private String pinfl;
    private String billingType;

    public CaseSmartposAppTypeChange(Long ctoId, String tin, String pinfl, String billingType) {
        this.ctoId = ctoId;
        this.pinfl = pinfl;
        this.tin = tin;
        this.billingType = billingType;
    }
}
