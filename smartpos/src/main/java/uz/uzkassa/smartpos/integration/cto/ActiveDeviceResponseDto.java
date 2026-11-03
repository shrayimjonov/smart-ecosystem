package uz.uzkassa.smartpos.integration.cto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveDeviceResponseDto {
    private String tin;
    private String deviceType;
    private Integer count;
}