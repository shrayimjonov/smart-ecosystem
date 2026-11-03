package uz.uzkassa.horeca.dto.supplier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierListDTO extends CompanyCommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8395843L;

    String contactName;

    String contactPhone;
}
