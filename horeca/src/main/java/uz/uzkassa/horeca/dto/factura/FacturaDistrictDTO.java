package uz.uzkassa.horeca.dto.factura;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDistrictDTO implements Serializable {

    private String districtId;
    private Long regionId;
    private Long districtCode;
    private String name;
}
