package uz.uzkassa.smartpossupply.dto.waybill;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WaybillRegionDTO implements Serializable {
    private Long regionId;
    private String name;
    private String nameUzLatn;
    private String nameUzCyrl;
    private String nameRu;
    private String code;
}
