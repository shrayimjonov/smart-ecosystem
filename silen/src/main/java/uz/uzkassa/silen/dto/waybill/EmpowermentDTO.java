package uz.uzkassa.silen.dto.waybill;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmpowermentDTO implements Serializable {
    String empowermentId;
    EmpowermentDocDTO empowermentDoc;

}
