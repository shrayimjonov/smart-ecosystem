package uz.uzkassa.cts.dto.dashboard.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serializable;

@Getter
@Setter
public class DashboardStatistics implements Serializable {

    public DashboardStatistics(Long count, String name, Long tariffId, DeviceStatus status) {
        this.count = count;
        this.name = name;
        this.tariffId = tariffId;
        this.status = status;
    }

    private Long count;
    private String name;
    private Long tariffId;
    private DeviceStatus status;
}
