package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;
import uz.uzkassa.smartpos.enums.DeviceType;

@Getter
@Setter
public class AdvertisementFilter extends PrimaryFilter {
    private Long companyId;
    private DeviceType deviceType;
}
