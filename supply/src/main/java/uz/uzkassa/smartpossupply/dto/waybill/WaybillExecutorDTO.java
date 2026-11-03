package uz.uzkassa.smartpossupply.dto.waybill;

import lombok.*;
import uz.uzkassa.smartpossupply.enums.WaybillStatus;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WaybillExecutorDTO implements Serializable {

    private WaybillStatus status;

    private String operationNumber;

    private String operationBy;

    private LocalDateTime operationDate;
}
