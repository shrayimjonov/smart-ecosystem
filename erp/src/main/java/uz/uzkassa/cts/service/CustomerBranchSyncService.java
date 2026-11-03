package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.dto.sync.customerbranch.CustomerBranchSyncDTO;
import uz.uzkassa.cts.enums.RelationType;
import uz.uzkassa.cts.filter.CustomerBranchFilter;

public interface CustomerBranchSyncService extends BaseService<CustomerBranch, Long> {

    Page<CustomerBranchSyncDTO> getList(CustomerBranchFilter filter);

    CustomerBranchSyncDTO get(Long id);

    Long sync(CustomerBranchSyncDTO customerSyncDTO);

    Long sync(Long id, RelationType relationType);
}
