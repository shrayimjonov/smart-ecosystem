package uz.uzkassa.smartpos.repository.postgres.company;

import org.springframework.data.domain.Page;
import uz.uzkassa.smartpos.dto.AdminBranchDTO;
import uz.uzkassa.smartpos.dto.BranchSelectItem;
import uz.uzkassa.smartpos.dto.branch.BranchListDTO;
import uz.uzkassa.smartpos.dto.branch.BranchListMapDTO;
import uz.uzkassa.smartpos.dto.filter.CompanyFilter;
import uz.uzkassa.smartpos.filter.BranchFilter;

import java.util.List;

public interface BranchRepositoryCustom {

    Page<BranchListDTO> findAllByFilter(BranchFilter filter);

    List<BranchListMapDTO> getBranchesForMap(CompanyFilter filter);

    Page<AdminBranchDTO> findAdminBranches(CompanyFilter filter, boolean forExcel);

    List<BranchSelectItem> lookup(BranchFilter filter);

}
