package uz.uzkassa.smartpossupply.dto.supplier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierListDTO extends CompanyBaseDTO {

    AddressDetailDTO address;

    String contactPerson;

    String phone;
}
