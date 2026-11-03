package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.inventory.Brand;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface BrandService extends BaseService<Brand, Long> {

    Long create(CommonDTO brandDTO);

    Long update(Long id, CommonDTO brandDTO);

    CommonDTO get(Long id);

    Page<CommonDTO> getList(BaseFilter filter);

    List<CommonDTO> lookUp(BaseFilter filter);
}
