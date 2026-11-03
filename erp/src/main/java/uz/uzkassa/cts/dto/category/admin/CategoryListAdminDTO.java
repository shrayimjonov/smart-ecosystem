package uz.uzkassa.cts.dto.category.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryListAdminDTO extends CommonDTO implements Serializable {

    CommonDTO parent;

    CompanyCommonDTO company;
}
