package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.CompanyModel;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.device.CompanyModelDTO;

import java.util.List;

public interface CompanyModelService extends BaseService<CompanyModel, Long> {

    void add(CompanyModelDTO companyModelDTO);

    List<CommonDTO> getByCompany(Long companyId);

    List<CommonDTO> getItems();
}
