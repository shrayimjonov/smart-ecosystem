package uz.uzkassa.cts.dto.dashboard.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serializable;

@Getter
@Setter
public class DashboardStatsByRegionDto implements Serializable {
    public DashboardStatsByRegionDto(String regionName, Long regionId, String tariffName, Long tariffId, DeviceStatus status, Long count) {
        this.regionName = regionName;
        this.regionId = regionId;
        this.tariffName = tariffName;
        this.tariffId = tariffId;
        this.status = status;
        this.count = count;
    }

    private String regionName;
    private Long regionId;
    private String tariffName;
    private Long tariffId;
    private DeviceStatus status;
    private Long count;
}
