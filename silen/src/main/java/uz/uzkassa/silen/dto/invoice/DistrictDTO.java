package uz.uzkassa.silen.dto.invoice;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DistrictDTO implements Serializable {
    private String soato;
    private String districtId;
    private Long regionId;
    private Long districtCode;
    private String name;
    private String nameUzLatn;
    private String nameUzCyrl;
    private String nameRu;
}
