package uz.uzkassa.cts.dto.position.admin;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.LocalizedDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PositionListAdminDTO extends LocalizedDTO {

    CommonDTO parent;

    CommonStatus status;

    CompanyCommonDTO company;
}
