package uz.uzkassa.cts.dto.fm.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class CustomerFiscalModuleListAdminDTO extends BranchFiscalModuleListAdminDTO implements Serializable {

    CompanyCommonDTO customer;
}
