package uz.uzkassa.cts.dto.address;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.enums.AddressType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressListDTO extends BaseAddressDetailDTO implements Serializable {

    Long id;

    AddressType type;
}
