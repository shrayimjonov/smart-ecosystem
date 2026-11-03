package uz.uzkassa.horeca.dto.company.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.activityttype.ActivityTypeDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.enums.BusinessType;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyDetailAdminDTO extends CompanyCommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5645L;

    BusinessType businessType;

    ActivityTypeDTO activityType;

    CommonDTO owner;

    CommonDTO director;

    String phone;

    String oked;

    String wifiPassword;
}
