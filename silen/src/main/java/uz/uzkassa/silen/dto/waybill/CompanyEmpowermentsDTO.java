package uz.uzkassa.silen.dto.waybill;


import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEmpowermentsDTO implements Serializable {
    EmpowermentDTO[] rows;
    Integer totalCount;
}
