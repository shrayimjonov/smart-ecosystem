package uz.uzkassa.smartpos.repository.postgres.catalog;

import uz.uzkassa.smartpos.dto.CategoryDTO;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.catalog.CategoryResult;
import uz.uzkassa.smartpos.dto.catalog.CategoryTreeDto;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;

import java.util.List;

public interface CategoryBranchRepositoryCustom {
    List<CategoryResult> findAll(Long branchId, Long cityId);

    List<CategoryTreeDto> findAllWithoutProductsCount(Long branchId);

    List<CategoryDTO> findEnabledCategories(Long branchId, Long cityId);

    List<CommonDTO> choose(BaseFilter filter);
}
