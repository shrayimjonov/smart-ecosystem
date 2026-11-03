package uz.uzkassa.smartpos.dto.integration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.enums.BillingType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class BillingTypeSyncDTO {
    private Long ctoId;
    private String tin;
    private String pinfl;
    private BillingType billingType;

    public List<String> getTins() {
        ArrayList<String> tins = new ArrayList<>();
        if (getTin() != null) {
            tins.add(getTin());
        }
        if (getPinfl() != null) {
            tins.add(getPinfl());
        }
        return tins;
    }
}
