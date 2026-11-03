package uz.uzkassa.smartpossupply.dto.waybill;


import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WaybillCompanyEmpowermentsDTO implements Serializable {
    WaybillEmpowermentDTO[] rows;
    Integer totalCount;
}
