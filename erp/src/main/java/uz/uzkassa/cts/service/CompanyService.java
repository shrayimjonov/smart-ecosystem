package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.dto.company.base.CompanyCheckDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.company.admin.CompanyDetailAdminDTO;
import uz.uzkassa.cts.dto.company.admin.CompanyListAdminDTO;
import uz.uzkassa.cts.dto.company.base.CompanyDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface CompanyService extends BaseService<Company, Long> {

    Long create(CompanyDTO companyDTO);

    Long update(Long id, CompanyDTO companyDTO);

    CompanyDetailAdminDTO get(Long id);

    Page<CompanyListAdminDTO> getListForAdmin(BaseFilter filter);

    List<CompanyCommonDTO> lookUp(BaseFilter filter);

    void updateStatus(Long id, CommonStatus status);

    CompanyCheckDTO check(String tin);
}
