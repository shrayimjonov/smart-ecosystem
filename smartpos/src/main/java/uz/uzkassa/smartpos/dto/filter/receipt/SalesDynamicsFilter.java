package uz.uzkassa.smartpos.dto.filter.receipt;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.TimeInterval;

@Getter
@Setter
public class SalesDynamicsFilter extends SalesStatsFilter {
    private TimeInterval granularity = TimeInterval.HOUR;
}
