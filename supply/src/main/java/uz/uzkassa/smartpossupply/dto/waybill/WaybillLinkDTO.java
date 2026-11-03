package uz.uzkassa.smartpossupply.dto.waybill;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.WaybillStatus;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.12.2024
 */
@Getter
@Setter
@NoArgsConstructor
public class WaybillLinkDTO implements Serializable {

    Long id;

    String waybillNo;

    String waybillLocalId;

    WaybillStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate waybillDate;


    public WaybillLinkDTO(Long id, String waybillNo, String waybillLocalId, WaybillStatus status, LocalDate waybillDate) {
        this.id = id;
        this.waybillNo = waybillNo;
        this.waybillLocalId = waybillLocalId;
        this.status = status;
        this.waybillDate = waybillDate;
    }
}
