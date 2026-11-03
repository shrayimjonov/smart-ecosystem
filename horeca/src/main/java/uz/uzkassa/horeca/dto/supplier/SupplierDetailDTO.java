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
public class SupplierDetailDTO extends CompanyCommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 435345L;

    String contactName;

    String contactPhone;
}
