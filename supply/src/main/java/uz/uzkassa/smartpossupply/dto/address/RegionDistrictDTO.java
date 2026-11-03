package uz.uzkassa.smartpossupply.dto.address;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegionDistrictDTO extends CommonCodeDTO {

    String mapValue;

    String nameUz;

    public RegionDistrictDTO(Long id, String name, String code, String mapValue,String nameUz) {
        super(id, name, code);
        this.mapValue = mapValue;
        this.nameUz=nameUz;
    }
}
