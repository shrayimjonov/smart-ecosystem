package uz.uzkassa.smartpossupply.dto.company;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@NoArgsConstructor
public class CompanyBaseDTO extends CommonDTO {

    @Pattern(regexp = Constants.TIN_REGEX)
    String tin;

    String brand;

    public CompanyBaseDTO(Long id, String name, String tin) {
        super(id, name);
        this.tin = tin;
    }

    public CompanyBaseDTO(Long id, String name, String tin, String brand) {
        super(id, name);
        this.tin = tin;
        this.brand = brand;
    }
}
