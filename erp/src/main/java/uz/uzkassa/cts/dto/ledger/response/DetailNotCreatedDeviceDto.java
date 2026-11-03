package uz.uzkassa.cts.dto.ledger.response;

import lombok.Data;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.DeviceSuspendStatus;

import java.io.Serializable;

@Data
public class DetailNotCreatedDeviceDto implements Serializable {
    private Long count;
    private DeviceStatus status;
    private DeviceSuspendStatus suspendStatus;
    private Long tariffId;
    private Boolean serviceAllowed;
    private Boolean freeTrailPeriod;
    private DeviceStatus fiscalStatus;


    public DetailNotCreatedDeviceDto(Long count, DeviceStatus status, DeviceSuspendStatus suspendStatus,
                                     Long tariffId, Boolean serviceAllowed,
                                     Boolean freeTrailPeriod, DeviceStatus fiscalStatus) {
        this.count = count;
        this.status = status;
        this.suspendStatus = suspendStatus;
        this.tariffId = tariffId;
        this.serviceAllowed = serviceAllowed;
        this.freeTrailPeriod = freeTrailPeriod;
        this.fiscalStatus = fiscalStatus;
    }

    public DetailNotCreatedDeviceDto() {
    }
}
