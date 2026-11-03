package uz.uzkassa.cts.dto.dashboard.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.DeviceStatus;

@Getter
@Setter
public class DashboardStatsByDistrictDto extends DashboardStatsByRegionDto {

    public DashboardStatsByDistrictDto(String regionName, Long regionId, String tariffName, Long tariffId, DeviceStatus status, Long count) {
        super(regionName, regionId, tariffName, tariffId, status, count);
    }

    public DashboardStatsByDistrictDto(String regionName, Long regionId, String tariffName, Long tariffId, DeviceStatus status, Long count, String districtName, Long districtId) {
        super(regionName, regionId, tariffName, tariffId, status, count);
        this.districtName = districtName;
        this.districtId = districtId;
    }

    private String districtName;
    private Long districtId;
}
