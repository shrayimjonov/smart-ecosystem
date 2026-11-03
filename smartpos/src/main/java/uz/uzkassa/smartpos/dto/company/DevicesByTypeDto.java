package uz.uzkassa.smartpos.dto.company;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.DeviceType;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DevicesByTypeDto {
    Map<DeviceType, List<String>> devices;
}
