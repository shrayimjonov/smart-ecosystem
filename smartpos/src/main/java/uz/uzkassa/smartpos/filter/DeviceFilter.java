package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;
import uz.uzkassa.smartpos.enums.DeviceType;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class DeviceFilter extends PrimaryFilter {
    private Long branchId;
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;
    private Long companyId;
    private List<DeviceType> types;
}
