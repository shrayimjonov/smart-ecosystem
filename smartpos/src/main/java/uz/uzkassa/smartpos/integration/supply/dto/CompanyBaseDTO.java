package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@NoArgsConstructor
public class CompanyBaseDTO extends CommonDTO {

    String tin;

    public CompanyBaseDTO(Long id, String name, String tin) {
        super(id, name);
        this.tin = tin;
    }
}
