package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.inventory.Category;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.category.CategoryDTO;
import uz.uzkassa.cts.dto.category.admin.CategoryDetailAdminDTO;
import uz.uzkassa.cts.dto.category.admin.CategoryListAdminDTO;
import uz.uzkassa.cts.dto.category.admin.CategoryTreeAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface CategoryService extends BaseService<Category, Long> {

    Long create(CategoryDTO categoryDTO);

    Long update(Long id, CategoryDTO categoryDTO);

    CategoryDetailAdminDTO getForAdmin(Long id);

    Page<CategoryListAdminDTO> getListForAdmin(BaseFilter filter);

    List<CommonDTO> lookUp(BaseFilter filter);

    List<CategoryTreeAdminDTO> treeList(BaseFilter filter);
}
