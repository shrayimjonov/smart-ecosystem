package uz.uzkassa.smartpossupply.dto.waybill;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WaybillEmpowermentDTO implements Serializable {
    String empowermentId;
    WaybillEmpowermentDocDTO empowermentDoc;

}
