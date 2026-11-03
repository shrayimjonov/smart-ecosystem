package uz.uzkassa.smartpossupply.dto.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.billingpermission.PermissionDetailDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.CompanyType;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.UserStatus;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrentUserCabinetDTO extends CurrentUserBaseDTO {

    CompanyBaseDTO company;

    CompanyType companyType;

    boolean hasContract;

    String userTin;

    UserStatus status;

    ProductGroup productGroup;

    Set<String> permissions;

    CommonDTO activityType;

    boolean inMarketplace;

    @Deprecated
    boolean oneCEnabled;

    List<PermissionDetailDTO> permissionDetails;

    Long parentCompanyId;
}
