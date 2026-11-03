package uz.uzkassa.horeca.dto.company;

import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.constants.Constants;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCommonDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3444429L;

    @Pattern(regexp = Constants.TIN_REGEX)
    String tin;

    @Pattern(regexp = Constants.PINFL_REGEX)
    String pinfl;

    public CompanyCommonDTO(Long id, String name, String tin) {
        super(id, name);
        this.tin = tin;
    }

    public CompanyCommonDTO(Long id, String name, String tin, String pinfl) {
        this(id, name, tin);
        this.pinfl = pinfl;
    }
}
