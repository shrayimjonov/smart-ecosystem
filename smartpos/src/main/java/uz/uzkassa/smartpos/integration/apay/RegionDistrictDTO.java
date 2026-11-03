package uz.uzkassa.smartpos.integration.apay;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class RegionDistrictDTO implements Serializable {
    String id;
    String name;
    String code;
    String mapValue;

    public RegionDistrictDTO(String code) {
        this.code = code;
    }
}
