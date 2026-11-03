package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.Dependent;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.dependent.DependentDTO;
import uz.uzkassa.cts.dto.dependent.DependentDetailAdminDTO;
import uz.uzkassa.cts.dto.dependent.DependentListAdminDTO;
import uz.uzkassa.cts.filter.DependentFilter;

import java.util.List;

public interface DependentService extends BaseService<Dependent, Long> {

    Long create(DependentDTO dependentDTO);

    Long update(Long id, DependentDTO dependentDTO);

    DependentDetailAdminDTO getForAdmin(Long id);

    Page<DependentListAdminDTO> getListForAdmin(DependentFilter filter);

    List<CommonDTO> lookUp(DependentFilter filter);

}
