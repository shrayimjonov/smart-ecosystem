package uz.uzkassa.smartpossupply.dto.waybill;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WaybillDistrictDTO implements Serializable {
    private String soato;
    private String districtId;
    private Long regionId;
    private Long districtCode;
    private String name;
    private String nameUzLatn;
    private String nameUzCyrl;
    private String nameRu;
}
