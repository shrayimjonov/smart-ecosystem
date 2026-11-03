package uz.uzkassa.cts.dto.customerbranchdevicechangetariff;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerBranchDeviceTariffDto {
    private Long customerId;
    private String tin;
    private String deviceSerialNumber;
    private Long deviceId;
    private Long tariffId;
    private String tariffName;

    public CustomerBranchDeviceTariffDto(Long customerId, String tin, String deviceSerialNumber, Long deviceId, Long tariffId, String tariffName) {
        this.customerId = customerId;
        this.tin = tin;
        this.deviceSerialNumber = deviceSerialNumber;
        this.deviceId = deviceId;
        this.tariffId = tariffId;
        this.tariffName = tariffName;
    }
}
