package uz.uzkassa.horeca.dto.supplier;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.constants.Constants;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierDTO extends CompanyCommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 666456543L;

    String contactName;

    @Pattern(regexp = Constants.PHONE_REGEX)
    String contactPhone;
}
