package uz.uzkassa.horeca.dto.branch.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.address.AddressDetailDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.dto.employee.OwnerDTO;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class BranchListAdminDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 83948982L;

    CommonStatus status;

    AddressDetailDTO address;

    OwnerDTO owner;

    CompanyCommonDTO company;
}
