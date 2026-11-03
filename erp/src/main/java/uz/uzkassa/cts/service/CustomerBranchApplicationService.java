package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.CustomerBranchApplication;
import uz.uzkassa.cts.dto.customerbranchapplication.admin.CustomerBranchApplicationDetailAdminDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.admin.CustomerBranchApplicationListAdminDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.base.CustomerBranchApplicationDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.base.CustomerBranchApplicationUpdateDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.cabinet.CustomerBranchApplicationDetailCabinetDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.cabinet.CustomerBranchApplicationListCabinetDTO;
import uz.uzkassa.cts.filter.BaseFilter;

public interface CustomerBranchApplicationService extends BaseService<CustomerBranchApplication, Long> {

    Page<CustomerBranchApplicationListAdminDTO> getListForAdmin(BaseFilter filter);

    Page<CustomerBranchApplicationListCabinetDTO> getListForCabinet(BaseFilter filter);

    Long create(CustomerBranchApplicationDTO customerBranchApplicationDTO);

    Long update(Long id, CustomerBranchApplicationDTO customerBranchApplicationDTO);

    CustomerBranchApplicationDetailCabinetDTO getForCabinet(Long id);

    CustomerBranchApplicationDetailAdminDTO getForAdmin(Long id);

    void updateStatus(Long id, CustomerBranchApplicationUpdateDTO dto);
}
