package uz.uzkassa.horeca.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.address.AddressDTO;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyDTO extends CompanyCommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 54634454L;

    String businessType;

    Long activityTypeId;

    String oked;

    String phone;

    AddressDTO address;

    Long ownerId;

    String wifiPassword;
}
