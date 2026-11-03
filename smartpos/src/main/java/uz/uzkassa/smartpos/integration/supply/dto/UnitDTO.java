package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonCodeDTO;

@Getter
@Setter
@NoArgsConstructor
public class UnitDTO extends CommonCodeDTO {

    Long measureId;

    public UnitDTO(Long id, String name, String code, Long measureId) {
        super(id, name, code);
        this.measureId = measureId;
    }
}
