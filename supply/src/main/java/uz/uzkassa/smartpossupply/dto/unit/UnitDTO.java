package uz.uzkassa.smartpossupply.dto.unit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnitDTO extends CommonCodeDTO {

    Long measureId;

    public UnitDTO(Long id, String name, String code, Long measureId) {
        super(id, name, code);
        this.measureId = measureId;
    }
}
