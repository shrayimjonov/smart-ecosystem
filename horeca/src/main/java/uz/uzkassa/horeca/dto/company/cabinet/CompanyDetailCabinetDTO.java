package uz.uzkassa.horeca.dto.company.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.activityttype.ActivityTypeDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.enums.BusinessType;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyDetailCabinetDTO extends CompanyCommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3090223L;

    String brand;

    BusinessType businessType;

    FileDTO logo;

    FileDTO backgroundPhoto;

    ActivityTypeDTO activityType;

    CommonDTO owner;

    CommonDTO manager;

    CommonDTO director;

    String phone;

    String oked;

    String wifiPassword;
}
