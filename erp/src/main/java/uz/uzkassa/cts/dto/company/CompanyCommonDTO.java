package uz.uzkassa.cts.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@NoArgsConstructor
public class CompanyCommonDTO extends CommonDTO {

    String tin;

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
