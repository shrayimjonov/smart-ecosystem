package uz.uzkassa.horeca.dto.sync;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.FullNameDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class CompanyOwnerSyncDTO extends FullNameDTO implements Serializable {

    String phone;

    String username;
}
