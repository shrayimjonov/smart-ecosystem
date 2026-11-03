package uz.uzkassa.smartpossupply.dto.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrentUserMobileDTO extends CurrentUserBaseDTO {

    CommonDTO activityType;

    CompanyBaseDTO company;

    Set<String> permissions;
}
