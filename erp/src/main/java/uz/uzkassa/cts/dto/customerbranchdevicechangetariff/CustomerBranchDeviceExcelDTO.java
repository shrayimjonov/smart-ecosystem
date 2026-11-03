package uz.uzkassa.cts.dto.customerbranchdevicechangetariff;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.cts.dto.device.CommonTinSerialNumberDTO;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CustomerBranchDeviceExcelDTO implements Serializable {
    private List<CommonTinSerialNumberDTO> data;
    private String paymentStatus;
    private String typeTin;

    public CustomerBranchDeviceExcelDTO(List<CommonTinSerialNumberDTO> data, String paymentStatus, String typeTin) {
        this.data = data;
        this.paymentStatus = paymentStatus;
        this.typeTin = typeTin;
    }
}
