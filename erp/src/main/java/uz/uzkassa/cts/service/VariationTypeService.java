package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.inventory.VariationType;
import uz.uzkassa.cts.dto.variationtype.VariationTypeDTO;
import uz.uzkassa.cts.dto.variationtype.VariationTypeLookUpDTO;
import uz.uzkassa.cts.dto.variationtype.admin.VariationTypeDetailAdminDTO;
import uz.uzkassa.cts.dto.variationtype.admin.VariationTypeListAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface VariationTypeService extends BaseService<VariationType, Long> {

    Long create(VariationTypeDTO variationTypeDTO);

    Long update(Long id, VariationTypeDTO variationTypeDTO);

    VariationTypeDetailAdminDTO getForAdmin(Long id);

    Page<VariationTypeListAdminDTO> getListForAdmin(BaseFilter filter);

    List<VariationTypeLookUpDTO> lookUp(BaseFilter filter);
}
