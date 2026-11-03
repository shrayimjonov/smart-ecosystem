package uz.uzkassa.silen.dto.waybill;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmpowermentDocDTO implements Serializable {

    String empowermentNo;
    LocalDateTime empowermentDateOfIssue;
    LocalDateTime empowermentDateOfExpire;
}
