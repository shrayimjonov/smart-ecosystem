package uz.uzkassa.horeca.filters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChefStatsFilter extends CoreFilter {
    Long chefId;

    Long branchId;
}
