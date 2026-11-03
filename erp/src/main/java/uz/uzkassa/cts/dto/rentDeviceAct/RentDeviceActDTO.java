package uz.uzkassa.cts.dto.rentDeviceAct;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.time.LocalDate;

@Getter
@Setter
public class RentDeviceActDTO extends RentDeviceActListDTO {
    private Long id;
    private String deviceSerialNumber;
    private Long accessoryId;
    private String accessoryName;
    private String modelName;
    private String modelDescription;
    private LocalDate dueDate;
    private LocalDate startDate;
    private Long customerId;
    private String number;

    public String getNumber() {
        return PrefixEnum.ADR + "/" + this.getId();
    }
}
