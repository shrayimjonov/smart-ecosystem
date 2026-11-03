package uz.uzkassa.cts.dto.address;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.enums.AddressType;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDTO extends BaseAddressDTO implements Serializable {

    Long id;

    AddressType type;

    Long entityId;

    EntityTypeEnum entityType;
}
