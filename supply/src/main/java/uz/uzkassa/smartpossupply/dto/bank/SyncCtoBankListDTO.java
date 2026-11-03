package uz.uzkassa.smartpossupply.dto.bank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SyncCtoBankListDTO extends CommonDTO {

    String code; //MFO

    String tin;

    String parentCode;

    String categoryCode; //Code

    Long regionCode;

    Long districtCode;

    String street;

    String house;

    String apartment;
}
