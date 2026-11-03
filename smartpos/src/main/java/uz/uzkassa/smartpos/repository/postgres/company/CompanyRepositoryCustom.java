package uz.uzkassa.smartpos.repository.postgres.company;

import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.CurrentBranchesAndTerminalsDTO;
import uz.uzkassa.smartpos.dto.filter.CompanySpecificationFilter;

import java.util.List;

public interface CompanyRepositoryCustom {

    CurrentBranchesAndTerminalsDTO getCurrentBranchesAndTerminalsCountByCompany(Long companyId);

    List<CommonDTO> search(String searchKey);

    List<CommonDTO> expiredCompanies();

    List<CompanyBaseDTO> companyLookup(CompanySpecificationFilter filter);
}
