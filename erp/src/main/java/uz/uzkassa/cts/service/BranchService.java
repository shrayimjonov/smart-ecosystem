package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.CommonTreeDTO;
import uz.uzkassa.cts.dto.branch.BranchDTO;
import uz.uzkassa.cts.dto.branch.admin.BranchDetailAdminDTO;
import uz.uzkassa.cts.dto.branch.admin.BranchListAdminDTO;
import uz.uzkassa.cts.dto.branch.admin.BranchStatisticsAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface BranchService extends BaseService<Branch, Long> {

    Long create(BranchDTO branchDTO);

    Long update(Long id, BranchDTO branchDTO);

    BranchDetailAdminDTO get(Long id);

    Page<BranchListAdminDTO> getListForAdmin(BaseFilter filter);

    List<CommonDTO> lookUp(BaseFilter filter);

    void updateStatus(Long id, CommonStatus status);

    List<CommonTreeDTO> treeList(BaseFilter filter);

    BranchStatisticsAdminDTO getStatistics(Long id);
}
