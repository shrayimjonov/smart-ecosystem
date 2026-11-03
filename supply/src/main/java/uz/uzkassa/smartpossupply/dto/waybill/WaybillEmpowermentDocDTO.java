package uz.uzkassa.smartpossupply.dto.waybill;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WaybillEmpowermentDocDTO implements Serializable {

    String empowermentNo;
    LocalDateTime empowermentDateOfIssue;
    LocalDateTime empowermentDateOfExpire;
}
