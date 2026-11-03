package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.ofd.ConfirmCustomerBranchDTO;
import uz.uzkassa.cts.filter.CustomerBranchFilter;

import java.util.List;

public interface OfdCustomerBranchService extends BaseService<CustomerBranch, Long> {

    String confirm(ConfirmCustomerBranchDTO confirmCustomerBranchDTO);

    List<CommonDTO> lookUp(CustomerBranchFilter filter);
}
