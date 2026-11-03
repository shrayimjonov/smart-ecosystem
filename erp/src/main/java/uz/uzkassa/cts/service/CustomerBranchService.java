package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customer.admin.CustomerBranchStatisticsAdminDTO;
import uz.uzkassa.cts.dto.customerbranch.CustomerBranchDTO;
import uz.uzkassa.cts.dto.customerbranch.admin.CustomerBranchDetailAdminDTO;
import uz.uzkassa.cts.dto.customerbranch.admin.CustomerBranchListAdminDTO;
import uz.uzkassa.cts.dto.customerbranch.cabinet.CustomerBranchDetailCabinetDTO;
import uz.uzkassa.cts.dto.customerbranch.cabinet.CustomerBranchListCabinetDTO;
import uz.uzkassa.cts.enums.CustomerBranchStatus;
import uz.uzkassa.cts.filter.CustomerBranchFilter;

import java.util.List;

public interface CustomerBranchService extends BaseService<CustomerBranch, Long> {

    Long create(CustomerBranchDTO customerBranchDTO);

    Long update(Long id, CustomerBranchDTO customerBranchDTO);

    void updateStatus(Long id, CustomerBranchStatus status, String reason);

    List<CommonDTO> lookUp(CustomerBranchFilter filter);

    CustomerBranchDetailAdminDTO getForAdmin(Long id);

    CustomerBranchDetailCabinetDTO getForCabinet(Long id);

    Page<CustomerBranchListAdminDTO> getListForAdmin(CustomerBranchFilter filter);

    Page<CustomerBranchListCabinetDTO> getListForCabinet(CustomerBranchFilter filter);

    CustomerBranchStatisticsAdminDTO getStatistics(Long id);
}
