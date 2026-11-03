package uz.uzkassa.cts.dto.dashboard.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DashboardStatsByRegionResponseDto implements Serializable {
    private String regionName;
    private Long regionId;
    private Long blocked;
    private Long unBlocked;
    private Long registered;
    private String tariffName;
    private Long tariffId;
}
