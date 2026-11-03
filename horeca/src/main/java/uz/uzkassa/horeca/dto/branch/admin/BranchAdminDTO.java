package uz.uzkassa.horeca.dto.branch.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.address.AddressDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class BranchAdminDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8394898394L;

    AddressDTO address;

    Long ownerId;

    Long companyId;
}
