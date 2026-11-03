package uz.uzkassa.smartpossupply.dto.billingpermission;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingCompanyItem extends CommonDTO {
    String tin;
    JsonNode activeBranches;
    JsonNode activeUsers;
    JsonNode activeOnlineKKMs;

    public BillingCompanyItem(Long id,
                              String name,
                              String tin,
                              JsonNode activeBranches,
                              JsonNode activeUsers,
                              JsonNode activeOnlineKKMs
    ) {
        super(id, name);
        this.tin = tin;
        this.activeBranches = activeBranches;
        this.activeUsers = activeUsers;
        this.activeOnlineKKMs = activeOnlineKKMs;
    }
}
