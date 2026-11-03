package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.inventory.VariationOption;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.variationoption.VariationOptionDTO;
import uz.uzkassa.cts.dto.variationoption.admin.VariationOptionDetailAdminDTO;
import uz.uzkassa.cts.dto.variationoption.admin.VariationOptionListAdminDTO;
import uz.uzkassa.cts.filter.VariationOptionFilter;

import java.util.List;

public interface VariationOptionService extends BaseService<VariationOption, Long> {

    Long create(VariationOptionDTO variationOptionDTO);

    Long update(Long id, VariationOptionDTO variationOptionDTO);

    VariationOptionDetailAdminDTO getForAdmin(Long id);

    Page<VariationOptionListAdminDTO> getListForAdmin(VariationOptionFilter filter);

    List<CommonDTO> lookUp(VariationOptionFilter filter);
}
