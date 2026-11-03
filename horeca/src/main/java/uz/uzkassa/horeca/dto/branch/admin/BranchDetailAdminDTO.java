package uz.uzkassa.horeca.dto.branch.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.address.AddressDetailDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.employee.OwnerDTO;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchDetailAdminDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9367632L;

    AddressDetailDTO address;

    OwnerDTO owner;

    CommonDTO company;
}
